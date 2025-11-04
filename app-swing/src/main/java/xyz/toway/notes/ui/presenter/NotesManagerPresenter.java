package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.ui.view.NotesManagerWindow;

import java.util.List;

import static xyz.toway.notes.ui.Main.context;

public class NotesManagerPresenter implements GeneralPresenter<NotesManagerWindow> {

    private NotesManagerWindow view;

    @Override
    public void setView(NotesManagerWindow view) {
        this.view = view;
    }

    @Override
    public NotesManagerWindow getView() {
        return null;
    }

    @Override
    public void init() {

    }

    public List<ContentModel> getNotesList() {
        return context.getNoteService().findAllLight();
    }

    public void deleteNote(ContentModel selectedNote) {
        //context.getNoteService().deleteNote(selectedNote);
        //view.refreshNotesList();
    }

    public void saveNote(ContentModel selectedNote) {
        //context.getNoteService().saveNote(selectedNote);
    }

    public void refreshNotesList() {
        view.getModel().clear();
        view.getModel().addAll(getNotesList());
    }
}
