package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;

import java.util.List;

public interface INotesPresenter extends GeneralPresenter {

    List<ContentModel> getNotesList();

    void deleteNote(ContentModel selectedNote);

    void saveNote(ContentModel selectedNote);

    void setViewData();
}
