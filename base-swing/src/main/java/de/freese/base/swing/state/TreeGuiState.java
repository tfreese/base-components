package de.freese.base.swing.state;

import java.awt.Component;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "TreeGuiState")
@XmlAccessorType(XmlAccessType.FIELD)
public class TreeGuiState extends AbstractGuiState
{
    @Serial
    private static final long serialVersionUID = -3862916832341697120L;

    private final transient List<int[]> expansionIndices = new ArrayList<>();

    private int[] selectedRows;

    public TreeGuiState()
    {
        super(JTree.class);
    }

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

        // Restore expanded TreePaths.
        for (int[] indices : this.expansionIndices)
        {
            Object parent = model.getRoot();
            TreePath treePath = new TreePath(parent);

            try
            {
                for (int index : indices)
                {
                    parent = model.getChild(parent, index);

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

        // Save expanded TreePaths.
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
    }
}
