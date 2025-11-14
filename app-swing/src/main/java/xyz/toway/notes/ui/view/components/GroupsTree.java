package xyz.toway.notes.ui.view.components;

import lombok.Getter;
import lombok.Setter;
import xyz.toway.notes.domain.model.GroupModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.Objects;

import static xyz.toway.notes.ui.Main.icon;

public class GroupsTree extends DnDTree {

    private static final String UNGROUPED = "[Ungrouped]";
    private static final String ALL = "[All Notes]";

    @Getter
    private final DefaultMutableTreeNode ungroupedNode;

    @Getter
    private final DefaultMutableTreeNode allNode;

    public GroupsTree(String rootLabel) {
        super(rootLabel);

        UIManager.put("Tree.selectionBackground", new Color(0xCCE8FF));  // light blue
        UIManager.put("Tree.selectionForeground", Color.BLACK);
        UIManager.put("Tree.rowHeight", 28);

        UIManager.put("Tree.dropCellBackground", Color.LIGHT_GRAY);
        UIManager.put("Tree.dropCellForeground", Color.WHITE);

        SwingUtilities.updateComponentTreeUI(this);

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        ungroupedNode = addSpecialNodeUnderRoot(UNGROUPED);
        ungroupedNode.setAllowsChildren(false);

        allNode = addSpecialNodeUnderRoot(ALL);
        allNode.setAllowsChildren(false);

        setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node == getAllNode()) {
                    setIcon(icon("/icons/ui/root.svg", 16, 16));
                } else if (node == getUngroupedNode()) {
                    setIcon(icon("/icons/ui/ungrouped.svg", 16, 16));
                } else if (node.getUserObject() instanceof GroupModel groupModel) {
                    var iconText = groupModel.getIcon() != null && !groupModel.getIcon().isEmpty() ? groupModel.getIcon() : null;
                    var icon = iconText != null ? icon("/icons/app/" + iconText + ".svg", 16, 16) : null;
                    setIcon(Objects.requireNonNullElse(icon, icon("/icons/ui/groups.svg")));
                    setText(groupModel.getTitle());
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

/*    public boolean isUngroupedNodeSelected() {
        return getSelectedTreeNode() == getUngroupedNode();
    }

    public boolean isAllNodeSelected() {
        return getSelectedTreeNode() == getAllNode();
    }*/

    public boolean isUngroupedNode(DefaultMutableTreeNode node) {
        return node == getUngroupedNode();
    }

    public boolean isAllNode(DefaultMutableTreeNode node) {
        return node == getAllNode();
    }
}
