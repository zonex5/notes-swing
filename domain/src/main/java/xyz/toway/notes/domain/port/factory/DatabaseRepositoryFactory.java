package xyz.toway.notes.domain.port.factory;

import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.SettingsRepository;

public interface DatabaseRepositoryFactory {
    DatabaseRepository create(SettingsRepository settingsRepository);
}
