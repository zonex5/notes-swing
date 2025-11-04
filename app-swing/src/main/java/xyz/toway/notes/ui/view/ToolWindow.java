package xyz.toway.notes.ui.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public abstract class ToolWindow extends JDialog {

    public ToolWindow(Window owner) {
        super(owner);
        setType(Type.POPUP);

        setModal(false);
        setAlwaysOnTop(true);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(Color.LIGHT_GRAY);

        content.add(getContent(), BorderLayout.CENTER);

        setContentPane(content);
        pack();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                reposition();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                super.windowDeactivated(e);
                dispose();
            }
        });
    }

    protected abstract JComponent getContent();

    private void reposition() {
        Window owner = getOwner();
        if (owner != null) {
            Point p = owner.getLocationOnScreen();
            int x = p.x + (owner.getWidth() - getWidth()) / 2;
            int y = p.y + (owner.getHeight() - getHeight()) / 2;
            setLocation(x, y);
        } else {
            setLocationRelativeTo(null);
        }
    }

    void showWindow() {
        setVisible(true);
    }
}
