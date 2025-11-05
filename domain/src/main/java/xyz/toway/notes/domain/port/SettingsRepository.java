package xyz.toway.notes.domain.port;

import java.util.Optional;

public interface SettingsRepository {

    Optional<String> getDatabaseFilePath();

    void setDatabaseFilePath(String path);

    boolean getStatusBarVisible();

    void setStatusBarVisible(boolean visible);

    boolean getRestoreLastSession();

    void setRestoreLastSession(boolean flag);
}
