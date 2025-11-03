package xyz.toway.notes.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import xyz.toway.notes.domain.types.ContentType;
import xyz.toway.notes.domain.types.SyntaxType;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoteModel extends ContentModel {
    private SyntaxType syntax;
    private String content;
}
