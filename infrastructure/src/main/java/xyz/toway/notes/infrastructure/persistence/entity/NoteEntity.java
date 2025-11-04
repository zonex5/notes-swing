package xyz.toway.notes.infrastructure.persistence.entity;

import lombok.Data;
import org.dizitart.no2.repository.annotations.Entity;
import org.dizitart.no2.repository.annotations.Id;
import xyz.toway.notes.domain.types.ContentType;
import xyz.toway.notes.domain.types.SyntaxType;

import java.time.Instant;

@Data
@Entity
public class NoteEntity {
    @Id()
    private String id;
    private String groupId;
    private String content;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;

    private ContentType type;
    private SyntaxType syntax;
}
