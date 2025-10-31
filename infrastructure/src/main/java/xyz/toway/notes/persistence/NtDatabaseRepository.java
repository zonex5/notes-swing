package xyz.toway.notes.persistence;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.mvstore.MVStoreModule;
import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.SettingsRepository;

public class NtDatabaseRepository implements DatabaseRepository {

    private final SettingsRepository settingsRepository;

    private Nitrite database;

    public NtDatabaseRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void initDatabase(String path, String user, String pass) {
        MVStoreModule store = MVStoreModule.withConfig()
                .filePath(path)
                .build();

        database = Nitrite.builder()
                .loadModule(store)
                .openOrCreate(user, pass);
    }

    @Override
    public void closeDatabase() {
        if (database != null && !database.isClosed()) {
            database.close();
        }
    }

    @Override
    public void test() {
        NitriteCollection col = database.getCollection("misc");
        Document doc = Document.createDocument()
                .put("name", "Alice")
                .put("age", 30)
                .put("skills", new String[]{"Java", "SQL"})
                .put("meta", Document.createDocument("level", "senior").put("remote", true));
        col.insert(doc);
    }
}
