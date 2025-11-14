package xyz.toway.notes.ui.view.components;

import lombok.Setter;
import xyz.toway.notes.domain.model.GroupModel;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * JTree with MOVE DnD and a single rebuild(List<GroupModel>) entrypoint.
 * - Regular nodes: draggable and droppable.
 * - Special nodes: cannot be dragged and cannot receive drops.
 * <p>
 * All user-facing comments are in English as requested.
 */
public class DnDTree extends JTree {

    // Special nodes are tracked by reference. Re-mark them if you rebuild with new node instances.
    private final Set<DefaultMutableTreeNode> specialNodes = Collections.newSetFromMap(new IdentityHashMap<>());

    @Setter
    private ExternalDropHandler externalDropHandler;

    @Setter
    private BiConsumer<GroupModel, GroupModel> onMoveDone;

    public DnDTree(String rootLabel) {
        super(new DefaultTreeModel(new DefaultMutableTreeNode(rootLabel != null ? rootLabel : "Root")));
        setRootVisible(false);
        setShowsRootHandles(true);
        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
        setTransferHandler(new MoveHandler());
        expandRow(0);
    }

    // ---------- Public API ----------

    /**
     * Rebuilds the whole tree from top-level GroupModel parents.
     */
    public void rebuild(List<GroupModel> parents) {
        DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode(root().getUserObject());

        for (DefaultMutableTreeNode special : specialNodes) {
            newRoot.add(special);
        }

        if (parents != null) {
            for (GroupModel g : parents) {
                newRoot.add(toNode(g));
            }
        }

        model().setRoot(newRoot);
        expandAll();
    }

    /**
     * Marks a node as special (no drag, no drop-into). You must add this node into the tree yourself.
     */
    public void addSpecialNode(DefaultMutableTreeNode node) {
        if (node != null) specialNodes.add(node);
    }

    /**
     * Removes special mark from a node.
     */
    public void removeSpecialNode(DefaultMutableTreeNode node) {
        if (node != null) specialNodes.remove(node);
    }

    /**
     * Adds a special node under root and returns it.
     */
    public DefaultMutableTreeNode addSpecialNodeUnderRoot(String label) {
        DefaultMutableTreeNode n = new DefaultMutableTreeNode(label);
        model().insertNodeInto(n, root(), root().getChildCount());
        addSpecialNode(n);
        return n;
    }

    /**
     * Inserts a child under root at index with model events.
     */
    public void insertAtRoot(DefaultMutableTreeNode child, int index) {
        if (child == null) return;
        index = Math.max(0, Math.min(index, root().getChildCount()));
        model().insertNodeInto(child, root(), index);
    }

    /**
     * Sets root label.
     */
    public void setRootLabel(String text) {
        root().setUserObject(text != null ? text : "Root");
        model().nodeChanged(root());
    }

    public DefaultTreeModel model() {
        return (DefaultTreeModel) getModel();
    }

    public DefaultMutableTreeNode root() {
        return (DefaultMutableTreeNode) model().getRoot();
    }

    /**
     * Expands all rows.
     */
    public void expandAll() {
        for (int i = 0; i < getRowCount(); i++) expandRow(i);
    }

    // ---------- Mapping GroupModel -> Swing nodes ----------

    private DefaultMutableTreeNode toNode(GroupModel g) {
        // Store the model as user object so renderer can show title later.
        DefaultMutableTreeNode n = new DefaultMutableTreeNode(g);
        Iterable<GroupModel> kids = g.getChildren();
        if (kids != null) {
            for (GroupModel ch : kids) {
                n.add(toNode(ch));
            }
        }
        return n;
    }

    // ---------- DnD handler (MOVE) ----------

    private final class MoveHandler extends TransferHandler {
        private final DataFlavor NODE_FLAVOR;
        private DefaultMutableTreeNode dragged;

        MoveHandler() {
            try {
                NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + DefaultMutableTreeNode.class.getName());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree t = (JTree) c;
            TreePath sel = t.getSelectionPath();
            if (sel == null) return null;

            dragged = (DefaultMutableTreeNode) sel.getLastPathComponent();

            // Do not drag root or any special node
            if (dragged.isRoot() || specialNodes.contains(dragged)) return null;

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
                public Object getTransferData(DataFlavor f) throws UnsupportedFlavorException, IOException {
                    if (!isDataFlavorSupported(f)) throw new UnsupportedFlavorException(f);
                    return dragged;
                }
            };
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport s) {
            if (!s.isDrop()) return false;

            JTree.DropLocation dl = (JTree.DropLocation) s.getDropLocation();
            if (dl == null || dl.getPath() == null) return false;

            DefaultMutableTreeNode target = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();

            if (!s.isDataFlavorSupported(NODE_FLAVOR)) {
                if (externalDropHandler == null) return false;
                boolean allowed = externalDropHandler.canImport(s, target, dl.getChildIndex());
                if (allowed) {
                    s.setDropAction(MOVE);
                }
                return allowed;
            }

            // Forbid drop ON a special node
            if (dl.getChildIndex() == -1 && specialNodes.contains(target)) return false;

            // Forbid INSERT into a special parent
            if (dl.getChildIndex() >= 0) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) target.getParent();
                if (parent != null && specialNodes.contains(parent)) return false;
            }

            // Standard self/descendant checks
            if (dragged != null) {
                if (target == dragged || isDescendant(dragged, target)) return false;
            }

            s.setDropAction(MOVE);
            return true;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport s) {
            if (!canImport(s)) return false;
            if (!s.isDataFlavorSupported(NODE_FLAVOR)) {
                if (externalDropHandler == null) return false;
                JTree.DropLocation dl = (JTree.DropLocation) s.getDropLocation();
                if (dl == null || dl.getPath() == null) return false;
                DefaultMutableTreeNode target = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();
                return externalDropHandler.importData(s, target, dl.getChildIndex());
            }
            try {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) s.getTransferable().getTransferData(NODE_FLAVOR);

                JTree t = (JTree) s.getComponent();
                DefaultTreeModel m = (DefaultTreeModel) t.getModel();
                JTree.DropLocation dl = (JTree.DropLocation) s.getDropLocation();
                DefaultMutableTreeNode target = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();

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

                // Enforce special-node rule again
                if (specialNodes.contains(newParent)) return false;

                if (isDescendant(node, newParent)) return false;

                DefaultMutableTreeNode oldParent = (DefaultMutableTreeNode) node.getParent();
                int oldIndex = oldParent != null ? oldParent.getIndex(node) : -1;

                if (oldParent != null) m.removeNodeFromParent(node);
                if (newParent == oldParent && oldIndex >= 0 && index > oldIndex) index--;

                m.insertNodeInto(node, newParent, Math.max(0, Math.min(index, newParent.getChildCount())));

                TreePath newPath = new TreePath(m.getPathToRoot(node));
                t.scrollPathToVisible(newPath);
                t.setSelectionPath(newPath);

                if (onMoveDone != null
                        && node.getUserObject() instanceof GroupModel targetModel
                        && newParent.getUserObject() instanceof GroupModel parentModel) {
                    onMoveDone.accept(targetModel, parentModel);
                }

                return true;
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            dragged = null;
        }

        private boolean isDescendant(DefaultMutableTreeNode a, DefaultMutableTreeNode b) {
            if (a == null || b == null) return false;
            if (a == b) return true;
            for (TreeNode n = b.getParent(); n != null; n = n.getParent()) {
                if (n == a) return true;
            }
            return false;
        }
    }

    @FunctionalInterface
    public interface ExternalDropHandler {
        default boolean canImport(TransferHandler.TransferSupport support, DefaultMutableTreeNode target, int childIndex) {
            return true;
        }

        boolean importData(TransferHandler.TransferSupport support, DefaultMutableTreeNode target, int childIndex);
    }
}
