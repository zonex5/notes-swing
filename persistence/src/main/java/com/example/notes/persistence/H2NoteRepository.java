package com.example.notes.persistence;

import com.example.notes.domain.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class H2NoteRepository implements NoteRepository {

    @Override
    public Note save(Note note) {
        return null;
    }

    @Override
    public Optional<Note> findById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Note> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(long id) {

    }
}
