package xyz.toway.notes.service;

import xyz.toway.notes.domain.port.SettingsRepository;

public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public SettingsRepository getSettings() {
        return settingsRepository;
    }
}
