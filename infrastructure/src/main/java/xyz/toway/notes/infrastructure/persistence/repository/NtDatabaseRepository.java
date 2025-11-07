package xyz.toway.notes.infrastructure.persistence.repository;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.NonNull;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mapper.jackson.JacksonMapperModule;
import org.dizitart.no2.mvstore.MVStoreModule;
import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.SettingsRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NtDatabaseRepository implements DatabaseRepository {

    private final SettingsRepository settingsRepository;

    @Getter
    private Nitrite database;

    public NtDatabaseRepository(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public void initDatabase(@NonNull String path, @NonNull String user, @NonNull String pass) {

        if (database != null && !database.isClosed()) {
            try {
                database.close();
            } catch (Exception ignored) {
            }
        }

        MVStoreModule store = MVStoreModule.withConfig()
                .filePath(path)
                .encryptionKey(pass.toCharArray())
                .compress(true)
                .build();

        database = Nitrite.builder()
                .loadModule(store)
                .loadModule(new JacksonMapperModule(new JavaTimeModule()))
                .openOrCreate(user, pass);

        // save last opened database path
        settingsRepository.setDatabaseFilePath(path);
    }

    @Override
    public void closeDatabase() {
        if (database != null && !database.isClosed()) {
            database.close();
        }
    }

    @Override
    public boolean databaseFileIsValid(String path) {
        if (path == null || !Files.exists(Paths.get(path))) return false;
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
}
