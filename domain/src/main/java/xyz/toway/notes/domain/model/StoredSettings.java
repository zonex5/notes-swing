package xyz.toway.notes.domain.model;

public record StoredSettings(
        String databaseFilePath,
        boolean statusBarVisible
) {
}
