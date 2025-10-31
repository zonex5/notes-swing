package xyz.toway.notes.persistence;

import xyz.toway.notes.domain.port.SettingsRepository;
import xyz.toway.notes.domain.port.factory.DatabaseRepositoryFactory;

public class NtDatabaseRepositoryFactory implements DatabaseRepositoryFactory {

    @Override
    public NtDatabaseRepository create(SettingsRepository settingsRepository) {
        return new NtDatabaseRepository(settingsRepository);
    }
}
