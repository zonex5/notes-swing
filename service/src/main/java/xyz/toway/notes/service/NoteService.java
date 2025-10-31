package xyz.toway.notes.service;

import xyz.toway.notes.domain.port.DatabaseRepository;

public class NoteService {

    private final DatabaseRepository databaseRepository;

    public NoteService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void initDatabase(String path, String user, String pass) {

    }
}
