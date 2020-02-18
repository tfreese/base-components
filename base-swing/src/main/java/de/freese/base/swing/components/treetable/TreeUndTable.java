package de.freese.base.swing.components.treetable;

import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;

import de.freese.base.swing.components.table.ExtTable;

/**
 * Enthaelt einen {@link JTree} und eine {@link JTable}, kombiniert zu der TreeTable Ansicht.
 * 
 * @author Thomas Freese
 */
public class TreeUndTable
{
	/**
	 *
	 */
	private JTable table = null;

	/**
	 *
	 */
	private JTree tree = null;

	/**
	 * Erstellt ein neues {@link TreeUndTable} Object.
	 */
	public TreeUndTable()
	{
		super();

		JTable table = getTable();
		JTree tree = getTree();

		// SelectionModel zuweisen
		CommonTreeAndTableSelectionModel selectionModel =
				new CommonTreeAndTableSelectionModel(tree);
		tree.setSelectionModel(selectionModel);
		table.setSelectionModel(selectionModel.getListSelectionModel());

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// oder
		// table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	/**
	 * @return {@link JTable}
	 */
	public JTable getTable()
	{
		if (this.table == null)
		{
			this.table = new ExtTable();
		}

		return this.table;
	}

	/**
	 * @return {@link JTree}
	 */
	public JTree getTree()
	{
		if (this.tree == null)
		{
			this.tree = new JXTree()
			{
				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

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
			};
		}

		return this.tree;
	}
}
