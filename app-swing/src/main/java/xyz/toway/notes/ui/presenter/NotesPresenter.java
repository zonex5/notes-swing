package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.ui.view.GeneralView;

import java.util.List;

import static xyz.toway.notes.ui.Main.context;

public class NotesPresenter implements INotesPresenter {

    private GeneralView view;

    @Override
    public void setView(GeneralView view) {
        this.view = view;
    }

    @Override
    public GeneralView getView() {
        return view;
    }

    @Override
    public List<ContentModel> getNotesList() {
        return context.getNoteService().findAllLight();
    }

    @Override
    public void deleteNote(ContentModel selectedNote) {
        context.getNoteService().delete(selectedNote);
    }

    @Override
    public void saveNote(ContentModel selectedNote) {
        context.getNoteService().save(selectedNote);
    }

    @Override
    public void setViewData() {
        view.setData("notesList", getNotesList());
    }
}
