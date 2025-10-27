package xyz.toway.notes.ui.view.components;

import javax.swing.*;

public class ListItem {
    private String title;
    private String subtitle;
    private Icon icon;

    public ListItem(String title, String subtitle, Icon icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }

    public ListItem(String title, Icon icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Icon getIcon() {
        return icon;
    }
}