package xyz.toway.notes.domain.port.factory;

import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.GroupRepository;
import xyz.toway.notes.domain.port.NoteRepository;

public interface GroupRepositoryFactory {
    GroupRepository create(DatabaseRepository databaseRepository);
}
