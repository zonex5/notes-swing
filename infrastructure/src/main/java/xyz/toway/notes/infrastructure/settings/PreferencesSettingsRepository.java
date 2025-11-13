package xyz.toway.notes.infrastructure.settings;

import xyz.toway.notes.domain.port.SettingsRepository;

import java.util.Optional;
import java.util.prefs.Preferences;

public class PreferencesSettingsRepository implements SettingsRepository {

    private final static String DB_FILE_PATH = "database-file-path";

    private final Preferences prefs = Preferences.userRoot().node("mysupernotes");

    public PreferencesSettingsRepository() {
    }

   @Override
    public Optional<String> getDatabaseFilePath() {
        return Optional.ofNullable(prefs.get(DB_FILE_PATH, null));
    }

    @Override
    public void setDatabaseFilePath(String path) {
        prefs.put(DB_FILE_PATH, path);
    }

 /*    @Override
    public boolean getStatusBarVisible() {
        return prefs.getBoolean(STATUS_BAR_VISIBLE, true);
    }

    @Override
    public void setStatusBarVisible(boolean visible) {
        prefs.putBoolean(STATUS_BAR_VISIBLE, visible);
    }

    @Override
    public boolean getRestoreLastSession() {
        return prefs.getBoolean(RESTORE_LAST_SESSION, false);
    }

    @Override
    public void setRestoreLastSession(boolean flag) {
        prefs.putBoolean(RESTORE_LAST_SESSION, flag);
    }

    @Override
    public boolean getMinimizeOnClose() {
        return prefs.getBoolean(MINIMIZE_ON_CLOSE, true);
    }

    @Override
    public void setMinimizeOnClose(boolean flag) {
        prefs.putBoolean(MINIMIZE_ON_CLOSE, flag);
    }*/

    @Override
    public void setBooleanValue(String name, boolean flag) {
        prefs.putBoolean(name, flag);
    }

    @Override
    public boolean getBooleanValue(String name) {
        return prefs.getBoolean(name, false);
    }

    @Override
    public void setStringValue(String name, String value) {
        prefs.put(name, value);
    }

    @Override
    public String getStringValue(String name) {
        return prefs.get(name, null);
    }
}
