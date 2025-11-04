package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.ui.view.NotesManagerWindow;

import java.util.List;

import static xyz.toway.notes.ui.Main.context;

public class OpenPresenter implements GeneralPresenter<NotesManagerWindow> {

    @Override
    public void setView(NotesManagerWindow view) {

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
}
