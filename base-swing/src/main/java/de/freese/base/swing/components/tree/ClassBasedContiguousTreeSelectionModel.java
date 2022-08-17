package de.freese.base.swing.components.tree;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

/**
 * Lässt nur für die übergebenen Klassen die mehrfache Selektion zu.<br>
 * Als Vergleichswert wird die LastPathComponent des {@link TreePath}s verwendet.<br>
 * Als Default wird intern DISCONTIGUOUS_TREE_SELECTION als SelectionMode verwendet.<br>
 *
 * <pre>
 * tree.setSelectionModel(new ClassBasedContiguousTreeSelectionModel(Class,Class...));
 *
 * Optionale Angabe:
 * tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
 * ODER
 * tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
 * </pre>
 *
 * @author Thomas Freese
 */
public class ClassBasedContiguousTreeSelectionModel extends DefaultTreeSelectionModel
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3330350835354351508L;
    /**
     *
     */
    private final Class<?>[] clazzes;

    /**
     * Erstellt ein neues {@link ClassBasedContiguousTreeSelectionModel} Object.
     *
     * @param clazz Class
     * @param clazzes Class[]; Optionale Klassen
     */
    public ClassBasedContiguousTreeSelectionModel(final Class<?> clazz, final Class<?>...clazzes)
    {
        super();

        if (clazz == null)
        {
            throw new NullPointerException("clazz");
        }

        this.clazzes = new Class<?>[1 + clazzes.length];
        this.clazzes[0] = clazz;

        for (int i = 0; i < clazzes.length; i++)
        {
            if (clazzes[i] == null)
            {
                throw new NullPointerException("clazzes[" + i + "]");
            }

            this.clazzes[i + 1] = clazzes[i];
        }

        setSelectionMode(DISCONTIGUOUS_TREE_SELECTION);
    }

    /**
     * @see javax.swing.tree.DefaultTreeSelectionModel#addSelectionPath(javax.swing.tree.TreePath)
     */
    @Override
    public void addSelectionPath(final TreePath path)
    {
        if ((path == null) || (path.getLastPathComponent() == null))
        {
            super.addSelectionPath(path);

            return;
        }

        TreePath[] selectedPaths = getSelectionPaths();

        if (selectedPaths == null)
        {
            selectedPaths = new TreePath[0];
        }

        if (!containsClazzes(path.getLastPathComponent().getClass()))
        {
            clearSelection();

            super.addSelectionPath(path);
        }
        else
        {
            // Alles andere ausser den definierten Typen raus.
            List<TreePath> paths = new ArrayList<>();

            for (TreePath treePath : selectedPaths)
            {
                if (containsClazzes(treePath.getLastPathComponent().getClass()))
                {
                    paths.add(treePath);
                }
                else
                {
                    removeSelectionPath(treePath);
                }
            }

            paths.add(path);

            super.addSelectionPaths(paths.toArray(new TreePath[0]));
        }
    }

    /**
     * Liefert true, wenn die Klasse in dem Klassen-Array enthalten ist.
     *
     * @param clazz Class
     *
     * @return boolean
     */
    private boolean containsClazzes(final Class<?> clazz)
    {
        if (clazz == null)
        {
            throw new NullPointerException("clazz");
        }

        for (Class<?> class1 : this.clazzes)
        {
            if (class1.equals(clazz))
            {
                return true;
            }
        }

        return false;
    }
}
