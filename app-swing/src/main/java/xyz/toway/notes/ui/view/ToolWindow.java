package xyz.toway.notes.ui.view;

import javax.swing.*;
import java.awt.*;

public abstract class ToolWindow extends JDialog {

    public ToolWindow(Window owner) {
        super(owner);
        setType(Type.NORMAL);

        setModal(false);
        setAlwaysOnTop(false);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        initialization();

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.add(createContent(), BorderLayout.CENTER);
        setContentPane(content);
        pack();

        setJMenuBar(createMenuBar());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                reposition();
            }
        });
    }

    protected abstract void initialization();

    protected abstract JComponent createContent();

    protected abstract JMenuBar createMenuBar();

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
