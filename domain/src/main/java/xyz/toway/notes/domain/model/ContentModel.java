package xyz.toway.notes.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.toway.notes.domain.types.ContentType;

import java.time.Instant;

@Data
@NoArgsConstructor
public abstract class ContentModel {
    private String id;
    private String groupId;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;
    private ContentType contentType;
    private long contentHash;

    public abstract String getContentPreview();

    public ContentModel(String title) {
        this.title = title;
        updateContentHash();
    }

    protected abstract void updateContentHash();
}
