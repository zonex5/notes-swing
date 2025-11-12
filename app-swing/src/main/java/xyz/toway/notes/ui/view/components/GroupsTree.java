package xyz.toway.notes.ui.view.components;

import lombok.Getter;
import lombok.Setter;
import xyz.toway.notes.domain.model.GroupModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;

import static xyz.toway.notes.ui.Main.icon;

public class GroupsTree extends DnDTree {

    private static final String UNGROUPED = "<Ungrouped>";

    @Setter
    private ImageIcon rootIcon;
    @Getter
    private final DefaultMutableTreeNode ungroupedNode;

    public GroupsTree(String rootLabel) {
        super(rootLabel);

        UIManager.put("Tree.selectionBackground", new Color(0xCCE8FF));  // light blue
        UIManager.put("Tree.selectionForeground", Color.BLACK);
        UIManager.put("Tree.rowHeight", 28);

        UIManager.put("Tree.dropCellBackground", Color.GREEN);
        UIManager.put("Tree.dropCellForeground", Color.BLUE);

        SwingUtilities.updateComponentTreeUI(this);

        rootIcon = icon("/icons/ui/root.svg", 16, 16);

        ungroupedNode = addSpecialNodeUnderRoot(UNGROUPED);
        ungroupedNode.setAllowsChildren(false);

        setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.isRoot()) {
                    setIcon(rootIcon);
                } else {
                    if (!node.getAllowsChildren()) {
                        setIcon(icon("/icons/app/default.svg", 16, 16));
                    }
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

    public DefaultMutableTreeNode getSelectedTreeNode() {
        TreePath path = getSelectionPath();
        if (path == null) {
            return null;
        }
        Object last = path.getLastPathComponent();
        if (last instanceof DefaultMutableTreeNode node) {
            return node;
        }
        return null;
    }

    public boolean isUngroupedNodeSelected() {
        return getSelectedTreeNode() == getUngroupedNode();
    }

}
