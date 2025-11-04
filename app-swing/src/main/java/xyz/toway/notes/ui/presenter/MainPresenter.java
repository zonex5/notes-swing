package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.ui.view.MainForm;

import javax.swing.*;

import static xyz.toway.notes.ui.Main.context;

public class MainPresenter implements GeneralPresenter<MainForm> {
    private static final String APP_USER = "app_user";

    private MainForm view;

    public MainPresenter() {

        // add shutdown hook to close database
        Runtime.getRuntime().addShutdownHook(new Thread(() -> context.getDatabaseService().closeDatabase(), "db-shutdown-hook"));

        //---- tmp
        //context.getSettingsService().getSettings().setDatabaseFilePath("D:\\database.db");
    }

    @Override
    public void setView(MainForm view) {
        this.view = view;
    }

    @Override
    public MainForm getView() {
        return view;
    }

    @Override
    public void init() {
        // get settings to Map<String, Object> settings
        var settings = context.getSettingsService().getStoredSettings();
        view.applyUISettings(settings);

        // try to open existing database
        settings.databaseFilePath().ifPresent(this::openDatabase);

        //----------
        context.getDatabaseService().initDatabase("d:\\test.db", APP_USER, "test");
        //context.getNoteService().test("Hello World!");
        //context.getNoteService().test("Test 2");
        //context.getNoteService().test("Another note");
        //context.getNoteService().test("Some note");
        //context.getNoteService().test("Another super note");
    }

    public void menuItemStatusBar(boolean visible) {
        context.getSettingsService().getSettingsRepo().setStatusBarVisible(visible);
    }

    private void openDatabase(String path) {

        try {
            if (!context.getDatabaseService().databaseFileIsValid(path)) {
                throw new Exception();
            }
            // todo: ask for password dialog
            //var password = view.requestPassword();
            //if (password == null) {
            //    view.showNotification("Open notes file cancelled.", UIManager.getColor("TitlePane.inactiveForeground"));
            //    return;
            //}
            context.getDatabaseService().initDatabase(path, APP_USER, "test"); //todo password
            view.showNotification("Notes file opened: " + path);
        } catch (Exception e) {
            view.showErrorMessage("Failed to open file: " + path);
            view.showNotification("Failed to open file: " + path, UIManager.getColor("Objects.RedStatus"));
        }
    }

    public void save(ContentModel model) {
        context.getNoteService().save(model);
    }
}
