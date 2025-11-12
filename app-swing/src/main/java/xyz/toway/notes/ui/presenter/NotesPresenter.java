package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.GroupModel;
import xyz.toway.notes.ui.view.INotesView;

import java.util.Collection;
import java.util.List;

import static xyz.toway.notes.ui.Main.context;

public class NotesPresenter implements INotesPresenter<GroupModel> {

    private INotesView view;

    @Override
    public void setView(INotesView view) {
        this.view = view;
    }

    @Override
    public INotesView getView() {
        return view;
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
    public void loadNotes(Collection<String> parentIds) {
        context.getNoteService()
                .findAllByParents(parentIds)
                .thenAccept(data -> view.setNotes(data));
    }

    @Override
    public void loadAllNotes() {
        context.getNoteService()
                .findAll()
                .thenAccept(data -> view.setNotes(data));
    }

    @Override
    public void loadOrphanNotes() {
        context.getNoteService()
                .findAllOrphans()
                .thenAccept(data -> view.setNotes(data));
    }

    @Override
    public void addNewGroup(String title) {
        /*context.getNoteService().createGroup(new GroupModel(title))
                .thenAccept(data ->)*/
    }

    @Override
    public void loadGroups() {
        //GroupModel model1 = new GroupModel("1", "Odin", "home");
        //GroupModel model2 = new GroupModel("2", "Dva", "home");
        //GroupModel model3 = new GroupModel("3", "Tri", "home");
        //model2.addChild(model3);
        //var result = List.of(model1, model2);
        //view.setGroups(result);

        context.getNoteService()
                .loadGroups()
                .thenAccept(data -> view.setGroups(data));
    }
}
