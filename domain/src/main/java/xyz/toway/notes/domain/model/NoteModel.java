package xyz.toway.notes.domain.model;

import lombok.Data;
import lombok.ToString;
import xyz.toway.notes.domain.types.ContentType;
import xyz.toway.notes.domain.types.SyntaxType;

import java.time.Instant;

@Data
public class NoteModel {
    private String id;
    private String groupId;
    private String title;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;

    private ContentType type;
    private SyntaxType syntax;

    public NoteModel() {
    }
}
