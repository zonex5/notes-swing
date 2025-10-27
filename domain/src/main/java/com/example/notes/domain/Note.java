package com.example.notes.domain;

import java.time.Instant;
import java.util.Objects;

public class Note {
    // Simple immutable entity
    private final Long id;
    private final String title;
    private final String content;
    private final Instant createdAt;

    public Note(Long id, String title, String content, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
