package de.freese.base.swing.components.treetable;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Gemeinsames SelectionModel fuer einen {@link JTree} und eine {@link JTable}.<br>
 * Zum Teil von SwingX geklaut.
 *
 * @author Thomas Freese
 */
public class CommonTreeAndTableSelectionModel extends DefaultTreeSelectionModel
{
    /**
     * SelectionListener der {@link JTable} um die Selection auf den {@link JTree} zu uebertragen.
     *
     * @author Thomas Freese
     */
    private class ListSelectionHandler implements ListSelectionListener
    {
        /**
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        @Override
        public void valueChanged(final ListSelectionEvent e)
        {
            updateSelectedPathsFromSelectedRows();
        }
    }

    /**
     * {@link ListSelectionModel}, welche bei Aenderung des SelectionModes das {@link TreeSelectionModel} aktualisiert.
     *
     * @author Thomas Freese
     */
    private class ThisListSelectionModel extends DefaultListSelectionModel
    {
        /**
         *
         */
        private static final long serialVersionUID = 1564334781623965506L;

        /**
         * @see javax.swing.DefaultListSelectionModel#setSelectionMode(int)
         */
        @Override
        public void setSelectionMode(final int selectionMode)
        {
            super.setSelectionMode(selectionMode);

            if (!CommonTreeAndTableSelectionModel.this.updateTreeSelectionMode)
            {
                return;
            }

            // TreeSelectionModel anpassen
            switch (selectionMode)
            {
                case ListSelectionModel.SINGLE_SELECTION:
                    updateTreeSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                    break;
                case ListSelectionModel.SINGLE_INTERVAL_SELECTION:
                    updateTreeSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
                    break;
                case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION:
                    updateTreeSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -7214861962851009038L;
    /**
     *
     */
    private final JTree tree;
    /**
     *
     */
    private boolean updateTreeSelectionMode = true;
    /**
     * Set to true when we are updating the ListSelectionModel.
     */
    private boolean updatingListSelectionModel;

    /**
     * Erstellt ein neues {@link CommonTreeAndTableSelectionModel} Object.
     *
     * @param tree {@link JTree}
     */
    public CommonTreeAndTableSelectionModel(final JTree tree)
    {
        super();

        this.listSelectionModel = new ThisListSelectionModel();
        this.tree = tree;
        getListSelectionModel().addListSelectionListener(new ListSelectionHandler());
    }

    /**
     * SelectionModel fuer die {@link JTable}.
     *
     * @return {@link ListSelectionModel}
     */
    public ListSelectionModel getListSelectionModel()
    {
        return this.listSelectionModel;
    }

    /**
     * This is overridden to set <code>updatingListSelectionModel</code> and message super. This is the only place DefaultTreeSelectionModel alters the
     * ListSelectionModel.
     */
    @Override
    public void resetRowSelection()
    {
        if (!this.updatingListSelectionModel)
        {
            this.updatingListSelectionModel = true;

            try
            {
                super.resetRowSelection();
            }
            finally
            {
                this.updatingListSelectionModel = false;
            }
        }
        // Notice how we don't message super if
        // updatingListSelectionModel is true. If
        // updatingListSelectionModel is true, it implies the
        // ListSelectionModel has already been updated and the
        // paths are the only thing that needs to be updated.
    }

    /**
     * @see javax.swing.tree.DefaultTreeSelectionModel#setSelectionMode(int)
     */
    @Override
    public void setSelectionMode(final int mode)
    {
        updateTreeSelectionMode(mode);

        this.updateTreeSelectionMode = false;

        // TableSelectionModel anpassen
        switch (mode)
        {
            case TreeSelectionModel.SINGLE_TREE_SELECTION:
                getListSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                break;
            case TreeSelectionModel.CONTIGUOUS_TREE_SELECTION:
                getListSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                break;
            case TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION:
                getListSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                break;
            default:
                break;
        }

        this.updateTreeSelectionMode = true;
    }

    /**
     * If <code>updatingListSelectionModel</code> is false, this will reset the selected paths from the selected rows in the list selection model.
     */
    private void updateSelectedPathsFromSelectedRows()
    {
        if (!this.updatingListSelectionModel)
        {
            this.updatingListSelectionModel = true;

            try
            {
                // This is way expensive, ListSelectionModel needs an
                // enumerator for iterating.
                int min = this.listSelectionModel.getMinSelectionIndex();
                int max = this.listSelectionModel.getMaxSelectionIndex();

                clearSelection();

                if ((min != -1) && (max != -1))
                {
                    for (int counter = min; counter <= max; counter++)
                    {
                        if (this.listSelectionModel.isSelectedIndex(counter))
                        {
                            TreePath selPath = this.tree.getPathForRow(counter);

                            if (selPath != null)
                            {
                                addSelectionPath(selPath);
                            }
                        }
                    }
                }
            }
            finally
            {
                this.updatingListSelectionModel = false;
            }
        }
    }

    /**
     * Aktualisiert den SelectionMode des {@link TreeSelectionModel}s ohne synchronisierung des {@link ListSelectionModel}s.
     *
     * @param mode int
     */
    private void updateTreeSelectionMode(final int mode)
    {
        super.setSelectionMode(mode);
    }
}
