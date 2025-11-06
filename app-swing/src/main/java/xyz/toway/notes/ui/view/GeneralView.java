package xyz.toway.notes.ui.view;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.StoredSettings;

import javax.swing.*;

import static xyz.toway.notes.ui.Main.icon;

public interface GeneralView {

    default Object requestData(String key) {
        return null;
    }

    void applySettings(StoredSettings settings);

    void showErrorMessage(String message);

    void showNotification(String message);

    void openDocument(ContentModel contentModel);

    default JButton createButton(String tooltip, String iconPath, Runnable action) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setIcon(icon(iconPath));
        button.addActionListener(e -> action.run());
        return button;
    }
}
