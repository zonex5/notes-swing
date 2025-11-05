package xyz.toway.notes.domain.port.factory;

import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.LastOpenedRepository;

public interface LastOpenedRepositoryFactory {
    LastOpenedRepository create(DatabaseRepository databaseRepository);
}
