package xyz.toway.notes.ui.view.components;

import lombok.Getter;
import xyz.toway.notes.domain.model.GroupModel;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DnDTree extends JTree {

    public DnDTree(String rootLabel) {
        super(new DefaultMutableTreeNode(rootLabel));
        setRootVisible(true);
        setShowsRootHandles(true);
        getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
        setTransferHandler(new NodeMoveTransferHandler());
        expandRow(0);
    }

    public DefaultTreeModel model() {
        return (DefaultTreeModel) getModel();
    }

    public DefaultMutableTreeNode root() {
        return (DefaultMutableTreeNode) model().getRoot();
    }

    public void setRootLabel(String text) {
        root().setUserObject(Objects.requireNonNullElse(text, "Root"));
        model().nodeChanged(root());
    }

    public void rebuild(List<GroupModel> parents) {
        root().removeAllChildren();
        if (parents != null) {
            for (GroupModel g : parents) {
                root().add(toNode(g));
            }
        }
        expandAll();
    }

    /**
     * Maps a GroupModel subtree to DefaultMutableTreeNode recursively.
     */
    private DefaultMutableTreeNode toNode(GroupModel g) {
        // Store the model itself as user object for future access
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(g);
        StreamSupport.stream(g.getChildren().spliterator(), false)
                .forEach(child -> node.add(toNode(child)));
        return node;
    }

    public void expandAll() {
        for (int i = 0; i < getRowCount(); i++) expandRow(i);
    }

    private static final class NodeMoveTransferHandler extends TransferHandler {
        private static final DataFlavor NODE_FLAVOR;

        static {
            try {
                NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
                        ";class=" + DefaultMutableTreeNode.class.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        private DefaultMutableTreeNode draggedNode;

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            var tree = (JTree) c;
            var sel = tree.getSelectionPath();
            if (sel == null) return null;
            draggedNode = (DefaultMutableTreeNode) sel.getLastPathComponent();
            if (draggedNode.isRoot()) return null;
            return new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{NODE_FLAVOR};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor f) {
                    return NODE_FLAVOR.equals(f);
                }

                @Override
                public Object getTransferData(DataFlavor f) throws UnsupportedFlavorException {
                    if (!isDataFlavorSupported(f)) throw new UnsupportedFlavorException(f);
                    return draggedNode;
                }
            };
        }

        @Override
        public boolean canImport(TransferSupport support) {
            if (!support.isDrop()) return false;
            if (!support.isDataFlavorSupported(NODE_FLAVOR)) return false;

            var dl = (JTree.DropLocation) support.getDropLocation();
            if (dl == null || dl.getPath() == null) return false;

            var target = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();

            // Get reference to tree and its "Blocked" node todo
           /* if (support.getComponent() instanceof DnDTree tree) {
                DefaultMutableTreeNode blocked = tree.getBlockedNode();
                if (blocked != null && target == blocked) {
                    return false; // forbid dropping into "Ungrouped"
                }
            }*/

            if (draggedNode != null) {
                if (target == draggedNode || isDescendant(draggedNode, target)) return false;
            }

            support.setDropAction(MOVE);
            return true;
        }


        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) return false;

            try {
                var node = (DefaultMutableTreeNode)
                        support.getTransferable().getTransferData(NODE_FLAVOR);

                var tree = (JTree) support.getComponent();
                var model = (DefaultTreeModel) tree.getModel();
                var dl = (JTree.DropLocation) support.getDropLocation();
                var target = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();

                DefaultMutableTreeNode newParent;
                int index;
                if (dl.getChildIndex() == -1) {
                    newParent = target;
                    index = target.getChildCount();
                } else {
                    newParent = (DefaultMutableTreeNode) target.getParent();
                    if (newParent == null) return false;
                    index = dl.getChildIndex();
                }

                if (isDescendant(node, newParent)) return false;

                var oldParent = (DefaultMutableTreeNode) node.getParent();
                int oldIndex = oldParent != null ? oldParent.getIndex(node) : -1;

                if (oldParent != null) model.removeNodeFromParent(node);

                if (newParent == oldParent && oldIndex >= 0 && index > oldIndex) index--;

                model.insertNodeInto(node, newParent, Math.max(0, Math.min(index, newParent.getChildCount())));

                var newPath = new TreePath(model.getPathToRoot(node));
                tree.scrollPathToVisible(newPath);
                tree.setSelectionPath(newPath);
                return true;
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            draggedNode = null;
        }

        private static boolean isDescendant(DefaultMutableTreeNode a, DefaultMutableTreeNode b) {
            if (a == null || b == null) return false;
            if (a == b) return true;
            for (TreeNode n = b.getParent(); n != null; n = ((DefaultMutableTreeNode) n).getParent()) {
                if (n == a) return true;
            }
            return false;
        }
    }
}
