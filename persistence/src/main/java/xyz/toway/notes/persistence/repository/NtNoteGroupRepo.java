package xyz.toway.notes.persistence.repository;

import xyz.toway.notes.persistence.entity.NoteEntity;
import xyz.toway.notes.persistence.entity.NoteGroupEntity;

import java.util.List;
import java.util.Optional;

public class NtNoteGroupRepo  implements NoteGroupRepository {
    @Override
    public NoteEntity save(NoteGroupEntity note) {
        return null;
    }

    @Override
    public Optional<NoteGroupEntity> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<NoteGroupEntity> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(String id) {

    }
}
