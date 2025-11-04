package xyz.toway.notes.ui.view;

import lombok.Getter;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.StoredSettings;
import xyz.toway.notes.ui.presenter.OpenPresenter;
import xyz.toway.notes.ui.view.components.OpenItemCellRenderer;

import javax.swing.*;
import java.awt.*;

public class NotesManagerWindow extends ToolWindow implements GeneralView<OpenPresenter> {

    private OpenPresenter presenter;
    private DefaultListModel<ContentModel> model;

    @Getter
    private ContentModel result;

    public NotesManagerWindow(Window owner) {
        super(owner);
        setTitle("Notes Manager");
        setModal(true);

        presenter = new OpenPresenter();
        presenter.setView(this);

        model.addAll(presenter.getNotesList());
    }

    @Override
    protected JComponent getContent() {
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
    public OpenPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void applyUISettings(StoredSettings settings) {

    }

    private JPanel createNoteListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        model = new DefaultListModel<>();
        JList<ContentModel> noteList = new JList<>(model);
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

        return panel;
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
