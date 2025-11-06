package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.ui.view.GeneralView;

import java.io.File;
import java.util.List;

public interface GeneralPresenter {

    default void init() {
    }

    default void destroy() {
    }

    void setView(GeneralView view);

    GeneralView getView();

    void saveSettingsFlag(String name, boolean flag);

    ContentModel save(ContentModel model);

    void saveOpenedDocs(List<String> ids);

    void saveTextFile(String text, File selectedFile);

    String validateFileName(String filename);
}
