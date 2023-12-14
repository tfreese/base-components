package de.freese.base.swing.components.treetable;

import java.io.Serial;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Thomas Freese
 */
public class CommonTreeAndTableSelectionModel extends DefaultTreeSelectionModel {
    @Serial
    private static final long serialVersionUID = -7214861962851009038L;

    /**
     * @author Thomas Freese
     */
    private final class ListSelectionHandler implements ListSelectionListener {
        @Override
        public void valueChanged(final ListSelectionEvent e) {
            updateSelectedPathsFromSelectedRows();
        }
    }

    /**
     * @author Thomas Freese
     */
    private final class ThisListSelectionModel extends DefaultListSelectionModel {
        @Serial
        private static final long serialVersionUID = 1564334781623965506L;

        @Override
        public void setSelectionMode(final int selectionMode) {
            super.setSelectionMode(selectionMode);

            if (!CommonTreeAndTableSelectionModel.this.updateTreeSelectionMode) {
                return;
            }

            switch (selectionMode) {
                case ListSelectionModel.SINGLE_SELECTION -> updateTreeSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                case ListSelectionModel.SINGLE_INTERVAL_SELECTION -> updateTreeSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
                case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION -> updateTreeSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                default -> {
                    // Empty
                }
            }
        }
    }

    private final JTree tree;
    private boolean updateTreeSelectionMode = true;
    /**
     * Set to true when we are updating the ListSelectionModel.
     */
    private boolean updatingListSelectionModel;

    public CommonTreeAndTableSelectionModel(final JTree tree) {
        super();

        this.listSelectionModel = new ThisListSelectionModel();
        this.tree = tree;
        getListSelectionModel().addListSelectionListener(new ListSelectionHandler());
    }

    public ListSelectionModel getListSelectionModel() {
        return this.listSelectionModel;
    }

    /**
     * This is overridden to set <code>updatingListSelectionModel</code> and message super.<br/>
     * This is the only place DefaultTreeSelectionModel alters the ListSelectionModel.
     */
    @Override
    public void resetRowSelection() {
        if (!this.updatingListSelectionModel) {
            this.updatingListSelectionModel = true;

            try {
                super.resetRowSelection();
            }
            finally {
                this.updatingListSelectionModel = false;
            }
        }
        // Notice how we don't message super if
        // updatingListSelectionModel is true. If
        // updatingListSelectionModel is true, it implies the
        // ListSelectionModel has already been updated and the
        // paths are the only thing that needs to be updated.
    }

    @Override
    public void setSelectionMode(final int mode) {
        updateTreeSelectionMode(mode);

        this.updateTreeSelectionMode = false;

        // TableSelectionModel anpassen
        switch (mode) {
            case TreeSelectionModel.SINGLE_TREE_SELECTION -> getListSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            case TreeSelectionModel.CONTIGUOUS_TREE_SELECTION -> getListSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            case TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION -> getListSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            default -> {
                // Empty
            }
        }

        this.updateTreeSelectionMode = true;
    }

    /**
     * If <code>updatingListSelectionModel</code> is false, this will reset the selected paths from the selected rows in the list selection model.
     */
    private void updateSelectedPathsFromSelectedRows() {
        if (!this.updatingListSelectionModel) {
            this.updatingListSelectionModel = true;

            try {
                // This is way expensive, ListSelectionModel needs an
                // enumerator for iterating.
                final int min = this.listSelectionModel.getMinSelectionIndex();
                final int max = this.listSelectionModel.getMaxSelectionIndex();

                clearSelection();

                if ((min != -1) && (max != -1)) {
                    for (int counter = min; counter <= max; counter++) {
                        if (this.listSelectionModel.isSelectedIndex(counter)) {
                            final TreePath selPath = this.tree.getPathForRow(counter);

                            if (selPath != null) {
                                addSelectionPath(selPath);
                            }
                        }
                    }
                }
            }
            finally {
                this.updatingListSelectionModel = false;
            }
        }
    }

    private void updateTreeSelectionMode(final int mode) {
        super.setSelectionMode(mode);
    }
}
