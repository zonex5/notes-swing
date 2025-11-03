package xyz.toway.notes.domain.model;

import lombok.Data;
import xyz.toway.notes.domain.types.ContentType;
import xyz.toway.notes.domain.types.SyntaxType;

import java.time.Instant;

@Data
public abstract class ContentModel {
    private String id;
    private String groupId;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;
}
