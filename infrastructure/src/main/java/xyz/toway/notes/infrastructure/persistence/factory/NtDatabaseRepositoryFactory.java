package xyz.toway.notes.infrastructure.persistence.factory;

import xyz.toway.notes.domain.port.SettingsRepository;
import xyz.toway.notes.domain.port.factory.DatabaseRepositoryFactory;
import xyz.toway.notes.infrastructure.persistence.repository.NtDatabaseRepository;

public class NtDatabaseRepositoryFactory implements DatabaseRepositoryFactory {

    @Override
    public NtDatabaseRepository create(SettingsRepository settingsRepository) {
        return new NtDatabaseRepository(settingsRepository);
    }
}
