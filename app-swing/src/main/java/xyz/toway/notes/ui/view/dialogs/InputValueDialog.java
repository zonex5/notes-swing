package xyz.toway.notes.ui.view.dialogs;

import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class InputValueDialog {

    public static void show(Component parent, String title, String value, @NonNull Consumer<String> okAction) {
        String newValue = JOptionPane.showInputDialog(
                parent,
                title,
                value
        );
        if (newValue != null && !newValue.trim().isEmpty()) {
            okAction.accept(newValue.trim());
        }
    }
}
