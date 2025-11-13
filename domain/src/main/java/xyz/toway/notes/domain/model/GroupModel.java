package xyz.toway.notes.domain.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class GroupModel {
    private String id;
    private String title;
    private String icon;

    @Setter(AccessLevel.NONE)
    private String parentId;

    private List<GroupModel> children = new ArrayList<>();

    public GroupModel(String id, String title, String icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public GroupModel(String title) {
        this.title = title;
    }

    public GroupModel(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public void addChild(GroupModel child) {
        if (!children.contains(child)) {
            children.add(child);
            child.parentId = id;
        }
    }

    public Iterable<GroupModel> getChildren() {
        return children;
    }

    public void clearChildren() {
        children.clear();
    }
}
