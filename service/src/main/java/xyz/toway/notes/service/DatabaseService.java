package xyz.toway.notes.service;

import xyz.toway.notes.domain.port.DatabaseRepository;

public class DatabaseService {

    private final DatabaseRepository databaseRepository;

    public DatabaseService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void initDatabase(String path, String user, String pass) {
        databaseRepository.initDatabase(path, user, pass);
    }

    public void closeDatabase() {
        databaseRepository.closeDatabase();
    }

    public boolean databaseFileIsValid(String path) {
        return databaseRepository.databaseFileIsValid(path);
    }

    public void test() {
        databaseRepository.test();
        closeDatabase();
    }
}
