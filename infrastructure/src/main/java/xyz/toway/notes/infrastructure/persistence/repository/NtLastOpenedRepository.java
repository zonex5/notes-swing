package xyz.toway.notes.infrastructure.persistence.repository;

import lombok.NonNull;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteCollection;
import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.LastOpenedRepository;

import java.util.List;

public class NtLastOpenedRepository implements LastOpenedRepository {

    private static final String COLLECTION_NAME = "last_opened_notes";
    private final NtDatabaseRepository databaseRepository;

    public NtLastOpenedRepository(DatabaseRepository databaseRepository) {
        if (databaseRepository instanceof NtDatabaseRepository ntDatabaseRepository) {
            this.databaseRepository = ntDatabaseRepository;
        } else {
            throw new IllegalArgumentException("Invalid DatabaseRepository implementation");
        }
    }

    private NitriteCollection getCollection() {
        return databaseRepository.getDatabase().getCollection(COLLECTION_NAME);
    }

    @Override
    public void saveLastOpenedNotes(@NonNull List<String> openedNotes) {
        var collection = getCollection();
        collection.clear();
        openedNotes.stream()
                .map(note -> Document.createDocument("value", note))
                .forEach(collection::insert);
    }

    @Override
    public List<String> getLastOpenedNotes() {
        return getCollection()
                .find()
                .toList()
                .stream()
                .map(doc -> doc.get("value", String.class))
                .toList();
    }
}
