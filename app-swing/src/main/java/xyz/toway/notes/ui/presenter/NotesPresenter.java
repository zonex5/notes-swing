package xyz.toway.notes.ui.presenter;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.GroupModel;
import xyz.toway.notes.ui.view.GeneralView;

import java.util.List;
import java.util.function.Consumer;

import static xyz.toway.notes.ui.Main.context;

public class NotesPresenter implements INotesPresenter<GroupModel> {

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
    public void loadData() {
        view.setData("notesList", getNotesList());
    }

    @Override
    public void loadGroups(Consumer<List<GroupModel>> consumer) {
        GroupModel model1 = new GroupModel("1", "Odin", "home");
        GroupModel model2 = new GroupModel("2", "Dva", "home");
        GroupModel model3 = new GroupModel("3", "Tri", "home");
        model2.addChild(model3);

        var result = List.of(model1, model2);

        consumer.accept(result);
    }
}
