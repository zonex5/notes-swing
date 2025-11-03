package xyz.toway.notes.infrastructure.settings;

import xyz.toway.notes.domain.port.SettingsRepository;
import xyz.toway.notes.domain.port.factory.SettingsRepositoryFactory;

public class PreferencesSettingsRepositoryFactory implements SettingsRepositoryFactory {
    @Override
    public SettingsRepository create() {
        return new PreferencesSettingsRepository();
    }
}
