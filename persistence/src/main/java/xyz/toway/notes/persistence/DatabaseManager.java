package xyz.toway.notes.persistence;

import xyz.toway.notes.persistence.repository.NoteRepository;

public interface DatabaseManager {

    default void initialize(String dbPath, String user, String password) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void initialize(String user, String password) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void initialize() {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void close() {
        throw new UnsupportedOperationException("Not implemented");
    }

    NoteRepository getNoteRepository();
}