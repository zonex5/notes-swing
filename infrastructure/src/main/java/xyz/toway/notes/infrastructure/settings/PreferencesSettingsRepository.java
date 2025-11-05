package xyz.toway.notes.infrastructure.settings;

import xyz.toway.notes.domain.port.SettingsRepository;

import java.util.Optional;
import java.util.prefs.Preferences;

public class PreferencesSettingsRepository implements SettingsRepository {

    private static final String DB_FILE_PATH = "database-file-path";
    private static final String STATUS_BAR_VISIBLE = "status-bar-visible";
    private static final String RESTORE_LAST_SESSION = "restore-last-session";

    private final Preferences prefs = Preferences.userRoot().node("mysupernotes");

    public PreferencesSettingsRepository() {
        System.out.println(prefs.absolutePath());
    }

    @Override
    public Optional<String> getDatabaseFilePath() {
        return Optional.ofNullable(prefs.get(DB_FILE_PATH, null));
    }

    @Override
    public void setDatabaseFilePath(String path) {
        prefs.put(DB_FILE_PATH, path);
    }

    @Override
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
}
