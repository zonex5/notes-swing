package xyz.toway.notes.infrastructure.persistence.factory;

import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.LastOpenedRepository;
import xyz.toway.notes.domain.port.factory.LastOpenedRepositoryFactory;
import xyz.toway.notes.infrastructure.persistence.repository.NtLastOpenedRepository;

public class NtLastOpenedRepositoryFactory implements LastOpenedRepositoryFactory {
    @Override
    public LastOpenedRepository create(DatabaseRepository databaseRepository) {
        return new NtLastOpenedRepository(databaseRepository);
    }
}
