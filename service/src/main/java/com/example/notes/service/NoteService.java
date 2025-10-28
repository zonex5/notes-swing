package com.example.notes.service;

import xyz.toway.notes.persistence.entity.NoteEntity;
import xyz.toway.notes.persistence.repository.NoteRepository;

import java.util.List;

public class NoteService {
    private final NoteRepository repo;

    public NoteService(NoteRepository repo) {
        this.repo = repo;
    }

    public NoteEntity create(String title, String content) {
        // Basic validation
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title required");
        // createdAt stored in DB in this sample, but service can also set timestamps
        return repo.save(new NoteEntity(null, title, content, java.time.Instant.now()));
    }

    public List<NoteEntity> list() { return repo.findAll(); }
    public void delete(long id) { repo.deleteById(id); }
}
