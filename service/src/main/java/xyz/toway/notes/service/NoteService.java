package xyz.toway.notes.service;

import lombok.NonNull;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.domain.port.LastOpenedRepository;
import xyz.toway.notes.domain.port.NoteRepository;
import xyz.toway.notes.domain.types.SyntaxType;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class NoteService {

    private final NoteRepository noteRepository;
    private final LastOpenedRepository lastOpenedRepository;

    public NoteService(NoteRepository noteRepository, LastOpenedRepository lastOpenedRepository) {
        this.noteRepository = noteRepository;
        this.lastOpenedRepository = lastOpenedRepository;
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

    public ContentModel save(@NonNull ContentModel model) {
        if (model instanceof NoteModel noteModel) {
            if (noteModel.getId() == null) {
                return noteRepository.create(noteModel);
            } else {
                noteModel.setUpdatedAt(Instant.now());
                return noteRepository.update(noteModel);
            }
        }
        return null;
    }

    public void delete(@NonNull ContentModel model) {
        if (model instanceof NoteModel noteModel) {
            noteRepository.delete(noteModel.getId());
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

    public void saveLastOpenedDocs(@NonNull List<String> ids) {
        lastOpenedRepository.saveLastOpenedNotes(ids);
    }

    public List<ContentModel> getLastOpenedDocs() {
        var ids = lastOpenedRepository.getLastOpenedNotes();
        //todo add items from other content types
        return ids.stream()
                .map(noteRepository::getById)
                .filter(Objects::nonNull)
                .map(note -> (ContentModel) note)
                .toList();
    }
}
