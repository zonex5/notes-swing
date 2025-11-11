package xyz.toway.notes.service;

import lombok.NonNull;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.domain.port.LastOpenedRepository;
import xyz.toway.notes.domain.port.NoteRepository;
import xyz.toway.notes.domain.types.SyntaxType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class NoteService {

    private final NoteRepository noteRepository;
    private final LastOpenedRepository lastOpenedRepository;

    public NoteService(NoteRepository noteRepository, LastOpenedRepository lastOpenedRepository) {
        this.noteRepository = noteRepository;
        this.lastOpenedRepository = lastOpenedRepository;
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

    public List<ContentModel> findAll() {
        //todo add items from other content types
        return noteRepository.findAll()
                .stream()
                .map(note -> (ContentModel) note)
                .toList();
    }

    public List<ContentModel> findAllByParentsLight(Collection<String> ids) {
        //todo add items from other content types
        return noteRepository.findAllByParents(ids)
                .stream()
                .map(note -> (ContentModel) note)
                .toList();
    }

    public List<ContentModel> findAllOrphansLight() {
        //todo add items from other content types
        return noteRepository.findAllOrphans()
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

    public void saveNoteAsTextFile(String text, File selectedFile) throws IOException {
        Files.writeString(selectedFile.toPath(), text);
    }
}
