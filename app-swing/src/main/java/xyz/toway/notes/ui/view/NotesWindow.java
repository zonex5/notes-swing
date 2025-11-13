package xyz.toway.notes.ui.view;

import lombok.Getter;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.GroupModel;
import xyz.toway.notes.ui.presenter.INotesPresenter;
import xyz.toway.notes.ui.presenter.NotesPresenter;
import xyz.toway.notes.ui.view.components.DnDTree;
import xyz.toway.notes.ui.view.components.GroupsTree;
import xyz.toway.notes.ui.view.components.OpenItemCellRenderer;
import xyz.toway.notes.ui.view.dialogs.InputValueDialog;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static xyz.toway.notes.ui.Main.icon;

public class NotesWindow extends ToolWindow implements INotesView {

    private INotesPresenter<GroupModel> presenter;

    private List<JButton> noteButtons;
    private DefaultListModel<ContentModel> model;
    private JList<ContentModel> noteList;
    private GroupsTree groupsTree;

    @Getter
    private ContentModel result;

    public NotesWindow(Window owner) {
        super(owner);
        setTitle("Notes Manager");
        setModal(true);
    }

    @Override
    protected void initialization() {
        model = new DefaultListModel<>();
        noteList = new JList<>(model);
        noteList.setDragEnabled(true);
        noteList.setTransferHandler(new NoteListTransferHandler(noteList));
        noteButtons = new ArrayList<>();
        presenter = new NotesPresenter();
        presenter.setView(this);
    }

    private JButton createButton(String tooltip, String iconPath, Runnable action) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setIcon(icon(iconPath));
        button.addActionListener(e -> action.run());
        return button;
    }

    @Override
    protected JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();
        menuBar.add(Box.createHorizontalGlue());

        // create toolbar buttons
        JButton refreshButton = createButton("Refresh list", "/icons/ui/refresh.svg", this::loadGroups);
        JButton renameButton = createButton("Rename note", "/icons/ui/doc-edit.svg", this::renameSelectedNote);
        JButton deleteButton = createButton("Delete note", "/icons/ui/delete.svg", this::deleteSelectedNote);

        JToolBar toolBar = new JToolBar();
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        menuBar.add(toolBar);

        noteButtons = new ArrayList<>();
        noteButtons.add(renameButton);
        noteButtons.add(deleteButton);
        noteButtons.forEach(button -> button.setEnabled(false));
        noteButtons.forEach(toolBar::add);
        toolBar.addSeparator();

        return menuBar;
    }

    @Override
    protected JComponent createContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        panel.setPreferredSize(new Dimension(700, 450));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel leftPanel = createGroupsPanel();
        leftPanel.setMinimumSize(new Dimension(200, 0));
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = createNoteListPanel();
        rightPanel.setMinimumSize(new Dimension(250, 0));
        splitPane.setRightComponent(rightPanel);

        JPanel bottomPanel = createBottomPanel();
        bottomPanel.setPreferredSize(new Dimension(0, 20));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadGroups();

        return panel;
    }

    @Override
    public void setNotes(List<ContentModel> notes) {
        model.clear();
        model.addAll(notes);
        noteList.repaint();
    }

    @Override
    public void setGroups(List<GroupModel> groups) {
        groupsTree.rebuild(groups);
        if (groupsTree.getSelectionPath() == null) {
            groupsTree.setSelectionRow(0);
        }
    }

    @Override
    public void refresh() {
        loadGroups();
    }

    private void loadGroups() {
        presenter.loadGroups();
    }

    private JPanel createGroupsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        groupsTree = new GroupsTree("All Notes");
        groupsTree.setExternalDropHandler(new NoteToGroupDropHandler());
        groupsTree.getSelectionModel().addTreeSelectionListener(e -> refreshListForCurrentSelection());
        groupsTree.setSelectionRow(0);
        panel.add(groupsTree);

        JPopupMenu rootMenu = new JPopupMenu();
        JMenuItem itemInfo = new JMenuItem("Add New Group", icon("/icons/ui/new-folder.svg"));
        itemInfo.addActionListener(e -> addNewGroup());
        rootMenu.add(itemInfo);

        // Add mouse listener
        groupsTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = groupsTree.getRowForLocation(e.getX(), e.getY());
                    TreePath path = groupsTree.getPathForLocation(e.getX(), e.getY());
                    if (row != -1 && path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        // Show menu only for root node
                        if (node.isRoot()) {
                            rootMenu.show(groupsTree, e.getX(), e.getY());
                        }
                    }
                }
            }
        });

        return panel;
    }

    private void refreshListForCurrentSelection() {
        var selectedNode = groupsTree.getSelectedTreeNode();

        if (selectedNode == null) return;

        // all
        if (selectedNode.isRoot()) {
            presenter.loadAllNotes();
            return;
        }

        // no group
        if (groupsTree.isUngroupedNodeSelected()) {
            presenter.loadOrphanNotes();
            return;
        }

        // by group
        if (!(selectedNode.getUserObject() instanceof GroupModel)) {
            return;
        }
        Set<String> allowedIds = collectGroupIds(selectedNode);
        presenter.loadNotes(allowedIds);

        revalidate();
        repaint();
    }

    private void addNewGroup() {
        InputValueDialog.show(this, "Input the name:", "New Group", name -> {
            presenter.addNewGroup(name);
        });
    }

    private JPanel createNoteListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteList.setCellRenderer(new OpenItemCellRenderer());
        panel.add(new JScrollPane(noteList), BorderLayout.CENTER);

        noteList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int index = noteList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        result = noteList.getModel().getElementAt(index);
                        dispose();
                    }
                }
            }
        });
        noteList.getSelectionModel()
                .addListSelectionListener(e -> noteButtons.forEach(
                        button -> button.setEnabled(!noteList.isSelectionEmpty())
                ));

        return panel;
    }

    private ContentModel getSelectedNote() {
        int index = noteList.getSelectedIndex();
        if (index >= 0) {
            return noteList.getModel().getElementAt(index);
        }
        return null;
    }

    private void deleteSelectedNote() {
        ContentModel selectedNote = getSelectedNote();
        if (selectedNote == null) {
            return;
        }

        // show confirm dialog
        int result = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete the note '" + selectedNote.getTitle() + "' ?", "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            model.removeElement(selectedNote);
            presenter.deleteNote(selectedNote);
            noteList.repaint();
        }
    }

    private void renameSelectedNote() {
        ContentModel selectedNote = getSelectedNote();
        if (selectedNote == null) {
            return;
        }

        InputValueDialog.show(this, "Enter new title for the note:", selectedNote.getTitle(), newTitle -> {
            selectedNote.setTitle(newTitle);
            presenter.saveNote(selectedNote);
            noteList.repaint();
        });
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        //panel.setBackground(Color.GREEN);
        //panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //panel.add(getSearchField());
        return panel;
    }

    private Set<String> collectGroupIds(DefaultMutableTreeNode node) {
        Set<String> ids = new HashSet<>();
        collectGroupIds(node, ids);
        return ids;
    }

    private void collectGroupIds(DefaultMutableTreeNode node, Set<String> ids) {
        if (node == null) {
            return;
        }
        Object value = node.getUserObject();
        if (value instanceof GroupModel groupModel && groupModel.getId() != null) {
            ids.add(groupModel.getId());
        }
        Enumeration<?> children = node.children();
        while (children.hasMoreElements()) {
            Object child = children.nextElement();
            if (child instanceof DefaultMutableTreeNode childNode) {
                collectGroupIds(childNode, ids);
            }
        }
    }

    private final class NoteToGroupDropHandler implements DnDTree.ExternalDropHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport support, DefaultMutableTreeNode target, int childIndex) {
            if (!support.isDataFlavorSupported(NoteListTransferHandler.NOTE_FLAVOR)) {
                return false;
            }
            if (target == null || childIndex != -1) {
                return false;
            }
            if (target == groupsTree.getUngroupedNode()) {
                return true;
            }
            return target.isRoot() || target.getUserObject() instanceof GroupModel;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support, DefaultMutableTreeNode target, int childIndex) {
            try {
                ContentModel note = (ContentModel) support.getTransferable().getTransferData(NoteListTransferHandler.NOTE_FLAVOR);
                int index = model.indexOf(note);
                String newGroupId = null;
                if (target.getUserObject() instanceof GroupModel groupModel) {
                    newGroupId = groupModel.getId();
                }
                if (Objects.equals(note.getGroupId(), newGroupId)) {
                    return false;
                }
                note.setGroupId(newGroupId);
                presenter.saveNote(note);
                if (index >= 0) {
                    model.setElementAt(note, index);
                }
                refreshListForCurrentSelection();
                noteList.clearSelection();
                noteList.repaint();
                return true;
            } catch (UnsupportedFlavorException | IOException e) {
                return false;
            }
        }
    }

    private static final class NoteListTransferHandler extends TransferHandler {
        static final DataFlavor NOTE_FLAVOR;

        static {
            try {
                NOTE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + ContentModel.class.getName());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Unable to create note data flavor", e);
            }
        }

        private final JList<ContentModel> list;

        private NoteListTransferHandler(JList<ContentModel> list) {
            this.list = list;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            ContentModel selected = list.getSelectedValue();
            if (selected == null) {
                return null;
            }
            return new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{NOTE_FLAVOR};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return NOTE_FLAVOR.equals(flavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
                    if (!isDataFlavorSupported(flavor)) {
                        throw new UnsupportedFlavorException(flavor);
                    }
                    return selected;
                }
            };
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }
    }
}
