package xyz.toway.notes.service;

import lombok.NonNull;
import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.GroupModel;
import xyz.toway.notes.domain.model.NoteModel;
import xyz.toway.notes.domain.port.GroupRepository;
import xyz.toway.notes.domain.port.LastOpenedRepository;
import xyz.toway.notes.domain.port.NoteRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoteService {

    private final Executor executor = Executors.newFixedThreadPool(4);

    private final NoteRepository noteRepository;
    private final GroupRepository groupRepository;
    private final LastOpenedRepository lastOpenedRepository;

    public NoteService(NoteRepository noteRepository, GroupRepository groupRepository, LastOpenedRepository lastOpenedRepository) {
        this.noteRepository = noteRepository;
        this.groupRepository = groupRepository;
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

    public CompletableFuture<List<ContentModel>> findAll() {
        //todo add items from other content types
        return CompletableFuture.supplyAsync(() -> toContentList(noteRepository.findAll()), executor)
                .exceptionally(ex -> {
                    System.err.println("Error loading all notes: " + ex.getMessage());
                    return Collections.emptyList();
                });
    }

    public CompletableFuture<List<ContentModel>> findAllByParents(Collection<String> ids) {
        //todo add items from other content types
        return CompletableFuture.supplyAsync(() -> toContentList(noteRepository.findAllByParents(ids)), executor)
                .exceptionally(ex -> {
                    System.err.println("Error loading notes by parent ids: " + ex.getMessage());
                    return Collections.emptyList();
                });
    }

    public CompletableFuture<List<ContentModel>> findAllOrphans() {
        //todo add items from other content types
        return CompletableFuture.supplyAsync(() -> toContentList(noteRepository.findAllOrphans()), executor)
                .exceptionally(ex -> {
                    System.err.println("Error loading orphans notes: " + ex.getMessage());
                    return Collections.emptyList();
                });
    }

    public CompletableFuture<List<GroupModel>> loadGroups() {
        return CompletableFuture.supplyAsync(groupRepository::findAll, executor)
                .exceptionally(ex -> {
                    System.err.println("Error loading groups: " + ex.getMessage());
                    return Collections.emptyList();
                });
    }

    public CompletableFuture<GroupModel> createGroup(GroupModel model) {
        return CompletableFuture.supplyAsync(() -> groupRepository.create(model));
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

    private List<ContentModel> toContentList(List<NoteModel> src) {
        return src.stream()
                .map(note -> (ContentModel) note)
                .toList();
    }
}
