package xyz.toway.notes.service;

import lombok.NonNull;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.domain.port.NoteRepository;
import xyz.toway.notes.domain.types.SyntaxType;

import java.time.Instant;
import java.util.List;

public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public void test(String title) {
        NoteModel note = new NoteModel();
        note.setContent("""
                This is a test note. 
                It contains multiple lines.
                <b>Bold text</b>
                <i>Italic text</i>
                <u>Underlined text</u>
                <a href="https://example.com">Example Link</a>
                        
                1. First item
                2. Second item
                - Bullet point
                        
                > This is a blockquote.
                        
                ```java
                public static void main(String[] args) {
                    System.out.println("Hello, World!");
                }
                ```
                       
                """);
        note.setTitle(title);
        note.setGroupId(null);
        note.setSyntax(SyntaxType.HTML);
        note.setCreatedAt(java.time.Instant.now());
        var result = noteRepository.create(note);
        System.out.println(result);
    }

    public void save(@NonNull ContentModel model) {
        if (model instanceof NoteModel noteModel) {
            if (noteModel.getId() == null) {
                noteRepository.create(noteModel);
            } else {
                noteModel.setUpdatedAt(Instant.now());
                noteRepository.update(noteModel);
            }
        }
    }

    public List<NoteModel> findAll() {
        return noteRepository.findAll();
    }

    public List<ContentModel> findAllLight() {
        //todo add items from other content types
        return findAll()
                .stream()
                .map(note -> (ContentModel) note)
                .toList();
    }
}
