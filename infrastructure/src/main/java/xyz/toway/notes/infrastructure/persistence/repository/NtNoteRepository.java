package xyz.toway.notes.infrastructure.persistence.repository;

import org.dizitart.no2.repository.ObjectRepository;
import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.domain.port.NoteRepository;
import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.infrastructure.persistence.Mapper;
import xyz.toway.notes.infrastructure.persistence.entity.NoteEntity;

import java.util.*;

import static org.dizitart.no2.filters.FluentFilter.where;

public class NtNoteRepository implements NoteRepository {

    private final NtDatabaseRepository databaseRepository;

    public NtNoteRepository(DatabaseRepository databaseRepository) {
        if (databaseRepository instanceof NtDatabaseRepository ntDatabaseRepository) {
            this.databaseRepository = ntDatabaseRepository;
        } else {
            throw new IllegalArgumentException("Invalid DatabaseRepository implementation");
        }
    }

    private ObjectRepository<NoteEntity> getRepository() {
        return Objects.requireNonNull(
                databaseRepository.getDatabase()
                        .getRepository(NoteEntity.class)
        );
    }

    @Override
    public NoteModel create(NoteModel item) {
        var entity = Mapper.fromModel(item);
        entity.setId(UUID.randomUUID().toString());
        if (getRepository().insert(entity).getAffectedCount() > 0) {
            return Mapper.toModel(entity);
        } else {
            throw new RuntimeException("Failed to create NoteModel");
        }
    }

    @Override
    public Optional<NoteModel> findById(String id) {
        return Optional.ofNullable(
                getRepository().find(where("id").eq(id))
                        .firstOrNull()
        ).map(Mapper::toModel);
    }

    @Override
    public NoteModel getById(String id) {
        return findById(id).orElse(null);
    }

    @Override
    public List<NoteModel> findAll() {
        return getRepository().find()
                .toList()
                .stream()
                .map(Mapper::toModel)
                .toList();
    }

    @Override
    public List<NoteModel> findAllByParents(Collection<String> ids) {
        return getRepository().find(where("groupId").in(ids.toArray(new String[0])))
                .toList()
                .stream()
                .map(Mapper::toModel)
                .toList();
    }

    @Override
    public List<NoteModel> findAllOrphans() {
        return getRepository().find(where("groupId").eq(null))
                .toList()
                .stream()
                .map(Mapper::toModel)
                .toList();
    }

    @Override
    public NoteModel update(NoteModel item) {
        var entity = Mapper.fromModel(item);
        getRepository().update(entity);
        return Mapper.toModel(entity);
    }

    @Override
    public void delete(String id) {
        getRepository().remove(where("id").eq(id));
    }

    @Override
    public void deleteAllByGroupId(String groupId) {
        getRepository().remove(where("groupId").eq(groupId));
    }

    @Override
    public void deleteAllByGroupId(Set<String> ids) {
        getRepository().remove(where("groupId").in(ids.toArray(new String[0])));
    }

    @Override
    public List<NoteModel> findByGroupId(String groupId) {
        return getRepository().find(where("groupId").eq(groupId))
                .toList()
                .stream()
                .map(Mapper::toModel)
                .toList();
    }
}
