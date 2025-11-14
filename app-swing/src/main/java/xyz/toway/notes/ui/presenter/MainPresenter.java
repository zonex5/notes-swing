package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.port.SettingsRepository;
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
            if (context.getDatabaseService().databaseFileIsValid(path)) {
                // open file
                openDatabase(path);
            } else {
                view.setData("notes-file-problem", null);
                System.out.println("Last opened notes file is invalid: " + path);
            }
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
        context.getSettingsService().getSettingsRepo().setBooleanValue(name, flag);
    }

    @Override
    public void openDatabase(String path) {

        if (!context.getDatabaseService().databaseFileIsValid(path)) {
            view.showErrorMessage("Invalid notes file: " + path);
            return;
        }

        // ask for password dialog todo
        /*var password = view.requestData("password");
        if (password == null) {
            view.showNotification("Open notes file cancelled.");
            view.setData("notes-file-problem", null);
            return;
        }*/

        var password = "test";

        try {
            context.getDatabaseService().initDatabase(path, APP_USER, (String) password);
            view.showNotification("Notes file: " + path);
            view.setData("open-success", true);

            // load last opened docs if setting enabled
            if (context.getSettingsService().getSettingsRepo().getBooleanValue(SettingsRepository.RESTORE_LAST_SESSION)) {
                context.getNoteService().getLastOpenedDocs()
                        .forEach(model -> view.openDocument(model));
            }

            // create default tab
            view.openDocument(null);
        } catch (Exception e) {
            view.setData("notes-file-problem", null);
            view.showErrorMessage("Failed to open file: " + path);
            view.showNotification("Failed to open file: " + path);
        }
    }

    @Override
    public void createNewFile(String path) {
        context.getDatabaseService().closeDatabase();
        var password = view.requestData("newPassword");
        if (password == null) {
            view.showNotification("Create notes file cancelled.");
            return;
        }
        try {
            context.getDatabaseService().initDatabase(path, APP_USER, (String) password);
            view.showNotification("New notes file created: " + path);
        } catch (Exception e) {
            view.showErrorMessage("Failed to create new notes file: " + e.getMessage());
        }
    }

    public ContentModel save(ContentModel model) {
        return context.getNoteService().saveNote(model);
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
