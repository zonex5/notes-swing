package xyz.toway.notes.domain.port;

import xyz.toway.notes.domain.model.NoteModel;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface NoteRepository extends CRUDRepository<NoteModel, String> {

    List<NoteModel> findByGroupId(String groupId);

    List<NoteModel> findAllByParents(Collection<String> ids);

    List<NoteModel> findAllOrphans();

    void deleteAllByGroupId(String groupId);

    void deleteAllByGroupId(Set<String> ids);
}
