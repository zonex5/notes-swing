package xyz.toway.notes.ui.view.dialogs;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;

public final class PasswordInputDialog {

    public static String show(String title, String message) {
        while (true) {
            // Create password field
            JPasswordField passwordField = new JPasswordField();
            JLabel label = new JLabel(message);

            // Build layout
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setPreferredSize(new Dimension(300, 45));

            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

            panel.add(label);
            panel.add(Box.createVerticalStrut(6));
            panel.add(passwordField);

            // set focus
            passwordField.addAncestorListener(new AncestorListener() {
                @Override
                public void ancestorAdded(AncestorEvent e) {
                    // Request focus after component is realized
                    SwingUtilities.invokeLater(passwordField::requestFocusInWindow);
                }

                @Override
                public void ancestorRemoved(AncestorEvent e) {
                }

                @Override
                public void ancestorMoved(AncestorEvent e) {
                }
            });

            int option = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    title,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    UIManager.getIcon("OptionPane.informationIcon")
            );

            if (option == JOptionPane.OK_OPTION) {
                String password = new String(passwordField.getPassword());

                // Check that password is not empty
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Password cannot be empty.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }

                return password;
            } else {
                return null;
            }
        }
    }

    public static String show(String title, String firstMessage, String secondMessage) {
        while (true) {
            // Create password fields
            JPasswordField passwordField = new JPasswordField();
            JPasswordField confirmField = new JPasswordField();

            JLabel label = new JLabel(firstMessage);
            JLabel confirmLabel = new JLabel(secondMessage);

            // Build layout
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setPreferredSize(new Dimension(300, 100));

            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
            confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);

            // set focus
            passwordField.addAncestorListener(new AncestorListener() {
                @Override
                public void ancestorAdded(AncestorEvent e) {
                    // Request focus after component is realized
                    SwingUtilities.invokeLater(() -> passwordField.requestFocusInWindow());
                }

                @Override
                public void ancestorRemoved(AncestorEvent e) {
                }

                @Override
                public void ancestorMoved(AncestorEvent e) {
                }
            });

            panel.add(label);
            panel.add(Box.createVerticalStrut(6));
            panel.add(passwordField);
            panel.add(Box.createVerticalStrut(10));
            panel.add(confirmLabel);
            panel.add(Box.createVerticalStrut(6));
            panel.add(confirmField);

            int option = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    title,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    UIManager.getIcon("OptionPane.informationIcon")
            );

            if (option == JOptionPane.OK_OPTION) {
                String password = new String(passwordField.getPassword());
                String confirm = new String(confirmField.getPassword());

                // Check that password is not empty
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Password cannot be empty.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }

                // Check that passwords match
                if (!password.equals(confirm)) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Passwords do not match.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }

                return password;
            } else {
                return null;
            }
        }
    }
}
