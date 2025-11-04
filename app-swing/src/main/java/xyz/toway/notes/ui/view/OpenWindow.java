package xyz.toway.notes.ui.view;

import xyz.toway.notes.domain.model.StoredSettings;
import xyz.toway.notes.ui.presenter.OpenPresenter;

import javax.swing.*;
import java.awt.*;

public class OpenWindow extends ToolWindow implements GeneralView<OpenPresenter> {

    private OpenPresenter presenter;

    public OpenWindow(Window owner) {
        super(owner);
        setTitle("Notes Manager");

        presenter = new OpenPresenter();
        presenter.setView(this);
    }

    @Override
    protected JComponent getContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(700, 450));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.CYAN);
        leftPanel.setMinimumSize(new Dimension(200, 0));
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.MAGENTA);
        rightPanel.setMinimumSize(new Dimension(250, 0));
        splitPane.setRightComponent(rightPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.GREEN);
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
}
