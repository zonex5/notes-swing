package xyz.toway.notes.domain.port;

import java.util.List;
import java.util.Optional;

public interface CRUDRepository<T, ID> {

    T create(T item);

    Optional<T> findById(ID id);

    T getById(ID id);

    List<T> findAll();

    T update(T item);

    void delete(ID id);
}
