package xyz.toway.settings;

import xyz.toway.notes.domain.port.SettingsRepository;

public class PreferencesSettings implements SettingsRepository {
    @Override
    public String dbFilePath() {
        return null;
    }
}
