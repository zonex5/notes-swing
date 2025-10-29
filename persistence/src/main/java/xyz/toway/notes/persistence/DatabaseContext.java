package xyz.toway.notes.persistence;

import lombok.Getter;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mvstore.MVStoreModule;
import xyz.toway.notes.persistence.repository.NoteRepository;

public class DatabaseContext implements DatabaseManager {

    @Getter
    private Nitrite db;

    @Override
    public void initialize(String dbPath, String user, String password) {
        MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath(dbPath)
                .compress(true)
                .build();

        db = Nitrite.builder()
                .loadModule(storeModule)
                .openOrCreate(user, password);
    }

    @Override
    public NoteRepository getNoteRepository() {
        return null;
    }
}
