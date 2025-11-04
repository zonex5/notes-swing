package xyz.toway.notes.ui.view;

import lombok.Getter;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.StoredSettings;
import xyz.toway.notes.ui.presenter.NotesManagerPresenter;
import xyz.toway.notes.ui.view.components.OpenItemCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NotesManagerWindow extends ToolWindow implements GeneralView<NotesManagerPresenter> {

    private NotesManagerPresenter presenter;
    private JList<ContentModel> noteList;
    private List<JButton> noteButtons;

    @Getter
    private DefaultListModel<ContentModel> model;

    @Getter
    private ContentModel result;

    public NotesManagerWindow(Window owner) {
        super(owner);
        setTitle("Notes Manager");
        setModal(true);

        presenter = new NotesManagerPresenter();
        presenter.setView(this);
        presenter.refreshNotesList();
    }

    @Override
    protected JMenuBar createMenuBar() {
        var menuBar = new JMenuBar();
        menuBar.add(Box.createHorizontalGlue());

        // create toolbar buttons
        JButton refreshButton = createButton("Refresh list", "/icons/refresh.svg", () -> presenter.refreshNotesList());
        JButton renameButton = createButton("Rename note", "/icons/rename.svg", this::renameSelectedNote);
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
        panel.setLayout(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(700, 450));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setMinimumSize(new Dimension(200, 0));
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = createNoteListPanel();
        rightPanel.setMinimumSize(new Dimension(250, 0));
        splitPane.setRightComponent(rightPanel);

        JPanel bottomPanel = createBottomPanel();
        bottomPanel.setPreferredSize(new Dimension(700, 40));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    public NotesManagerPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void applyUISettings(StoredSettings settings) {

    }

    private JPanel createNoteListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        model = new DefaultListModel<>();
        noteList = new JList<>(model);
        noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteList.setCellRenderer(new OpenItemCellRenderer());

        JScrollPane scrollPane = new JScrollPane(noteList);
        panel.add(scrollPane, BorderLayout.CENTER);

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
        noteList.getSelectionModel().addListSelectionListener(e ->
                noteButtons.forEach(button -> button.setEnabled(!noteList.isSelectionEmpty())
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
                "Are you sure you want to delete the note '" + selectedNote.getTitle() + "' ?",
                "Confirm Deletion",
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

        String newTitle = JOptionPane.showInputDialog(
                this,
                "Enter new title for the note:",
                selectedNote.getTitle()
        );
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            selectedNote.setTitle(newTitle.trim());
            presenter.saveNote(selectedNote);
            noteList.repaint();
        }
    }

    private JPanel createBottomPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(new JButton("New Note"));
        buttonsPanel.add(new JButton("Delete Note"));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setPreferredSize(new Dimension(200, 30));
        searchPanel.setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(buttonsPanel, BorderLayout.EAST);

        return panel;
    }
}
