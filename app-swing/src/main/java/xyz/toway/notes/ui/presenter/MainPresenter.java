package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.ui.view.GeneralView;

import java.io.File;
import java.util.List;

import static xyz.toway.notes.ui.Main.context;

public class MainPresenter implements IMainPresenter {
    private static final String APP_USER = "app_user";

    private GeneralView view;

    @Override
    public void setView(GeneralView view) {
        this.view = view;
    }

    @Override
    public GeneralView getView() {
        return view;
    }

    @Override
    public void init() {
        // get settings to Map<String, Object> settings
        var settings = context.getSettingsService().getStoredSettings();
        view.applySettings(settings);

        // try to open existing database
        settings.databaseFilePath().ifPresent(path -> {
            openDatabase(path);
            // create default tab
            view.openDocument(null);
        });
    }

    @Override
    public void destroy() {
        try {
            context.getDatabaseService().closeDatabase();
        } catch (Exception e) {
            view.showErrorMessage("Error closing database: " + e.getMessage());
        }
    }

    @Override
    public void saveSettingsFlag(String name, boolean flag) {
        switch (name) {
            case "statusBarVisible" -> context.getSettingsService().getSettingsRepo().setStatusBarVisible(flag);
            case "restoreLastSession" -> context.getSettingsService().getSettingsRepo().setRestoreLastSession(flag);
        }
    }

    private void openDatabase(String path) {

        try {
            //if (!context.getDatabaseService().databaseFileIsValid(path)) {
            //    throw new Exception();
            //}
            // todo: ask for password dialog
            //var password = view.requestPassword();
            //if (password == null) {
            //    view.showNotification("Open notes file cancelled.", UIManager.getColor("TitlePane.inactiveForeground"));
            //    return;
            //}
            context.getDatabaseService().initDatabase(path, APP_USER, "test"); //todo password
            view.showNotification("Notes file opened: " + path);

            // load last opened docs if setting enabled
            var lastOpenedDocs = context.getNoteService().getLastOpenedDocs();
            if (context.getSettingsService().getSettingsRepo().getRestoreLastSession() && !lastOpenedDocs.isEmpty()) {
                lastOpenedDocs.forEach(model -> view.openDocument(model));
            }
            System.out.println("Count of last opened docs: " + lastOpenedDocs.size());
        } catch (Exception e) {
            view.showErrorMessage("Failed to open file: " + path);
            view.showNotification("Failed to open file: " + path);
        }
    }

    public ContentModel save(ContentModel model) {
        return context.getNoteService().save(model);
    }

    public void saveOpenedDocs(List<String> ids) {
        context.getNoteService().saveLastOpenedDocs(ids);
    }

    public void saveTextFile(String text, File selectedFile) {
        try {
            context.getNoteService().saveNoteAsTextFile(text, selectedFile);
            view.showNotification("Note exported to: " + selectedFile.getAbsolutePath());
        } catch (Exception e) {
            view.showErrorMessage("Failed to export note: " + e.getMessage());
        }
    }
}
