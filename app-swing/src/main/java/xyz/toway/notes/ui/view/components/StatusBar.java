package xyz.toway.notes.ui.view.components;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private final JLabel leftLabel;
    private final JLabel centerLabel;
    private final JLabel rightLabel;

    private final JPanel leftPanel;
    private final JPanel centerPanel;
    private final JPanel rightPanel;

    public StatusBar() {
        setLayout(new BorderLayout(10, 0));
        setPreferredSize(new Dimension(getWidth(), 22));

        leftLabel = new JLabel();
        centerLabel = new JLabel();
        rightLabel = new JLabel();

        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leftPanel.add(leftLabel);

        centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        centerPanel.add(centerLabel);

        rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        rightPanel.add(rightLabel);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    public void setLeftText(String text) {
        leftLabel.setText(text);
        revalidate();
        repaint();
    }

    public void setCenterText(String text) {
        centerLabel.setText(text);
    }

    public void setRightText(String text) {
        rightLabel.setText(text);
    }

    public void setLeftPanel(JPanel panel) {
        remove(leftPanel);
        add(panel, BorderLayout.WEST);
        revalidate();
        repaint();
    }

    public void setCenterPanel(JPanel panel) {
        remove(centerPanel);
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void setRightPanel(JPanel panel) {
        remove(rightPanel);
        add(panel, BorderLayout.EAST);
        revalidate();
        repaint();
    }

    public void clear() {
        leftLabel.setText("");
        centerLabel.setText("");
        rightLabel.setText("");
    }
}
