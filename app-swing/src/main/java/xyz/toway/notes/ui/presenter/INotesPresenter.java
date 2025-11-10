package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface INotesPresenter<T> extends GeneralPresenter {

    List<ContentModel> getNotesList();

    void deleteNote(ContentModel selectedNote);

    void saveNote(ContentModel selectedNote);

    void loadData();

    void loadGroups(Consumer<List<T>> consumer);
}
