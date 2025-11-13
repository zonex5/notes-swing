package xyz.toway.notes.infrastructure.persistence.factory;

import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.GroupRepository;
import xyz.toway.notes.domain.port.factory.GroupRepositoryFactory;
import xyz.toway.notes.infrastructure.persistence.repository.NtGroupRepository;

public class NtGroupRepositoryFactory implements GroupRepositoryFactory {
    @Override
    public GroupRepository create(DatabaseRepository databaseRepository) {
        return new NtGroupRepository(databaseRepository);
    }
}
