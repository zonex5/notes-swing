package xyz.toway.notes.ui.view.dialogs;

import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class InputValueDialog {

    /**
     * Show input dialog to get a string value from user. If user inputs a non-empty value, call okAction with the value.
     *
     * @param parent   the parent component of the dialog
     * @param title    the title/message of the dialog
     * @param value    the initial value to show in the input field
     * @param okAction the action to perform with the input value if it's valid
     */
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
