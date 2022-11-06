package de.freese.base.swing.components.treetable;

import java.awt.Rectangle;
import java.io.Serial;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import de.freese.base.swing.components.table.ExtTable;
import org.jdesktop.swingx.JXTree;

/**
 * @author Thomas Freese
 */
public class TreeUndTable
{
    private JTable table;

    private JTree tree;

    public TreeUndTable()
    {
        super();

        // SelectionModel zuweisen
        CommonTreeAndTableSelectionModel selectionModel = new CommonTreeAndTableSelectionModel(this.tree);
        getTree().setSelectionModel(selectionModel);
        getTable().setSelectionModel(selectionModel.getListSelectionModel());

        getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // oder getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JTable getTable()
    {
        if (this.table == null)
        {
            this.table = new ExtTable();
        }

        return this.table;
    }

    public JTree getTree()
    {
        if (this.tree == null)
        {
            this.tree = new JXTree()
            {
                @Serial
                private static final long serialVersionUID = 1L;

                /**
                 * @see java.awt.Component#setBounds(int, int, int, int)
                 */
                @Override
                public void setBounds(final int x, final int y, final int width, final int height)
                {
                    Rectangle tableBounds = getTable().getBounds();

                    // if (y != tableBounds.y)
                    // {
                    // System.out.printf("%d,%d,%d,%d\n", x, y, width, height);
                    // }

                    super.setBounds(x, tableBounds.y, width, tableBounds.height);
                }

                /**
                 * @see javax.swing.JTree#setRowHeight(int)
                 */
                @Override
                public void setRowHeight(final int rowHeight)
                {
                    if (this.rowHeight > 0)
                    {
                        super.setRowHeight(this.rowHeight);

                        if (getTable().getRowHeight() != this.rowHeight)
                        {
                            getTable().setRowHeight(getRowHeight());
                        }
                    }
                }
            };
        }

        return this.tree;
    }
}
