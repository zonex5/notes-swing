package xyz.toway.notes.ui.view.components;

import lombok.Setter;
import xyz.toway.notes.domain.model.GroupModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

import static xyz.toway.notes.ui.Main.icon;

public class GroupsTree extends DnDTree {

    @Setter
    private ImageIcon rootIcon;

    public GroupsTree(String rootLabel) {
        super(rootLabel);

        UIManager.put("Tree.selectionBackground", new Color(0xCCE8FF));  // light blue
        UIManager.put("Tree.selectionForeground", Color.BLACK);
        UIManager.put("Tree.rowHeight", 28);

        UIManager.put("Tree.dropCellBackground", Color.GREEN);
        UIManager.put("Tree.dropCellForeground", Color.BLUE);

        SwingUtilities.updateComponentTreeUI(this);

        rootIcon = icon("/icons/ui/root.svg", 16, 16);

        setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.isRoot()) {
                    setIcon(rootIcon);
                } else {
                    if (node.getUserObject() instanceof GroupModel groupModel) {
                        var iconText = groupModel.getIcon() != null && !groupModel.getIcon().isEmpty() ? groupModel.getIcon() : null;
                        setIcon(icon("/icons/app/" + iconText + ".svg", 16, 16));
                        setText(groupModel.getTitle());
                    }
                }
                return c;
            }
        });
    }
}
