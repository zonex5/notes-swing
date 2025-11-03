package xyz.toway.notes.domain.port;

import xyz.toway.notes.domain.model.NoteModel;

import java.util.List;

public interface NoteRepository extends CRUDRepository<NoteModel, String> {

    List<NoteModel> findByGroupId(String groupId);
}
