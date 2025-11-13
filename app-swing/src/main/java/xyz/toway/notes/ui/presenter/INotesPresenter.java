package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.ui.view.INotesView;

import java.util.Collection;

public interface INotesPresenter<T> extends GeneralPresenter<INotesView> {

    void deleteNote(ContentModel selectedNote);

    void saveNote(ContentModel selectedNote);

    void loadNotes(Collection<String> parentIds);

    void loadAllNotes();

    void loadOrphanNotes();

    void loadGroups();

    void addNewGroup(String title);
}
