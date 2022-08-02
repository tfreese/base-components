package de.freese.base.swing.components.treetable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import de.freese.base.swing.components.table.AbstractListTableModel;

/**
 * Ein {@link AbstractListTableModel}, welches ein {@link TreeModelListener} ist und sich bei Änderungen des Trees automatisch aktualisiert.
 *
 * @author Thomas Freese
 */
public abstract class AbstractTreeListenerTableModel extends AbstractListTableModel<Object> implements TreeExpansionListener, TreeModelListener
{
    /**
     *
     */
    private static final long serialVersionUID = -3225572645684058554L;
    /**
     *
     */
    private final JTree tree;

    /**
     * Erstellt ein neues {@link AbstractTreeListenerTableModel} Object.
     *
     * @param columnCount int
     * @param tree {@link JTree}, Wird benötigt, um die Liste mit Objekten zu füllen, NICHT um die Listener zu registrieren !<br>
     * Setzten der Listener:<br>
     * JTree.addTreeExpansionListener(this)<br>
     * JTree.getModel().addTreeModelListener(this)
     */
    protected AbstractTreeListenerTableModel(final int columnCount, final JTree tree)
    {
        super(columnCount);

        this.tree = tree;
    }

    /**
     * @see javax.swing.event.TreeExpansionListener#treeCollapsed(javax.swing.event.TreeExpansionEvent)
     */
    @Override
    public void treeCollapsed(final TreeExpansionEvent event)
    {
        updateFromTree();
    }

    /**
     * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
     */
    @Override
    public void treeExpanded(final TreeExpansionEvent event)
    {
        updateFromTree();
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeNodesChanged(javax.swing.event.TreeModelEvent)
     */
    @Override
    public void treeNodesChanged(final TreeModelEvent e)
    {
        if (!(e.getSource() instanceof JTree))
        {
            return;
        }

        Object object = e.getTreePath().getLastPathComponent();

        if (object == null)
        {
            return;
        }

        int row = getRowOf(object);

        if (row < 0)
        {
            return;
        }

        fireTableRowsUpdated(row, row);
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeNodesInserted(javax.swing.event.TreeModelEvent)
     */
    @Override
    public void treeNodesInserted(final TreeModelEvent e)
    {
        updateFromTree();
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeNodesRemoved(javax.swing.event.TreeModelEvent)
     */
    @Override
    public void treeNodesRemoved(final TreeModelEvent e)
    {
        updateFromTree();
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeStructureChanged(javax.swing.event.TreeModelEvent)
     */
    @Override
    public void treeStructureChanged(final TreeModelEvent e)
    {
        updateFromTree();
    }

    /**
     *
     */
    public void updateFromTree()
    {
        Runnable runnable = () ->
        {
            getList().clear();

            for (int row = 0; row < getTree().getRowCount(); row++)
            {
                TreePath treePath = getTree().getPathForRow(row);
                getList().add(treePath.getLastPathComponent());
            }
        };

        if (SwingUtilities.isEventDispatchThread())
        {
            runnable.run();
        }
        else
        {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * @return {@link JTree}
     */
    protected JTree getTree()
    {
        return this.tree;
    }
}
