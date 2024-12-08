package de.freese.base.utils;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author Thomas Freese
 */
public final class TreeUtils {
    public static void collapse(final JTree tree) {
        final Object root = tree.getModel().getRoot();

        if (root == null) {
            return;
        }

        // Traverse tree from root
        collapse(tree, new TreePath(root));
    }

    public static void collapse(final JTree tree, final TreePath parent) {
        if (parent == null) {
            collapse(tree);

            return;
        }

        final Object node = parent.getLastPathComponent();

        for (int i = 0; i < tree.getModel().getChildCount(node); i++) {
            final Object n = tree.getModel().getChild(node, i);

            final TreePath path = parent.pathByAddingChild(n);

            collapse(tree, path);
        }

        tree.collapsePath(parent);
    }

    public static void expand(final JTree tree) {
        final Object root = tree.getModel().getRoot();

        if (root == null) {
            return;
        }

        expand(tree, new TreePath(root));
    }

    public static void expand(final JTree tree, final int level) {
        final Object root = tree.getModel().getRoot();

        if (root == null) {
            return;
        }

        expand(tree, new TreePath(root), level);
    }

    public static void expand(final JTree tree, final TreePath parent) {
        // Traverse children
        final Object node = parent.getLastPathComponent();

        for (int i = 0; i < tree.getModel().getChildCount(node); i++) {
            final Object n = tree.getModel().getChild(node, i);

            final TreePath path = parent.pathByAddingChild(n);

            expand(tree, path);
        }

        tree.expandPath(parent);
    }

    public static void expand(final JTree tree, final TreePath path, final int level) {
        // tree.expandPath(path);
        // final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        // for (int i = 0; i < node.getChildCount(); i++) {
        // final DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
        // if (child.getLevel() < level) {
        // final TreePath childPath = new TreePath(child.getPath());
        // expandTreeToLevel(tree, childPath, level);
        // }
        // }
        tree.expandPath(path);

        final Object node = path.getLastPathComponent();
        final int count = tree.getModel().getChildCount(node);
        final int currLevel = path.getPathCount();

        for (int i = 0; i < count; i++) {
            final Object child = tree.getModel().getChild(node, i);

            if (currLevel <= level) {
                // Copy the old path.
                final Object[] copy = Arrays.copyOf(path.getPath(), path.getPath().length + 1);

                // Add the latest path element.
                copy[copy.length - 1] = child;

                // Add the latest path element.

                expand(tree, new TreePath(copy), level);
            }
        }
    }

    public static List<TreePath> getExpandedTreePaths(final JTree tree) {
        final List<TreePath> expanded = new ArrayList<>();

        // final TreeModel treeModel = tree.getModel();
        // final TreePath rootPath = new TreePath(treeModel.getRoot());
        // final Enumeration<TreePath> expandedEnum = tree.getExpandedDescendants(rootPath);
        //
        // if (expandedEnum != null) {
        // expanded = Collections.list(expandedEnum);
        // }

        for (int row = 0; row < tree.getRowCount(); row++) {
            if (tree.isExpanded(row)) {
                expanded.add(tree.getPathForRow(row));
            }
        }

        return expanded;
    }

    public static Object getFirstSelectedObject(final JTree tree) {
        final Object[] obj = getSelectedObjects(tree);

        if (obj.length > 0) {
            return obj[0];
        }

        return null;
    }

    public static Object getParentFor(final JTree tree, final Object object) {
        return getParentFor(tree.getModel(), tree.getModel().getRoot(), object);
    }

    public static Object getParentFor(final JTree tree, final Object parent, final Object object) {
        return getParentFor(tree.getModel(), parent, object);
    }

    public static Object getParentFor(final TreeModel treeModel, final Object object) {
        return getParentFor(treeModel, treeModel.getRoot(), object);
    }

    public static Object getParentFor(final TreeModel treeModel, final Object parent, final Object object) {
        Object found = null;
        final int childCount = treeModel.getChildCount(parent);

        try {
            for (int index = 0; index < childCount; index++) {
                final Object child = treeModel.getChild(parent, index);

                if (child == object) {
                    found = parent;

                    break;
                }

                found = getParentFor(treeModel, child, object);

                if (found != null) {
                    break;
                }
            }
        }
        catch (Exception ex) {
            // Empty
        }

        return found;
    }

    public static TreePath getPathToRoot(final JTree tree, final Object object) {
        return getPathToRoot(tree.getModel(), object);
    }

    public static TreePath getPathToRoot(final TreeModel treeModel, final Object object) {
        final List<Object> list = new ArrayList<>();

        list.add(object);

        Object parent = getParentFor(treeModel, object);

        while (parent != null) {
            list.addFirst(parent);
            parent = getParentFor(treeModel, parent);
        }

        return new TreePath(list.toArray());
    }

    public static Object[] getSelectedObjects(final JTree tree) {
        final TreePath[] paths = tree.getSelectionPaths();

        if (paths == null) {
            return new Object[0];
        }

        final Object[] nodes = new Object[paths.length];

        for (int i = 0; i < paths.length; i++) {
            nodes[i] = paths[i].getLastPathComponent();
        }

        return nodes;
    }

    public static <K extends DefaultMutableTreeNode> K getSelectedTreeNode(final JTree tree, final Iterable<Class<?>> userObjectTypes) {
        // Überprüfen, ob selektierte Elemente vorhanden sind.
        final Object[] selectedObjects = getSelectedObjects(tree);

        for (Object selected : selectedObjects) {
            final DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode) selected;

            for (Class<?> element : userObjectTypes) {
                if (element.isInstance(selectedTreeNode.getUserObject())) {
                    return (K) selectedTreeNode;
                }
            }
        }

        return null;
    }

    public static <T> T getSelectedUserObject(final JTree tree, final Class<T> userObjectType) {
        final DefaultMutableTreeNode node = getSelectedTreeNode(tree, Set.of(userObjectType));

        return node == null ? null : userObjectType.cast(node.getUserObject());
    }

    public static Object getTreeObjectForEvent(final MouseEvent event) {
        if (event == null || !(event.getSource() instanceof JTree tree)) {
            return null;
        }

        Object obj = null;

        if (tree.getRowForLocation(event.getX(), event.getY()) > 0) {
            final TreePath path = tree.getClosestPathForLocation(event.getX(), event.getY());

            if (path != null) {
                obj = path.getLastPathComponent();

                if (!path.equals(tree.getSelectionPath())) {
                    tree.setSelectionPath(path);
                }
            }
        }

        return obj;
    }

    public static void selectNode(final JTree tree, final DefaultMutableTreeNode node) {
        if (tree == null || node == null) {
            return;
        }

        final TreePath path = new TreePath(node.getPath());

        // Node NOT expand
        // tree.expandPath(path);

        tree.scrollPathToVisible(path);
        tree.getSelectionModel().setSelectionPath(path);
    }

    public static void selectNode(final JTree tree, final MouseEvent event) {
        if (tree == null || event == null) {
            return;
        }

        final TreePath closestPath = tree.getClosestPathForLocation(event.getX(), event.getY());

        // Knoten selektieren
        tree.setSelectionPath(closestPath);
    }

    private TreeUtils() {
        super();
    }
}
