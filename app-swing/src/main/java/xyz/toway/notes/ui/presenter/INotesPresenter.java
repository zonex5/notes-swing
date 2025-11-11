package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface INotesPresenter<T> extends GeneralPresenter {

    List<ContentModel> getNotesList();

    void deleteNote(ContentModel selectedNote);

    void saveNote(ContentModel selectedNote);

    CompletableFuture<List<ContentModel>> loadNotes(Collection<String> ids);

    void loadGroups(Consumer<List<T>> consumer);

    void addNewGroup(String title);
}
