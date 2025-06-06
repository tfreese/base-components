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
public class TreeGuiState extends AbstractGuiState {
    @Serial
    private static final long serialVersionUID = -3862916832341697120L;
    private final transient List<int[]> expansionIndices = new ArrayList<>();

    private int[] selectedRows;

    public TreeGuiState() {
        super(JTree.class);
    }

    public boolean hasSelectedRows() {
        return selectedRows != null && selectedRows.length > 0;
    }

    @Override
    public void restore(final Component component) {
        super.restore(component);

        final JTree tree = (JTree) component;

        final TreeModel model = tree.getModel();

        if (model == null) {
            return;
        }

        // Restore expanded TreePaths.
        for (int[] indices : expansionIndices) {
            Object parent = model.getRoot();
            TreePath treePath = new TreePath(parent);

            try {
                for (int index : indices) {
                    parent = model.getChild(parent, index);

                    if (parent == null) {
                        continue;
                    }

                    treePath = treePath.pathByAddingChild(parent);
                }
            }
            catch (Exception ex) {
                // Ignore
            }

            tree.expandPath(treePath);
        }

        if (hasSelectedRows()) {
            tree.setSelectionRows(selectedRows);
        }
    }

    @Override
    public void store(final Component component) {
        super.store(component);

        final JTree tree = (JTree) component;

        expansionIndices.clear();

        final TreeModel model = tree.getModel();

        if (model == null) {
            return;
        }

        final int rows = tree.getRowCount();

        // Save expanded TreePaths.
        for (int row = 0; row < rows; row++) {
            if (!tree.isExpanded(row)) {
                continue;
            }

            final TreePath treePath = tree.getPathForRow(row);

            if (treePath != null) {
                final int count = treePath.getPathCount();
                final int[] indices = new int[count - 1];
                Object parent = model.getRoot();

                for (int counter = 1; counter < count; counter++) {
                    final Object pathComponent = treePath.getPathComponent(counter);

                    indices[counter - 1] = model.getIndexOfChild(parent, pathComponent);
                    parent = treePath.getPathComponent(counter);

                    if (indices[counter - 1] < 0) {
                        break;
                    }
                }

                if (indices.length > 0) {
                    expansionIndices.add(indices);
                }
            }
        }

        selectedRows = tree.getSelectionRows();
    }
}
