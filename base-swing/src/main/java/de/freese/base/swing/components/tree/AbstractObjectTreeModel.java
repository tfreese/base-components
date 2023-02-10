package de.freese.base.swing.components.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Basis TreeModel, welches die Verwendung von normalen Objekten ermöglicht ohne jedes Mal einen TreeNode erzeugen zu müssen.
 *
 * @author Thomas Freese
 */
public abstract class AbstractObjectTreeModel implements TreeModel {
    private final EventListenerList eventListenerList = new EventListenerList();

    private final Map<Object, List<?>> treeCache = new HashMap<>();

    /**
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    @Override
    public void addTreeModelListener(final TreeModelListener listener) {
        synchronized (this.eventListenerList) {
            this.eventListenerList.add(TreeModelListener.class, listener);
        }
    }

    /**
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    @Override
    public Object getChild(final Object parent, final int index) {
        return getChildren(parent).get(index);
    }

    /**
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    @Override
    public int getChildCount(final Object parent) {
        return getChildren(parent).size();
    }

    /**
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        return getChildren(parent).indexOf(child);
    }

    /**
     * Liefert den Parent für das Child Objekt.<br/>
     * Durchläuft dabei den Baum rekursiv durch die Pfade.
     */
    public Object getParentFor(final Object child) {
        return getParentFor(getRoot(), child);
    }

    /**
     * Liefert den Pfad vom Root zum Objekt.
     */
    public Object[] getPathToRoot(final Object object) {
        List<Object> list = new ArrayList<>();

        list.add(object);

        Object parent = getParentFor(object);

        while (parent != null) {
            list.add(0, parent);
            parent = getParentFor(parent);
        }

        return list.toArray();
    }

    /**
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    @Override
    public boolean isLeaf(final Object node) {
        return ((getChildren(node) == null) || (getChildren(node).isEmpty()));
    }

    /**
     * Invoke this method after you've changed how node is to be represented in the tree.
     */
    public void nodeChanged(final Object node) {
        synchronized (this.eventListenerList) {
            if ((this.eventListenerList != null) && (node != null)) {
                Object parent = getParentFor(node);

                if (parent != null) {
                    int anIndex = getIndexOfChild(parent, node);

                    if (anIndex != -1) {
                        int[] cIndexes = new int[1];

                        cIndexes[0] = anIndex;
                        nodesChanged(parent, cIndexes);
                    }
                }
                else if (node == getRoot()) {
                    nodesChanged(node, null);
                }
            }
        }
    }

    /**
     * Feuert ein fireTreeNodesInserted Event, für diesen Node.
     */
    public void nodeInserted(final Object node) {
        if (node == null) {
            return;
        }

        Object parent = getParentFor(node);

        if (parent == null) {
            return;
        }

        int index = getIndexOfChild(parent, node);

        if (index == -1) {
            return;
        }

        nodesWereInserted(parent, new int[]{index});

        // Liste des Parents entfernen
        // getTreeCache().put(parent, null);
    }

    /**
     * Feuert ein fireTreeNodesRemoved Event, für diesen Node.
     */
    public void nodeRemoved(final Object node) {
        if (node == null) {
            return;
        }

        Object parent = getParentFor(node);

        if (parent == null) {
            return;
        }

        int index = getIndexOfChild(parent, node);

        if (index == -1) {
            return;
        }

        nodesWereRemoved(parent, new int[]{index}, new Object[]{node});

        getChildren(parent).remove(node);

        // Liste des Parents entfernen
        // getTreeCache().put(parent, null);
    }

    /**
     * Invoke this method if you've totally changed the children of node and its children children.<br/>
     * This will post a treeStructureChanged event.
     */
    public void nodeStructureChanged(final Object node) {
        if (node != null) {
            getTreeCache().clear();

            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Invoke this method after you've changed how the children identified by childIndices are to be represented in the tree.
     */
    public void nodesChanged(final Object node, final int[] childIndices) {
        if (node == null) {
            return;
        }

        if (childIndices != null) {
            int cCount = childIndices.length;

            if (cCount > 0) {
                Object[] cChildren = new Object[cCount];

                for (int counter = 0; counter < cCount; counter++) {
                    cChildren[counter] = getChild(node, childIndices[counter]);
                }

                fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
            }
        }
        else if (node == getRoot()) {
            fireTreeNodesChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Invoke this method after you've inserted some TreeNodes into node.<br/>
     * ChildIndices should be the index of the new elements and must be sorted in ascending order.
     */
    public void nodesWereInserted(final Object node, final int[] childIndices) {
        synchronized (this.eventListenerList) {
            if ((this.eventListenerList != null) && (node != null) && (childIndices != null) && (childIndices.length > 0)) {
                int cCount = childIndices.length;
                Object[] newChildren = new Object[cCount];

                for (int counter = 0; counter < cCount; counter++) {
                    newChildren[counter] = getChild(node, childIndices[counter]);
                }

                fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
            }
        }
    }

    /**
     * Invoke this method after you've removed some TreeNodes from node.<br/>
     * ChildIndices should be the index of the removed elements and must be sorted in  ascending order.<br/>
     * And removedChildren should be the array of the children objects that were removed.
     */
    public void nodesWereRemoved(final Object node, final int[] childIndices, final Object[] removedChildren) {
        if ((node != null) && (childIndices != null)) {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, removedChildren);
        }
    }

    /**
     * Invoke this method if you've modified the TreeNodes upon which this model depends.<br/>
     * The model will notify all of its listeners that the model has changed below the node <code>node</code> (PENDING).
     */
    public void reload(final Object node) {
        if (node != null) {
            getTreeCache().clear();

            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    @Override
    public void removeTreeModelListener(final TreeModelListener listener) {
        synchronized (this.eventListenerList) {
            this.eventListenerList.remove(TreeModelListener.class, listener);
        }
    }

    /**
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
     */
    @Override
    public void valueForPathChanged(final TreePath path, final Object newValue) {
        // Empty
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type.<br/>
     * The event instance is lazily created using the parameters passed into the fire method.
     */
    protected void fireTreeNodesChanged(final Object source, final Object[] pathToRoot, final int[] childIndices, final Object[] children) {
        synchronized (this.eventListenerList) {
            // Guaranteed to return a non-null array
            Object[] listeners = this.eventListenerList.getListenerList();
            TreeModelEvent e = null;

            // Process the listeners last to first, notifying those that are interested in this event.
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TreeModelListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new TreeModelEvent(source, pathToRoot, childIndices, children);
                    }

                    ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
                }
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type.<br/>
     * The event instance is lazily created using the parameters passed into the fire method.
     */
    protected void fireTreeNodesInserted(final Object source, final Object[] pathToRoot, final int[] childIndices, final Object[] children) {
        synchronized (this.eventListenerList) {
            // Guaranteed to return a non-null array
            Object[] listeners = this.eventListenerList.getListenerList();
            TreeModelEvent e = null;

            // Process the listeners last to first, notifying those that are interested in this event.
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TreeModelListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new TreeModelEvent(source, pathToRoot, childIndices, children);
                    }

                    ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
                }
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type.<br/>
     * The event instance is lazily created using the parameters  passed into the fire method.
     */
    protected void fireTreeNodesRemoved(final Object source, final Object[] pathToRoot, final int[] childIndices, final Object[] children) {
        synchronized (this.eventListenerList) {
            // Guaranteed to return a non-null array
            Object[] listeners = this.eventListenerList.getListenerList();
            TreeModelEvent e = null;

            // Process the listeners last to first, notifying those that are interested in this event.
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TreeModelListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new TreeModelEvent(source, pathToRoot, childIndices, children);
                    }

                    ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
                }
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type.<br/>
     * The event instance is lazily created using the parameters passed into the fire method.
     */
    protected void fireTreeStructureChanged(final Object source, final Object[] pathToRoot, final int[] childIndices, final Object[] children) {
        synchronized (this.eventListenerList) {
            // Guaranteed to return a non-null array
            Object[] listeners = this.eventListenerList.getListenerList();
            TreeModelEvent e = null;

            // Process the listeners last to first, notifying those that are interested in this event.
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == TreeModelListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new TreeModelEvent(source, pathToRoot, childIndices, children);
                    }

                    ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
                }
            }
        }
    }

    /**
     * Liefert für einen Parent die entsprechende {@link List} der Child-Objekte.
     */
    protected abstract List<?> getChildren(Object parent);

    /**
     * Wird von getParentFor(Object child) mit Root als Parent aufgerufen und durchsucht den Baum rekursiv durch die Pfade.
     */
    protected Object getParentFor(final Object parent, final Object child) {
        Object found = null;
        int childCount = getChildCount(parent);

        try {
            for (int index = 0; index < childCount; index++) {
                Object object = getChild(parent, index);

                if (object == child) {
                    found = parent;

                    break;
                }

                found = getParentFor(object, child);

                if (found != null) {
                    break;
                }
            }
        }
        catch (Exception ex) {
            // Do nothing
        }

        return found;
    }

    protected Map<Object, List<?>> getTreeCache() {
        return this.treeCache;
    }
}
