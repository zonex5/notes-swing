package xyz.toway.notes.persistence.entity;

import lombok.Data;
import lombok.Getter;
import org.dizitart.no2.repository.annotations.Id;

import java.time.Instant;
import java.util.UUID;

@Data
public class NoteEntity {

    @Id
    private String id;
    private String title;
    private String content;
    private Instant createdAt;

    public NoteEntity() {
        this.id = UUID.randomUUID().toString();
    }

    public NoteEntity(String title, String content, Instant createdAt) {
        this();
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public NoteEntity(String id, String title, String content, Instant createdAt) {
        this(title, content, createdAt);
        this.id = id;
    }
}
