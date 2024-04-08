package de.freese.base.swing.components.treetable;

import java.io.Serial;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import de.freese.base.swing.components.table.AbstractListTableModel;

/**
 * @author Thomas Freese
 */
public abstract class AbstractTreeListenerTableModel extends AbstractListTableModel<Object> implements TreeExpansionListener, TreeModelListener {
    @Serial
    private static final long serialVersionUID = -3225572645684058554L;

    private final JTree tree;

    /**
     * @param tree {@link JTree}, Wird benötigt, um die Liste mit Objekten zu füllen, NICHT um die Listener zu registrieren !<br>
     * Setzen der Listener:<br>
     * JTree.addTreeExpansionListener(this)<br>
     * JTree.getModel().addTreeModelListener(this)
     */
    protected AbstractTreeListenerTableModel(final int columnCount, final JTree tree) {
        super(columnCount);

        this.tree = tree;
    }

    @Override
    public void treeCollapsed(final TreeExpansionEvent event) {
        updateFromTree();
    }

    @Override
    public void treeExpanded(final TreeExpansionEvent event) {
        updateFromTree();
    }

    @Override
    public void treeNodesChanged(final TreeModelEvent event) {
        if (!(event.getSource() instanceof JTree)) {
            return;
        }

        final Object object = event.getTreePath().getLastPathComponent();

        if (object == null) {
            return;
        }

        final int row = getRowOf(object);

        if (row < 0) {
            return;
        }

        fireTableRowsUpdated(row, row);
    }

    @Override
    public void treeNodesInserted(final TreeModelEvent event) {
        updateFromTree();
    }

    @Override
    public void treeNodesRemoved(final TreeModelEvent event) {
        updateFromTree();
    }

    @Override
    public void treeStructureChanged(final TreeModelEvent event) {
        updateFromTree();
    }

    public void updateFromTree() {
        final Runnable runnable = () -> {
            getList().clear();

            for (int row = 0; row < getTree().getRowCount(); row++) {
                final TreePath treePath = getTree().getPathForRow(row);
                getList().add(treePath.getLastPathComponent());
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    protected JTree getTree() {
        return this.tree;
    }
}
