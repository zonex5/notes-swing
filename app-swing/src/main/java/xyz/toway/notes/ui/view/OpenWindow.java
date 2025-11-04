package xyz.toway.notes.ui.view;

import javax.swing.*;
import java.awt.*;

public class OpenWindow extends ToolWindow {

    public OpenWindow(Window owner) {
        super(owner);
        setTitle("Open the note");
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
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.MAGENTA);
        splitPane.setRightComponent(rightPanel);



        return panel;
    }
}
