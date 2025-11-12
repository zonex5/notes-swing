package xyz.toway.notes.ui.view;

import xyz.toway.notes.domain.model.ContentModel;
import xyz.toway.notes.domain.model.GroupModel;

import java.util.List;

public interface INotesView extends GeneralView {

    void setNotes(List<ContentModel> notes);

    void setGroups(List<GroupModel> groups);
}
