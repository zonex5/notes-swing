package xyz.toway.notes.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public abstract class ContentModel {
    private String id;
    private String groupId;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;
    private long contentHash;

    public ContentModel(String title) {
        this.title = title;
        updateContentHash();
    }

    protected abstract void updateContentHash();
}
