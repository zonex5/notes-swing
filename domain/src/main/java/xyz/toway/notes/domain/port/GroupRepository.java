package xyz.toway.notes.domain.port;

import xyz.toway.notes.domain.model.GroupModel;

import java.util.Set;

public interface GroupRepository extends CRUDRepository<GroupModel, String> {

    void delete(Set<String> ids);
}
