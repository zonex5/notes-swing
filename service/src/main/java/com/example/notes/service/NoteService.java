package com.example.notes.service;

import com.example.notes.domain.Note;
import com.example.notes.persistence.NoteRepository;

import java.util.List;

public class NoteService {
    private final NoteRepository repo;

    public NoteService(NoteRepository repo) {
        this.repo = repo;
    }

    public Note create(String title, String content) {
        // Basic validation
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title required");
        // createdAt stored in DB in this sample, but service can also set timestamps
        return repo.save(new Note(null, title, content, java.time.Instant.now()));
    }

    public List<Note> list() { return repo.findAll(); }
    public void delete(long id) { repo.deleteById(id); }
}
