package de.freese.base.swing.components.treetable;

import java.awt.Rectangle;
import java.io.Serial;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;

import de.freese.base.swing.components.table.ExtTable;

/**
 * @author Thomas Freese
 */
public class TreeAndTable {
    private JTable table;
    private JTree tree;

    public TreeAndTable() {
        super();

        final CommonTreeAndTableSelectionModel selectionModel = new CommonTreeAndTableSelectionModel(getTree());
        getTree().setSelectionModel(selectionModel);
        getTable().setSelectionModel(selectionModel.getListSelectionModel());

        getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // or getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JTable getTable() {
        if (table == null) {
            table = new ExtTable();
        }

        return table;
    }

    public JTree getTree() {
        if (tree == null) {
            tree = new JXTree() {
                @Serial
                private static final long serialVersionUID = 1L;

                @Override
                public void setBounds(final int x, final int y, final int width, final int height) {
                    final Rectangle tableBounds = getTable().getBounds();

                    // if (y != tableBounds.y) {
                    // System.out.printf("%d,%d,%d,%d\n", x, y, width, height);
                    // }

                    super.setBounds(x, tableBounds.y, width, tableBounds.height);
                }

                @Override
                public void setRowHeight(final int rowHeight) {
                    if (rowHeight > 0) {
                        super.setRowHeight(rowHeight);

                        if (getTable().getRowHeight() != rowHeight) {
                            getTable().setRowHeight(getRowHeight());
                        }
                    }
                }
            };
        }

        return tree;
    }
}
