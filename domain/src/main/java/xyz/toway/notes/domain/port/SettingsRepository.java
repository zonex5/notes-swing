package xyz.toway.notes.domain.port;

import java.util.Optional;

public interface SettingsRepository {

    // settings keys
    String STATUS_BAR_VISIBLE = "status-bar-visible";
    String RESTORE_LAST_SESSION = "restore-last-session";
    String MINIMIZE_ON_CLOSE = "minimize-on-cose";
    String OPEN_DEFAULT_NOTE = "open-default-note";

    void setBooleanValue(String name, boolean flag);

    boolean getBooleanValue(String name);

    void setStringValue(String name, String value);

    String getStringValue(String name);

    Optional<String> getDatabaseFilePath();

    void setDatabaseFilePath(String path);
}
