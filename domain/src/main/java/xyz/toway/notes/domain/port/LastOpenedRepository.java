package xyz.toway.notes.domain.port;

import java.util.List;

public interface LastOpenedRepository {

    void saveLastOpenedNotes(List<String> openedNotes);

    List<String> getLastOpenedNotes();
}
