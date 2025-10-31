package xyz.toway.notes.domain.port;

public interface DatabaseRepository {

    void initDatabase(String path, String user, String pass);

    void closeDatabase();

    /**
     * Checks if file looks like a Nitrite/MVStore encrypted database
     * without trying to open it.
     *
     * @param path path to file
     * @return true if file seems to be MVStore or H2-encrypted DB
     */

    boolean databaseFileIsValid(String path);

    void test();
}
