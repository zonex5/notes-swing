package xyz.toway.notes.persistence.repository;

import xyz.toway.notes.persistence.entity.NoteEntity;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {

    NoteEntity save(NoteEntity note);

    Optional<NoteEntity> findById(String id);

    List<NoteEntity> findAll();

    void deleteById(String id);
}
