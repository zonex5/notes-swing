package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.GroupModel;
import xyz.toway.notes.ui.view.INotesView;

import java.util.Collection;

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
        context.getNoteService().deleteNote(selectedNote);
    }

    @Override
    public void saveNote(ContentModel selectedNote) {
        context.getNoteService().saveNote(selectedNote);
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
        context.getNoteService()
                .createGroup(new GroupModel(title))
                .thenAccept(model -> view.refresh());
    }

    @Override
    public void saveGroup(GroupModel group) {
        context.getNoteService()
                .saveGroup(group)
                .thenAccept(model -> view.refresh());
    }

    @Override
    public void deleteGroup(GroupModel group) {
        context.getNoteService()
                .deleteGroup(group)
                .thenAccept(model -> view.refresh());
    }

    @Override
    public void loadGroups() {
        context.getNoteService()
                .loadGroups()
                .thenAccept(data -> view.setGroups(data));
    }
}
