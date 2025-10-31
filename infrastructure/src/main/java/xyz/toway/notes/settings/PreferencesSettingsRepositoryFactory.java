package xyz.toway.notes.settings;

import xyz.toway.notes.domain.port.SettingsRepository;
import xyz.toway.notes.domain.port.factory.SettingsRepositoryFactory;

public class PreferencesSettingsRepositoryFactory implements SettingsRepositoryFactory {
    @Override
    public SettingsRepository create() {
        return new PreferencesSettingsRepository();
    }
}
