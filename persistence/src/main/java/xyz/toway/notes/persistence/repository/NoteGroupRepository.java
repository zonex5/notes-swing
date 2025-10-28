package xyz.toway.notes.persistence.repository;

import xyz.toway.notes.persistence.entity.NoteEntity;
import xyz.toway.notes.persistence.entity.NoteGroupEntity;

import java.util.List;
import java.util.Optional;

public interface NoteGroupRepository {

    NoteEntity save(NoteGroupEntity note);

    Optional<NoteGroupEntity> findById(String id);

    List<NoteGroupEntity> findAll();

    void deleteById(String id);
}
