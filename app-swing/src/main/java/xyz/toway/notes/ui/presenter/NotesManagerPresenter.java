package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.ui.view.GeneralView;

import java.util.List;

import static xyz.toway.notes.ui.Main.context;

public class NotesManagerPresenter implements GeneralPresenter {

    private GeneralView view;

    @Override
    public void setView(GeneralView view) {
        this.view = view;
    }

    @Override
    public GeneralView getView() {
        return view;
    }

    public List<ContentModel> getNotesList() {
        return context.getNoteService().findAllLight();
    }

    public void deleteNote(ContentModel selectedNote) {
        context.getNoteService().delete(selectedNote);
    }

    public void saveNote(ContentModel selectedNote) {
        context.getNoteService().save(selectedNote);
    }

    public void refreshNotesList() {
        view.getModel().clear();
        view.getModel().addAll(getNotesList());
    }
}
