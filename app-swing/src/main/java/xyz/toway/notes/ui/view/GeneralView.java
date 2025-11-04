package xyz.toway.notes.ui.view;

import xyz.toway.notes.domain.model.StoredSettings;
import xyz.toway.notes.ui.presenter.GeneralPresenter;

import javax.swing.*;

import static xyz.toway.notes.ui.Main.icon;

public interface GeneralView<P extends GeneralPresenter<?>> {

    P getPresenter();

    void applyUISettings(StoredSettings settings);

    default JButton createButton(String tooltip, String iconPath, Runnable action) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setIcon(icon(iconPath));
        button.addActionListener(e -> action.run());
        return button;
    }
}
