package xyz.toway.notes.ui.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import lombok.Getter;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.ui.presenter.INotesPresenter;
import xyz.toway.notes.ui.presenter.NotesPresenter;
import xyz.toway.notes.ui.view.components.OpenItemCellRenderer;
import xyz.toway.notes.ui.view.dialogs.InputValueDialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static xyz.toway.notes.ui.Main.icon;

public class NotesWindow extends ToolWindow implements GeneralView {

    private INotesPresenter presenter;

    private List<JButton> noteButtons;
    private DefaultListModel<ContentModel> model;
    private JList<ContentModel> noteList;

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
        noteButtons = new ArrayList<>();
        presenter = new NotesPresenter();
        presenter.setView(this);
        presenter.setViewData();
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
        JButton refreshButton = createButton("Refresh list", "/icons/refresh.svg", () -> presenter.setViewData()); // do not replace with method reference
        JButton renameButton = createButton("Rename note", "/icons/doc-edit.svg", this::renameSelectedNote);
        JButton deleteButton = createButton("Delete note", "/icons/delete.svg", this::deleteSelectedNote);

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
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setMinimumSize(new Dimension(200, 0));
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = createNoteListPanel();
        rightPanel.setMinimumSize(new Dimension(250, 0));
        splitPane.setRightComponent(rightPanel);

        JPanel bottomPanel = createBottomPanel();
        bottomPanel.setPreferredSize(new Dimension(0, 20));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void setData(String key, Object value) {
        if (key.equals("notesList")) {
            model.clear();
            if (value instanceof List<?> notes) {
                List<ContentModel> list = notes.stream()
                        .filter(obj -> obj instanceof ContentModel)
                        .map(obj -> (ContentModel) obj)
                        .toList();
                model.addAll(list);
            }
        }
    }

    private JPanel createGroupsPanel() {
        JPanel panel = new JPanel();

        return panel;
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

    private JTextField getSearchField() {
        JButton searchHistoryButton = new JButton(new FlatSearchWithHistoryIcon(true));
        searchHistoryButton.setToolTipText("Search History");
        searchHistoryButton.addActionListener(e -> {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add("(empty)");
            popupMenu.show(searchHistoryButton, 0, searchHistoryButton.getHeight());
        });

        JTextField searchField = new JTextField();
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, searchHistoryButton);

        searchField.setBackground(Color.CYAN);

        /*Dimension pref = searchField.getPreferredSize();
        pref.width = 250;
        searchField.setPreferredSize(pref);
        searchField.setMaximumSize(pref);*/

        return searchField;
    }
}
