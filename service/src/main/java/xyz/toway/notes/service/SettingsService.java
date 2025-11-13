package xyz.toway.notes.service;

import xyz.toway.notes.domain.model.StoredSettings;
import xyz.toway.notes.domain.port.SettingsRepository;

import static xyz.toway.notes.domain.port.SettingsRepository.*;

public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public SettingsRepository getSettingsRepo() {
        return settingsRepository;
    }

    public StoredSettings getStoredSettings() {
        return new StoredSettings(
                settingsRepository.getDatabaseFilePath(),
                settingsRepository.getBooleanValue(STATUS_BAR_VISIBLE),
                settingsRepository.getBooleanValue(RESTORE_LAST_SESSION),
                settingsRepository.getBooleanValue(MINIMIZE_ON_CLOSE)
        );
    }
}
