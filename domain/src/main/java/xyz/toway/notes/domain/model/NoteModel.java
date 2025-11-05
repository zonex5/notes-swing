package xyz.toway.notes.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import xyz.toway.notes.domain.types.SyntaxType;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.zip.CRC32C;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoteModel extends ContentModel {
    private SyntaxType syntax;
    private String content;

    public NoteModel() {
        setTitle(ContentModel.DEFAULT_DOCUMENT_TITLE);
        setGroupId(null);
        setCreatedAt(Instant.now());
    }

    public void setContent(String content) {
        this.content = content;
        updateContentHash();
    }

    @Override
    public boolean isNew() {
        return super.isNew() && (this.content == null || this.content.isEmpty());
    }

    @Override
    public String getContentPreview() {
        if (getContent() == null) {
            return "";
        }
        String content = getContent().replace("\n", " ").replace("\r", " ");
        return content.length() <= 70 ? content : content.substring(0, 67) + "...";
    }

    @Override
    protected void updateContentHash() {
        setContentHash(calculateContentHash(content));
    }

    public static long calculateContentHash(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        CRC32C c = new CRC32C();
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        c.update(bytes, 0, bytes.length);
        return c.getValue();
    }
}
