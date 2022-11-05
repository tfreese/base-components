package de.freese.base.utils;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Thomas Freese
 */
public final class TreeUtils
{
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

    public static void expand(final JTree tree)
    {
        Object root = tree.getModel().getRoot();

        if (root == null)
        {
            return;
        }

        expand(tree, new TreePath(root));
    }

    public static void expand(final JTree tree, final int level)
    {
        Object root = tree.getModel().getRoot();

        if (root == null)
        {
            return;
        }

        expand(tree, new TreePath(root), level);
    }

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

    public static Object getFirstSelectedObject(final JTree tree)
    {
        Object[] obj = getSelectedObjects(tree);

        if ((obj != null) && (obj.length > 0))
        {
            return obj[0];
        }

        return null;
    }

    public static Object getParentFor(final JTree tree, final Object object)
    {
        return getParentFor(tree.getModel(), tree.getModel().getRoot(), object);
    }

    public static Object getParentFor(final JTree tree, final Object parent, final Object object)
    {
        return getParentFor(tree.getModel(), parent, object);
    }

    public static Object getParentFor(final TreeModel treeModel, final Object object)
    {
        return getParentFor(treeModel, treeModel.getRoot(), object);
    }

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
            // Empty
        }

        return found;
    }

    public static TreePath getPathToRoot(final JTree tree, final Object object)
    {
        return getPathToRoot(tree.getModel(), object);
    }

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

    public static <K extends DefaultMutableTreeNode> K getSelectedTreeNode(final JTree tree, final Class<?>... userObjectType)
    {
        // Überprüfen, ob selektierte Elemente vorhanden sind.
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

    public static <T> T getSelectedUserObject(final JTree tree, final Class<?> userObjectType)
    {
        DefaultMutableTreeNode node = getSelectedTreeNode(tree, userObjectType);

        return (T) (node == null ? null : node.getUserObject());
    }

    public static Object getTreeObjectForEvent(final MouseEvent me)
    {
        Object obj = null;

        if ((me != null) && (me.getSource() instanceof JTree tree))
        {
            if (tree.getRowForLocation(me.getX(), me.getY()) == -1)
            {
                // Empty
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

    private TreeUtils()
    {
        super();
    }
}
