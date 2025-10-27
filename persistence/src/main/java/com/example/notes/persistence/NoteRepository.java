package com.example.notes.persistence;

import com.example.notes.domain.Note;
import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    // CRUD interface
    Note save(Note note);
    Optional<Note> findById(long id);
    List<Note> findAll();
    void deleteById(long id);
}
