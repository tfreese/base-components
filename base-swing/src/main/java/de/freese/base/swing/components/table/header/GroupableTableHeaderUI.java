package de.freese.base.swing.components.table.header;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * GroupableTableHeaderUI
 * 
 * @author Nobuo Tamemasa
 * @version 1.0 pre 2004
 */
public class GroupableTableHeaderUI extends BasicTableHeaderUI
{
	/**
	 * @param width long
	 * @return {@link Dimension}
	 */
	private Dimension createHeaderSize(final long width)
	{
		long m_width = width;

		TableColumnModel columnModel = this.header.getColumnModel();
		m_width += (columnModel.getColumnMargin() * columnModel.getColumnCount());

		if (m_width > Integer.MAX_VALUE)
		{
			m_width = Integer.MAX_VALUE;
		}

		return new Dimension((int) m_width, getHeaderHeight());
	}

	/**
	 * @return int
	 */
	private int getHeaderHeight()
	{
		// if (true)
		// {
		// return 30;
		// }
		int height = 0;
		TableColumnModel columnModel = this.header.getColumnModel();

		for (int column = 0; column < columnModel.getColumnCount(); column++)
		{
			TableColumn aColumn = columnModel.getColumn(column);

			// TableCellRenderer renderer = aColumn.getHeaderRenderer();
			// int cHeight = 0;
			// if (renderer != null)
			// {
			// Component comp =
			// renderer.getTableCellRendererComponent(
			// header.getTable(), aColumn.getHeaderValue(), false, false, -1,
			// column
			// );
			//
			// cHeight += comp.getPreferredSize().height;
			// }
			int cHeight = getHeaderRendererComponent(column).getPreferredSize().height;

			List<Object> columnGroups =
					((GroupableTableHeader) this.header).getColumnGroups(aColumn);

			if (columnGroups != null)
			{
				for (Object col : columnGroups)
				{
					GroupableColumn cGroup = (GroupableColumn) col;
					cHeight += cGroup.getSize(this.header.getTable()).height;
				}
			}

			height = Math.max(height, cHeight);
		}

		return height;
	}

	/**
	 * @param columnIndex int
	 * @return {@link Component}
	 */
	private Component getHeaderRendererComponent(final int columnIndex)
	{
		TableColumn aColumn = this.header.getColumnModel().getColumn(columnIndex);
		TableCellRenderer renderer = aColumn.getHeaderRenderer();

		if (renderer == null)
		{
			renderer = this.header.getDefaultRenderer();
		}

		return renderer.getTableCellRendererComponent(this.header.getTable(),
				aColumn.getHeaderValue(), false, false, -1, columnIndex);
	}

	/**
	 * @see javax.swing.plaf.basic.BasicTableHeaderUI#getPreferredSize(javax.swing.JComponent)
	 */
	@Override
	public Dimension getPreferredSize(final JComponent c)
	{
		long width = 0;
		Enumeration<TableColumn> enumeration = this.header.getColumnModel().getColumns();

		while (enumeration.hasMoreElements())
		{
			TableColumn aColumn = enumeration.nextElement();
			width += aColumn.getPreferredWidth();
		}

		return createHeaderSize(width);
	}

	/**
	 * @see javax.swing.plaf.basic.BasicTableHeaderUI#paint(java.awt.Graphics,
	 *      javax.swing.JComponent)
	 */
	@Override
	public void paint(final Graphics g, final JComponent c)
	{
		Rectangle clipBounds = g.getClipBounds();

		if (this.header.getColumnModel() == null)
		{
			return;
		}

		// Scheint 1. unnoetig und behebt ausserdem 2. das Problem der nicht buendigen ColumnHeader,
		// die von Fall zu Fall durch das Setzen bzw. NichtSetzten der Margin auf 0 (s.u.)
		// verursacht wurde.
		// ((GroupableTableHeader) this.header).setColumnMargin();

		int column = 0;
		Dimension size = this.header.getSize();
		Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
		Hashtable<GroupableColumn, Rectangle> h = new Hashtable<>();
		int columnMargin = this.header.getColumnModel().getColumnMargin();

		// Fehler bei columnMargin>0 werden die Header nicht mehr buendig zu den
		// Columns gemalt !!!
		columnMargin = 0;

		Enumeration<TableColumn> enumeration = this.header.getColumnModel().getColumns();

		while (enumeration.hasMoreElements())
		{
			cellRect.height = size.height;
			cellRect.y = 0;

			TableColumn aColumn = enumeration.nextElement();
			List<Object> columnGroups =
					((GroupableTableHeader) this.header).getColumnGroups(aColumn);

			if (columnGroups != null)
			{
				int groupHeight = 0;

				for (Object col : columnGroups)
				{
					GroupableColumn cGroup = (GroupableColumn) col;
					Rectangle groupRect = h.get(cGroup);

					if (groupRect == null)
					{
						groupRect = new Rectangle(cellRect);

						Dimension d = cGroup.getSize(this.header.getTable());
						groupRect.width = d.width;
						groupRect.height = d.height;
						h.put(cGroup, groupRect);
					}

					paintCell(g, groupRect, cGroup);
					groupHeight += groupRect.height;
					cellRect.height = size.height - groupHeight;
					cellRect.y = groupHeight;
				}
			}

			cellRect.width = aColumn.getWidth() + columnMargin;

			if (cellRect.intersects(clipBounds))
			{
				paintCell(g, cellRect, column);
			}

			cellRect.x += cellRect.width;
			column++;
		}

		h.clear();
		h = null;
	}

	/**
	 * @param g {@link Graphics}
	 * @param cellRect {@link Rectangle}
	 * @param cGroup {@link GroupableColumn}
	 */
	private void paintCell(final Graphics g, final Rectangle cellRect, final GroupableColumn cGroup)
	{
		TableCellRenderer renderer = cGroup.getHeaderRenderer();
		Component component =
				renderer.getTableCellRendererComponent(this.header.getTable(),
						cGroup.getHeaderValue(), false, false, -1, -1);
		this.rendererPane.add(component);
		this.rendererPane.paintComponent(g, component, this.header, cellRect.x, cellRect.y,
				cellRect.width, cellRect.height, true);
	}

	/**
	 * @param g {@link Graphics}
	 * @param cellRect {@link Rectangle}
	 * @param columnIndex int
	 */
	private void paintCell(final Graphics g, final Rectangle cellRect, final int columnIndex)
	{
		// TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
		// TableCellRenderer renderer = aColumn.getHeaderRenderer();
		//
		// Component component =
		// renderer.getTableCellRendererComponent(
		// header.getTable(), aColumn.getHeaderValue(), false, false, -1,
		// columnIndex
		// );
		Component component = getHeaderRendererComponent(columnIndex);
		this.rendererPane.add(component);
		this.rendererPane.paintComponent(g, component, this.header, cellRect.x, cellRect.y,
				cellRect.width, cellRect.height, true);
	}
}
