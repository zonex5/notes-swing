package xyz.toway.notes.infrastructure.persistence.factory;

import xyz.toway.notes.domain.port.NoteRepository;
import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.factory.NoteRepositoryFactory;
import xyz.toway.notes.infrastructure.persistence.repository.NtNoteRepository;

public class NtRepositoryFactory implements NoteRepositoryFactory {
    @Override
    public NoteRepository create(DatabaseRepository databaseRepository) {
        return new NtNoteRepository(databaseRepository);
    }
}
