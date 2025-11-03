package xyz.toway.notes.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.NonNull;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import org.dizitart.no2.mapper.jackson.JacksonMapperModule;
import org.dizitart.no2.mvstore.MVStoreModule;
import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.SettingsRepository;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NtDatabaseRepository implements DatabaseRepository {

    private final SettingsRepository settingsRepository;

    @Getter
    private Nitrite database;

    public NtDatabaseRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void initDatabase(@NonNull String path, @NonNull String user, @NonNull String pass) {
        MVStoreModule store = MVStoreModule.withConfig()
                .filePath(path)
                .encryptionKey(pass.toCharArray())
                .compress(true)
                .build();

        database = Nitrite.builder()
                .loadModule(store)
                .loadModule(new JacksonMapperModule(new JavaTimeModule()))
                .openOrCreate();
        //.openOrCreate(user, pass); //todo
    }

    @Override
    public void closeDatabase() {
        if (database != null && !database.isClosed()) {
            database.close();
        }
    }

    @Override
    public boolean databaseFileIsValid(String path) {
        if (path == null) return false;
        try (FileInputStream in = new FileInputStream(path)) {
            byte[] buf = new byte[32];
            int n = in.read(buf);
            if (n <= 0) return false;
            String header = new String(buf, 0, n, StandardCharsets.ISO_8859_1);
            return header.startsWith("H2encrypt");
        } catch (IOException e) {
            return false;
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
