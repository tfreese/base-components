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
 * Basis TreeModel, welches die Verwendung von normalen Objekten ermöglicht ohne jedesmal einen TreeNode erzeugen zu müssen.
 *
 * @author Thomas Freese
 */
public abstract class AbstractObjectTreeModel implements TreeModel
{
    /**
     *
     */
    private final EventListenerList eventListenerList = new EventListenerList();
    /**
     *
     */
    private final Map<Object, List<?>> treeCache = new HashMap<>();

    /**
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    @Override
    public void addTreeModelListener(final TreeModelListener listener)
    {
        synchronized (this.eventListenerList)
        {
            this.eventListenerList.add(TreeModelListener.class, listener);
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type. The event instance is lazily created using the parameters
     * passed into the fire method.
     *
     * @param source the node being changed
     * @param pathToRoot the path to the root node
     * @param childIndices the indices of the changed elements
     * @param children the changed elements
     *
     * @see EventListenerList
     */
    protected void fireTreeNodesChanged(final Object source, final Object[] pathToRoot, final int[] childIndices, final Object[] children)
    {
        synchronized (this.eventListenerList)
        {
            // Guaranteed to return a non-null array
            Object[] listeners = this.eventListenerList.getListenerList();
            TreeModelEvent e = null;

            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
                if (listeners[i] == TreeModelListener.class)
                {
                    // Lazily create the event:
                    if (e == null)
                    {
                        e = new TreeModelEvent(source, pathToRoot, childIndices, children);
                    }

                    ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
                }
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type. The event instance is lazily created using the parameters
     * passed into the fire method.
     *
     * @param source the node where new elements are being inserted
     * @param pathToRoot the path to the root node
     * @param childIndices the indices of the new elements
     * @param children the new elements
     *
     * @see EventListenerList
     */
    protected void fireTreeNodesInserted(final Object source, final Object[] pathToRoot, final int[] childIndices, final Object[] children)
    {
        synchronized (this.eventListenerList)
        {
            // Guaranteed to return a non-null array
            Object[] listeners = this.eventListenerList.getListenerList();
            TreeModelEvent e = null;

            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
                if (listeners[i] == TreeModelListener.class)
                {
                    // Lazily create the event:
                    if (e == null)
                    {
                        e = new TreeModelEvent(source, pathToRoot, childIndices, children);
                    }

                    ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
                }
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type. The event instance is lazily created using the parameters
     * passed into the fire method.
     *
     * @param source the node where elements are being removed
     * @param pathToRoot the path to the root node
     * @param childIndices the indices of the removed elements
     * @param children the removed elements
     *
     * @see EventListenerList
     */
    protected void fireTreeNodesRemoved(final Object source, final Object[] pathToRoot, final int[] childIndices, final Object[] children)
    {
        synchronized (this.eventListenerList)
        {
            // Guaranteed to return a non-null array
            Object[] listeners = this.eventListenerList.getListenerList();
            TreeModelEvent e = null;

            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
                if (listeners[i] == TreeModelListener.class)
                {
                    // Lazily create the event:
                    if (e == null)
                    {
                        e = new TreeModelEvent(source, pathToRoot, childIndices, children);
                    }

                    ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
                }
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on this event type. The event instance is lazily created using the parameters
     * passed into the fire method.
     *
     * @param source the node where the tree model has changed
     * @param pathToRoot the path to the root node
     * @param childIndices the indices of the affected elements
     * @param children the affected elements
     *
     * @see EventListenerList
     */
    protected void fireTreeStructureChanged(final Object source, final Object[] pathToRoot, final int[] childIndices, final Object[] children)
    {
        synchronized (this.eventListenerList)
        {
            // Guaranteed to return a non-null array
            Object[] listeners = this.eventListenerList.getListenerList();
            TreeModelEvent e = null;

            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2)
            {
                if (listeners[i] == TreeModelListener.class)
                {
                    // Lazily create the event:
                    if (e == null)
                    {
                        e = new TreeModelEvent(source, pathToRoot, childIndices, children);
                    }

                    ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
                }
            }
        }
    }

    /**
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    @Override
    public Object getChild(final Object parent, final int index)
    {
        return getChilds(parent).get(index);
    }

    /**
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    @Override
    public int getChildCount(final Object parent)
    {
        return getChilds(parent).size();
    }

    /**
     * Liefert für einen Parent die entsprechende {@link List} der Child-Objecte.
     *
     * @param parent Object
     *
     * @return {@link List}
     */
    protected abstract List<?> getChilds(Object parent);

    /**
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    @Override
    public int getIndexOfChild(final Object parent, final Object child)
    {
        return getChilds(parent).indexOf(child);
    }

    /**
     * Liefert den Parent für das Child Objekt. Durchläuft dabei den Baum rekursiv durch die Pfade.
     *
     * @param child Object
     *
     * @return Object
     */
    public Object getParentFor(final Object child)
    {
        return getParentFor(getRoot(), child);
    }

    /**
     * Wird von getParentFor(Object child) mit Root als Parent aufgerufen und durchsucht den Baum rekursiv durch die Pfade.
     *
     * @param parent Object
     * @param child Object
     *
     * @return Object
     */
    protected Object getParentFor(final Object parent, final Object child)
    {
        Object found = null;
        int childCount = getChildCount(parent);

        try
        {
            for (int index = 0; index < childCount; index++)
            {
                Object object = getChild(parent, index);

                if (object == child)
                {
                    found = parent;

                    break;
                }

                found = getParentFor(object, child);

                if (found != null)
                {
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            // Do nothing
        }

        return found;
    }

    /**
     * Liefert den Pfad vom Root zum Objekt.
     *
     * @param object Object
     *
     * @return Object[]
     */
    public Object[] getPathToRoot(final Object object)
    {
        List<Object> list = new ArrayList<>();

        list.add(object);

        Object parent = getParentFor(object);

        while (parent != null)
        {
            list.add(0, parent);
            parent = getParentFor(parent);
        }

        return list.toArray();
    }

    /**
     * Liefert den TreeCache.
     *
     * @return {@link Map}
     */
    protected Map<Object, List<?>> getTreeCache()
    {
        return this.treeCache;
    }

    /**
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    @Override
    public boolean isLeaf(final Object node)
    {
        return ((getChilds(node) == null) || (getChilds(node).isEmpty()));
    }

    /**
     * Invoke this method after you've changed how node is to be represented in the tree.
     *
     * @param node Object
     */
    public void nodeChanged(final Object node)
    {
        synchronized (this.eventListenerList)
        {
            if ((this.eventListenerList != null) && (node != null))
            {
                Object parent = getParentFor(node);

                if (parent != null)
                {
                    int anIndex = getIndexOfChild(parent, node);

                    if (anIndex != -1)
                    {
                        int[] cIndexs = new int[1];

                        cIndexs[0] = anIndex;
                        nodesChanged(parent, cIndexs);
                    }
                }
                else if (node == getRoot())
                {
                    nodesChanged(node, null);
                }
            }
        }
    }

    /**
     * Feuert ein fireTreeNodesInserted Event, fuer diesen Node.
     *
     * @param node Object
     */
    public void nodeInserted(final Object node)
    {
        if (node == null)
        {
            return;
        }

        Object parent = getParentFor(node);

        if (parent == null)
        {
            return;
        }

        int index = getIndexOfChild(parent, node);

        if (index == -1)
        {
            return;
        }

        nodesWereInserted(parent, new int[]
        {
                index
        });

        // Liste des Parents entfernen
        // getTreeCache().put(parent, null);
    }

    /**
     * Feuert ein fireTreeNodesRemoved Event, fuer diesen Node.
     *
     * @param node Object
     */
    public void nodeRemoved(final Object node)
    {
        if (node == null)
        {
            return;
        }

        Object parent = getParentFor(node);

        if (parent == null)
        {
            return;
        }

        int index = getIndexOfChild(parent, node);

        if (index == -1)
        {
            return;
        }

        nodesWereRemoved(parent, new int[]
        {
                index
        }, new Object[]
        {
                node
        });

        getChilds(parent).remove(node);

        // Liste des Parents entfernen
        // getTreeCache().put(parent, null);
    }

    /**
     * Invoke this method after you've changed how the children identified by childIndicies are to be represented in the tree.
     *
     * @param node Object
     * @param childIndices int[]
     */
    public void nodesChanged(final Object node, final int[] childIndices)
    {
        if (node != null)
        {
            if (childIndices != null)
            {
                int cCount = childIndices.length;

                if (cCount > 0)
                {
                    Object[] cChildren = new Object[cCount];

                    for (int counter = 0; counter < cCount; counter++)
                    {
                        cChildren[counter] = getChild(node, childIndices[counter]);
                    }

                    fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
                }
            }
            else if (node == getRoot())
            {
                fireTreeNodesChanged(this, getPathToRoot(node), null, null);
            }
        }
    }

    /**
     * Invoke this method if you've totally changed the children of node and its childrens children... This will post a treeStructureChanged event.
     *
     * @param node Object
     */
    public void nodeStructureChanged(final Object node)
    {
        if (node != null)
        {
            getTreeCache().clear();

            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Invoke this method after you've inserted some TreeNodes into node. childIndices should be the index of the new elements and must be sorted in ascending
     * order.
     *
     * @param node Object
     * @param childIndices int[]
     */
    public void nodesWereInserted(final Object node, final int[] childIndices)
    {
        synchronized (this.eventListenerList)
        {
            if ((this.eventListenerList != null) && (node != null) && (childIndices != null) && (childIndices.length > 0))
            {
                int cCount = childIndices.length;
                Object[] newChildren = new Object[cCount];

                for (int counter = 0; counter < cCount; counter++)
                {
                    newChildren[counter] = getChild(node, childIndices[counter]);
                }

                fireTreeNodesInserted(this, getPathToRoot(node), childIndices, newChildren);
            }
        }
    }

    /**
     * Invoke this method after you've removed some TreeNodes from node. childIndices should be the index of the removed elements and must be sorted in
     * ascending order. And removedChildren should be the array of the children objects that were removed.
     *
     * @param node Object
     * @param childIndices int[]
     * @param removedChildren int[]
     */
    public void nodesWereRemoved(final Object node, final int[] childIndices, final Object[] removedChildren)
    {
        if ((node != null) && (childIndices != null))
        {
            fireTreeNodesRemoved(this, getPathToRoot(node), childIndices, removedChildren);
        }
    }

    /**
     * Invoke this method if you've modified the TreeNodes upon which this model depends. The model will notify all of its listeners that the model has changed
     * below the node <code>node</code> (PENDING).
     *
     * @param node Object
     */
    public void reload(final Object node)
    {
        if (node != null)
        {
            getTreeCache().clear();

            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    @Override
    public void removeTreeModelListener(final TreeModelListener listener)
    {
        synchronized (this.eventListenerList)
        {
            this.eventListenerList.remove(TreeModelListener.class, listener);
        }
    }

    /**
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
     */
    @Override
    public void valueForPathChanged(final TreePath path, final Object newValue)
    {
        // NOOP
    }
}
