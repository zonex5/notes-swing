package xyz.toway.notes.domain.model;

import java.util.Optional;

public record StoredSettings(
        Optional<String> databaseFilePath,
        boolean statusBarVisible,
        boolean restoreLastSession
) {
}
