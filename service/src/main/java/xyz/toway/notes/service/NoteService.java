package xyz.toway.notes.service;

import lombok.NonNull;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.domain.port.NoteRepository;
import xyz.toway.notes.domain.types.SyntaxType;

public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public void test() {
        NoteModel note = new NoteModel();
        note.setContent("This is a test note.");
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
                noteRepository.update(noteModel);
            }
        }
    }
}
