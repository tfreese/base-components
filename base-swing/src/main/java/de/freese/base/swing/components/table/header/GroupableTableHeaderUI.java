package de.freese.base.swing.components.table.header;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * @author Nobuo Tamemasa
 * @version 1.0 pre 2004
 */
public class GroupableTableHeaderUI extends BasicTableHeaderUI {
    @Override
    public Dimension getPreferredSize(final JComponent c) {
        long width = 0;
        final Enumeration<TableColumn> enumeration = this.header.getColumnModel().getColumns();

        while (enumeration.hasMoreElements()) {
            final TableColumn aColumn = enumeration.nextElement();
            width += aColumn.getPreferredWidth();
        }

        return createHeaderSize(width);
    }

    @Override
    public void paint(final Graphics g, final JComponent c) {
        final Rectangle clipBounds = g.getClipBounds();

        if (this.header.getColumnModel() == null) {
            return;
        }

        // Scheint 1. unnötig und behebt ausserdem 2. das Problem der nicht bündigen ColumnHeader,
        // die von Fall zu Fall durch das Setzen bzw. NichtSetzten der Margin auf 0 (s.u.)
        // verursacht wurde.
        // ((GroupableTableHeader) this.header).setColumnMargin();

        int column = 0;
        final Dimension size = this.header.getSize();
        final Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
        final Map<GroupableColumn, Rectangle> h = new HashMap<>();
        int columnMargin = this.header.getColumnModel().getColumnMargin();

        // Fehler bei columnMargin>0 werden die Header nicht mehr bündig zu den
        // Columns gemalt !!!
        columnMargin = 0;

        final Enumeration<TableColumn> enumeration = this.header.getColumnModel().getColumns();

        while (enumeration.hasMoreElements()) {
            cellRect.height = size.height;
            cellRect.y = 0;

            final TableColumn aColumn = enumeration.nextElement();
            final List<Object> columnGroups = ((GroupableTableHeader) this.header).getColumnGroups(aColumn);

            if (columnGroups != null) {
                int groupHeight = 0;

                for (Object col : columnGroups) {
                    final GroupableColumn cGroup = (GroupableColumn) col;
                    Rectangle groupRect = h.get(cGroup);

                    if (groupRect == null) {
                        groupRect = new Rectangle(cellRect);

                        final Dimension d = cGroup.getSize(this.header.getTable());
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

            if (cellRect.intersects(clipBounds)) {
                paintCell(g, cellRect, column);
            }

            cellRect.x += cellRect.width;
            column++;
        }

        h.clear();
    }

    private Dimension createHeaderSize(final long width) {
        long w = width;

        final TableColumnModel columnModel = this.header.getColumnModel();
        w += (long) columnModel.getColumnMargin() * columnModel.getColumnCount();

        if (w > Integer.MAX_VALUE) {
            w = Integer.MAX_VALUE;
        }

        return new Dimension((int) w, getHeaderHeight());
    }

    private int getHeaderHeight() {
        // if (true)
        // {
        // return 30;
        // }
        int height = 0;
        final TableColumnModel columnModel = this.header.getColumnModel();

        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            final TableColumn aColumn = columnModel.getColumn(column);

            // final  TableCellRenderer renderer = aColumn.getHeaderRenderer();
            // int cHeight = 0;
            // if (renderer != null)
            // {
            // final  Component comp =
            // renderer.getTableCellRendererComponent(
            // header.getTable(), aColumn.getHeaderValue(), false, false, -1,
            // column
            // );
            //
            // cHeight += comp.getPreferredSize().height;
            // }
            int cHeight = getHeaderRendererComponent(column).getPreferredSize().height;

            final List<Object> columnGroups = ((GroupableTableHeader) this.header).getColumnGroups(aColumn);

            if (columnGroups != null) {
                for (Object col : columnGroups) {
                    final GroupableColumn cGroup = (GroupableColumn) col;
                    cHeight += cGroup.getSize(this.header.getTable()).height;
                }
            }

            height = Math.max(height, cHeight);
        }

        return height;
    }

    private Component getHeaderRendererComponent(final int columnIndex) {
        final TableColumn aColumn = this.header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();

        if (renderer == null) {
            renderer = this.header.getDefaultRenderer();
        }

        return renderer.getTableCellRendererComponent(this.header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);
    }

    private void paintCell(final Graphics g, final Rectangle cellRect, final GroupableColumn cGroup) {
        final TableCellRenderer renderer = cGroup.getHeaderRenderer();
        final Component component = renderer.getTableCellRendererComponent(this.header.getTable(), cGroup.getHeaderValue(), false, false, -1, -1);
        this.rendererPane.add(component);
        this.rendererPane.paintComponent(g, component, this.header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private void paintCell(final Graphics g, final Rectangle cellRect, final int columnIndex) {
        // final TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        // final TableCellRenderer renderer = aColumn.getHeaderRenderer();
        //
        // final Component component =
        // renderer.getTableCellRendererComponent(
        // header.getTable(), aColumn.getHeaderValue(), false, false, -1,
        // columnIndex
        // );
        final Component component = getHeaderRendererComponent(columnIndex);
        this.rendererPane.add(component);
        this.rendererPane.paintComponent(g, component, this.header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }
}
