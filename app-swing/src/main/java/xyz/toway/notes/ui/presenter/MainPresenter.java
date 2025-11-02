package xyz.toway.notes.ui.presenter;

import lombok.NonNull;
import xyz.toway.notes.service.DatabaseService;
import xyz.toway.notes.service.SettingsService;
import xyz.toway.notes.ui.ApplicationContext;
import xyz.toway.notes.ui.view.MainForm;

import javax.swing.*;

public class MainPresenter implements GeneralPresenter<MainForm> {
    private static final String APP_USER = "app_user";

    private final DatabaseService databaseService;
    private final SettingsService settingsService;

    private ApplicationContext context;
    private MainForm view;

    public MainPresenter(@NonNull ApplicationContext applicationContext) {
        this.context = applicationContext;
        databaseService = context.getDatabaseService();
        settingsService = context.getSettingsService();

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
        // try to open existing database
        context.getSettingsService().getSettings()
                .getDatabaseFilePath()
                .ifPresent(this::openDatabase);
    }

    public void menuItemStatusBar(boolean visible) {
        settingsService.getSettings().setStatusBarVisible(visible);
    }

    private void openDatabase(String path) {

        try {
            if (!databaseService.databaseFileIsValid(path)) {
                throw new Exception();
            }
            var password = view.askForPassword();
            if (password == null) {
                view.showNotification("Open notes file cancelled.", UIManager.getColor("TitlePane.inactiveForeground"));
                return;
            }
            context.getDatabaseService().initDatabase(path, APP_USER, password);
            view.showNotification("Notes file opened: " + path);
        } catch (Exception e) {
            view.showErrorMessage("Failed to open file: " + path);
            view.showNotification("Failed to open file: " + path, UIManager.getColor("Objects.RedStatus"));
        }
    }
}
