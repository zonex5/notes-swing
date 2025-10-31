package xyz.toway.notes.domain.port;

public interface DatabaseRepository {

    void initDatabase(String path, String user, String pass);

    void closeDatabase();

    void test();
}
