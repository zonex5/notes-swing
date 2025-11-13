package xyz.toway.notes.ui.view;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.StoredSettings;

public interface GeneralView {

    default Object requestData(String key) {
        return null;
    }

    default void setData(String key, Object value) {
    }

    default void applySettings(StoredSettings settings) {
    }

    default void showErrorMessage(String message) {
    }

    default void showNotification(String message) {
    }

    default void openDocument(ContentModel contentModel) {
    }

    default void refresh() {
    }
}
