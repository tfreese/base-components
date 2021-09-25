package de.freese.base.utils;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Utilmethoden fuer einen JTree.
 *
 * @author Thomas Freese
 */
public final class TreeUtils
{
    /**
     * Zuklappen des gesamten Baumes.
     *
     * @param tree {@link JTree}
     */
    public static void collapse(final JTree tree)
    {
        Object root = tree.getModel().getRoot();

        if (root == null)
        {
            return;
        }

        // Traverse tree from root
        collapse(tree, new TreePath(root));
    }

    /**
     * Zuklappen des TreePaths.
     *
     * @param tree {@link JTree}
     * @param parent {@link TreePath}
     */
    public static void collapse(final JTree tree, final TreePath parent)
    {
        if (parent == null)
        {
            collapse(tree);

            return;
        }

        Object node = parent.getLastPathComponent();

        for (int i = 0; i < tree.getModel().getChildCount(node); i++)
        {
            Object n = tree.getModel().getChild(node, i);

            TreePath path = parent.pathByAddingChild(n);

            collapse(tree, path);
        }

        tree.collapsePath(parent);
    }

    /**
     * Aufklappen des gesamten Baumes.
     *
     * @param tree {@link JTree}
     */
    public static void expand(final JTree tree)
    {
        Object root = tree.getModel().getRoot();

        if (root == null)
        {
            return;
        }

        expand(tree, new TreePath(root));
    }

    /**
     * Aufklappen des Baumes bis zu einem bestimmen Level.
     *
     * @param tree {@link JTree}
     * @param level int
     */
    public static void expand(final JTree tree, final int level)
    {
        Object root = tree.getModel().getRoot();

        if (root == null)
        {
            return;
        }

        expand(tree, new TreePath(root), level);
    }

    /**
     * Aufklappen des TreePaths und aller seiner Children.
     *
     * @param tree {@link JTree}
     * @param parent {@link TreePath}
     */
    public static void expand(final JTree tree, final TreePath parent)
    {
        // Traverse children
        Object node = parent.getLastPathComponent();

        for (int i = 0; i < tree.getModel().getChildCount(node); i++)
        {
            Object n = tree.getModel().getChild(node, i);

            TreePath path = parent.pathByAddingChild(n);

            expand(tree, path);
        }

        tree.expandPath(parent);
    }

    /**
     * Methode expandiert den Baum bis auf das angegebende Level.
     *
     * @param tree {@link JTree}
     * @param path {@link TreePath}
     * @param level int
     */
    public static void expand(final JTree tree, final TreePath path, final int level)
    {
        // tree.expandPath(path);
        // DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        // for (int i = 0; i < node.getChildCount(); i++)
        // {
        // DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
        // if (child.getLevel() < level)
        // {
        // TreePath childPath = new TreePath(child.getPath());
        // expandTreeToLevel(tree, childPath, level);
        // }
        // }
        tree.expandPath(path);

        Object node = path.getLastPathComponent();
        int count = tree.getModel().getChildCount(node);
        int currLevel = path.getPathCount();

        for (int i = 0; i < count; i++)
        {
            Object child = tree.getModel().getChild(node, i);

            if (currLevel <= level)
            {
                Object[] arr = new Object[path.getPath().length + 1];

                // copy the old path
                for (int j = 0; j < path.getPath().length; j++)
                {
                    arr[j] = path.getPath()[j];
                }

                // add the latest path element
                arr[arr.length - 1] = child;
                expand(tree, new TreePath(arr), level);
            }
        }
    }

    /**
     * Liefert die ausgeklappten {@link TreePath}s.
     *
     * @param tree {@link JTree}
     *
     * @return {@link List}
     */
    public static List<TreePath> getExpandedTreePaths(final JTree tree)
    {
        List<TreePath> expanded = new ArrayList<>();

        // TreeModel treeModel = tree.getModel();
        // TreePath rootPath = new TreePath(treeModel.getRoot());
        // Enumeration<TreePath> expandedEnum = tree.getExpandedDescendants(rootPath);
        //
        // if (expandedEnum != null)
        // {
        // expanded = Collections.list(expandedEnum);
        // }

        for (int row = 0; row < tree.getRowCount(); row++)
        {
            if (tree.isExpanded(row))
            {
                expanded.add(tree.getPathForRow(row));
            }
        }

        return expanded;
    }

    /**
     * Liefert das erste selektierte Object.
     *
     * @param tree {@link JTree}
     *
     * @return Object
     */
    public static Object getFirstSelectedObject(final JTree tree)
    {
        Object[] obj = getSelectedObjects(tree);

        if ((obj != null) && (obj.length > 0))
        {
            return obj[0];
        }

        return null;
    }

    /**
     * Liefert den Parent fuer das Child Objekt. Durchlaeuft dabei den Baum rekursiv durch die Pfade.
     *
     * @param tree {@link JTree}
     * @param object Object
     *
     * @return Object
     */
    public static Object getParentFor(final JTree tree, final Object object)
    {
        return getParentFor(tree.getModel(), tree.getModel().getRoot(), object);
    }

    /**
     * Wird von getParentFor(Object child) mit Root als Parent aufgerufen und durchsucht den Baum rekursiv durch die Pfade.
     *
     * @param tree {@link JTree}
     * @param parent Object
     * @param object Object
     *
     * @return Object
     */
    public static Object getParentFor(final JTree tree, final Object parent, final Object object)
    {
        return getParentFor(tree.getModel(), parent, object);
    }

    /**
     * Liefert den Parent fuer das Child Objekt. Durchlaeuft dabei den Baum rekursiv durch die Pfade.
     *
     * @param treeModel {@link TreeModel}
     * @param object Object
     *
     * @return Object
     */
    public static Object getParentFor(final TreeModel treeModel, final Object object)
    {
        return getParentFor(treeModel, treeModel.getRoot(), object);
    }

    /**
     * Wird von getParentFor(Object child) mit Root als Parent aufgerufen und durchsucht den Baum rekursiv durch die Pfade.
     *
     * @param treeModel {@link TreeModel}
     * @param parent Object
     * @param object Object
     *
     * @return Object
     */
    public static Object getParentFor(final TreeModel treeModel, final Object parent, final Object object)
    {
        Object found = null;
        int childCount = treeModel.getChildCount(parent);

        try
        {
            for (int index = 0; index < childCount; index++)
            {
                Object child = treeModel.getChild(parent, index);

                if (child == object)
                {
                    found = parent;

                    break;
                }

                found = getParentFor(treeModel, child, object);

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
     * Liefert den TreePath des Objektes.
     *
     * @param tree {@link JTree}
     * @param object Object
     *
     * @return {@link TreePath}
     */
    public static TreePath getPathToRoot(final JTree tree, final Object object)
    {
        return getPathToRoot(tree.getModel(), object);
    }

    /**
     * Liefert den TreePath des Objektes.
     *
     * @param treeModel {@link TreeModel}
     * @param object Object
     *
     * @return {@link TreePath}
     */
    public static TreePath getPathToRoot(final TreeModel treeModel, final Object object)
    {
        List<Object> list = new ArrayList<>();

        list.add(object);

        Object parent = getParentFor(treeModel, object);

        while (parent != null)
        {
            list.add(0, parent);
            parent = getParentFor(treeModel, parent);
        }

        return new TreePath(list.toArray());
    }

    /**
     * Liefert alle selektierten Objekte.
     *
     * @param tree {@link JTree}
     *
     * @return Object[]
     */
    public static Object[] getSelectedObjects(final JTree tree)
    {
        TreePath[] paths = tree.getSelectionPaths();

        if (paths == null)
        {
            return new Object[0];
        }

        Object[] nodes = new Object[paths.length];

        for (int i = 0; i < paths.length; i++)
        {
            nodes[i] = paths[i].getLastPathComponent();
        }

        return nodes;
    }

    /**
     * Liefert den ersten Knoten, der ein UserObject vom gewuenschten Typ enthaelt.
     *
     * @param <K> extends {@link DefaultMutableTreeNode}
     * @param tree {@link JTree}
     * @param userObjectType {@link Class}[]
     *
     * @return {@link DefaultMutableTreeNode}
     */
    @SuppressWarnings("unchecked")
    public static final <K extends DefaultMutableTreeNode> K getSelectedTreeNode(final JTree tree, final Class<?>...userObjectType)
    {
        // Ueberpruefen, ob selektierte Elemente vorhanden sind.
        Object[] selectedObjects = getSelectedObjects(tree);

        if (selectedObjects == null)
        {
            return null;
        }

        for (Object selected : selectedObjects)
        {
            DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode) selected;

            for (Class<?> element : userObjectType)
            {
                if (element.isInstance(selectedTreeNode.getUserObject()))
                {
                    return (K) selectedTreeNode;
                }
            }
        }

        return null;
    }

    /**
     * Liefert das UserObject des ersten Knoten, der das UserObject vom gewuenschten Typ enthaelt.
     *
     * @param <T> Konkretes Objekt
     * @param tree {@link JTree}
     * @param userObjectType {@link Class}
     *
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSelectedUserObject(final JTree tree, final Class<?> userObjectType)
    {
        DefaultMutableTreeNode node = getSelectedTreeNode(tree, userObjectType);

        return (T) (node == null ? null : node.getUserObject());
    }

    /**
     * Liefert das Object des Paths zurueck, wenn der Mauszeiger genau darueber steht und selektiert es bei Bedarf.
     *
     * @param me {@link MouseEvent}
     *
     * @return Object
     */
    public static Object getTreeObjectForEvent(final MouseEvent me)
    {
        Object obj = null;

        if ((me != null) && (me.getSource()instanceof JTree tree))
        {
            if (tree.getRowForLocation(me.getX(), me.getY()) == -1)
            {
                // NOOP
            }
            else
            {
                TreePath path = tree.getClosestPathForLocation(me.getX(), me.getY());

                if (path != null)
                {
                    obj = path.getLastPathComponent();

                    if (!path.equals(tree.getSelectionPath()))
                    {
                        tree.setSelectionPath(path);
                    }
                }
            }
        }

        return obj;
    }

    /**
     * Selektiert den Uebergebenen Node.
     *
     * @param tree {@link JTree}
     * @param node {@link DefaultMutableTreeNode}
     */
    public static void selectNode(final JTree tree, final DefaultMutableTreeNode node)
    {
        if ((tree == null) || (node == null))
        {
            return;
        }

        TreePath path = new TreePath(node.getPath());

        // Knoten NICHT aufklappen
        // tree.expandPath(path);

        tree.scrollPathToVisible(path);
        tree.getSelectionModel().setSelectionPath(path);
    }

    /**
     * Selektiert den Node des betreffenden Events.
     *
     * @param tree {@link JTree}
     * @param me {@link MouseEvent}
     */
    public static void selectNode(final JTree tree, final MouseEvent me)
    {
        if ((tree == null) || (me == null))
        {
            return;
        }

        TreePath closestPath = tree.getClosestPathForLocation(me.getX(), me.getY());

        // Knoten selektieren
        tree.setSelectionPath(closestPath);
    }

    /**
     * Erstellt ein neues {@link TreeUtils} Object.
     */
    private TreeUtils()
    {
        super();
    }
}
