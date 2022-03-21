package de.freese.base.swing.state;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Verwaltet den Status eines Trees.
 *
 * @author Thomas Freese
 */
@XmlRootElement(name = "TreeGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeGuiState extends AbstractGuiState
{
    /**
     *
     */
    private static final long serialVersionUID = -3862916832341697120L;
    /**
     *
     */
    private final List<int[]> expansionIndices = new ArrayList<>();
    /**
     * int[]
     */
    private int[] selectedRows;

    /**
     * Creates a new {@link TreeGuiState} object.
     */
    public TreeGuiState()
    {
        super(JTree.class);
    }

    /**
     * Liefert true, wenn selektierte Rows vorhanden sind.
     *
     * @return boolean
     */
    public boolean hasSelectedRows()
    {
        return (this.selectedRows != null) && (this.selectedRows.length > 0);
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#restore(java.awt.Component)
     */
    @Override
    public void restore(final Component component)
    {
        super.restore(component);

        JTree tree = (JTree) component;

        TreeModel model = tree.getModel();

        if (model == null)
        {
            return;
        }

        // Wiederherstellen der aufgeklappten TreePaths.
        for (int[] indices : this.expansionIndices)
        {
            int count = indices.length;
            Object parent = model.getRoot();
            TreePath treePath = new TreePath(parent);

            try
            {
                for (int counter = 0; counter < count; counter++)
                {
                    parent = model.getChild(parent, indices[counter]);

                    if (parent == null)
                    {
                        continue;
                    }

                    treePath = treePath.pathByAddingChild(parent);
                }
            }
            catch (Exception ex)
            {
                // Ignore
            }

            tree.expandPath(treePath);
        }

        if (hasSelectedRows())
        {
            tree.setSelectionRows(this.selectedRows);
        }
    }

    /**
     * @see de.freese.base.swing.state.AbstractGuiState#store(java.awt.Component)
     */
    @Override
    public void store(final Component component)
    {
        super.store(component);

        JTree tree = (JTree) component;

        this.expansionIndices.clear();

        TreeModel model = tree.getModel();

        if (model == null)
        {
            return;
        }

        int rows = tree.getRowCount();

        // Merken der aufgeklappten TreePaths.
        for (int row = 0; row < rows; row++)
        {
            if (!tree.isExpanded(row))
            {
                continue;
            }

            TreePath treePath = tree.getPathForRow(row);

            if (treePath != null)
            {
                int count = treePath.getPathCount();
                int[] indices = new int[count - 1];
                Object parent = model.getRoot();

                for (int counter = 1; counter < count; counter++)
                {
                    Object pathComponent = treePath.getPathComponent(counter);

                    indices[counter - 1] = model.getIndexOfChild(parent, pathComponent);
                    parent = treePath.getPathComponent(counter);

                    if (indices[counter - 1] < 0)
                    {
                        break;
                    }
                }

                if (indices.length > 0)
                {
                    this.expansionIndices.add(indices);
                }
            }
        }

        this.selectedRows = tree.getSelectionRows();

        // for (Iterator iter = expansionIndices.iterator(); iter.hasNext();)
        // {
        // int[] element = (int[]) iter.next();
        //
        // for (int i = 0; i < element.length; i++)
        // {
        // System.out.print(element[i] + "; ");
        // }
        //
        // System.out.println();
        // }
    }
}
