package xyz.toway.notes.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.toway.notes.domain.types.ContentType;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public abstract class ContentModel {
    public static final String DEFAULT_DOCUMENT_TITLE = "New Note";

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

    public boolean isContentChanged(long newContentHash) {
        return this.contentHash != newContentHash;
    }

    public boolean isNew() {
        return (this.id == null || this.id.isEmpty()) && DEFAULT_DOCUMENT_TITLE.equals(this.title);
    }

    protected abstract void updateContentHash();
}
