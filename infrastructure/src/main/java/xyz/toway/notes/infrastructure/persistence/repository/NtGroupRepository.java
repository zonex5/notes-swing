package xyz.toway.notes.infrastructure.persistence.repository;

import org.dizitart.no2.repository.ObjectRepository;
import xyz.toway.notes.domain.model.GroupModel;
import xyz.toway.notes.domain.port.DatabaseRepository;
import xyz.toway.notes.domain.port.GroupRepository;
import xyz.toway.notes.infrastructure.persistence.Mapper;
import xyz.toway.notes.infrastructure.persistence.entity.GroupEntity;

import java.util.*;

import static org.dizitart.no2.filters.FluentFilter.where;

public class NtGroupRepository implements GroupRepository {

    private final NtDatabaseRepository databaseRepository;

    public NtGroupRepository(DatabaseRepository databaseRepository) {
        if (databaseRepository instanceof NtDatabaseRepository ntDatabaseRepository) {
            this.databaseRepository = ntDatabaseRepository;
        } else {
            throw new IllegalArgumentException("Invalid DatabaseRepository implementation");
        }
    }

    @Override
    public GroupModel create(GroupModel item) {
        var entity = Mapper.fromModel(item);
        entity.setId(UUID.randomUUID().toString());
        if (getRepository().insert(entity).getAffectedCount() > 0) {
            return Mapper.toModel(entity);
        } else {
            throw new RuntimeException("Failed to create GroupModel");
        }
    }

    @Override
    public Optional<GroupModel> findById(String id) {
        return Optional.ofNullable(
                getRepository().find(where("id").eq(id))
                        .firstOrNull()
        ).map(Mapper::toModel);
    }

    @Override
    public GroupModel getById(String id) {
        return findById(id)
                .orElse(null);
    }

    @Override
    public List<GroupModel> findAll() {
        return getRepository().find()
                .toList()
                .stream()
                .map(Mapper::toModel)
                .sorted(Comparator.comparing(GroupModel::getTitle))
                .toList();
    }

    @Override
    public GroupModel update(GroupModel item) {
        var entity = Mapper.fromModel(item);
        getRepository().update(entity);
        return Mapper.toModel(entity);
    }

    @Override
    public void delete(String id) {
        getRepository().remove(where("id").eq(id));
    }

    private ObjectRepository<GroupEntity> getRepository() {
        return Objects.requireNonNull(
                databaseRepository.getDatabase()
                        .getRepository(GroupEntity.class)
        );
    }

    @Override
    public void delete(Set<String> ids) {
        getRepository().remove(where("id").in(ids.toArray(new String[0])));
    }
}
