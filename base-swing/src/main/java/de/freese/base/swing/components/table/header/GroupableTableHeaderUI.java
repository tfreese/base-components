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
        final Enumeration<TableColumn> enumeration = header.getColumnModel().getColumns();

        while (enumeration.hasMoreElements()) {
            final TableColumn aColumn = enumeration.nextElement();
            width += aColumn.getPreferredWidth();
        }

        return createGroupableHeaderSize(width);
    }

    @Override
    public void paint(final Graphics g, final JComponent c) {
        final Rectangle clipBounds = g.getClipBounds();

        if (header.getColumnModel() == null) {
            return;
        }

        // Scheint 1. unnötig und behebt ausserdem 2. das Problem der nicht bündigen ColumnHeader,
        // die von Fall zu Fall durch das Setzen bzw. NichtSetzten der Margin auf 0 (s.u.) verursacht wurde.
        // ((GroupableTableHeader) header).setColumnMargin();

        int column = 0;
        final Dimension size = header.getSize();
        final Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
        final Map<GroupableColumn, Rectangle> map = new HashMap<>();

        // int columnMargin = header.getColumnModel().getColumnMargin();

        // Fehler bei columnMargin > 0 werden die Header nicht mehr bündig zu den Columns gemalt!
        final int columnMargin = 0;

        final Enumeration<TableColumn> enumeration = header.getColumnModel().getColumns();

        while (enumeration.hasMoreElements()) {
            cellRect.height = size.height;
            cellRect.y = 0;

            final TableColumn aColumn = enumeration.nextElement();
            final List<Object> columnGroups = ((GroupableTableHeader) header).getColumnGroups(aColumn);

            int groupHeight = 0;

            for (Object col : columnGroups) {
                final GroupableColumn cGroup = (GroupableColumn) col;

                final Rectangle groupRect = map.computeIfAbsent(cGroup, key -> {
                    final Rectangle rectangle = new Rectangle(cellRect);

                    final Dimension d = key.getSize(header.getTable());
                    rectangle.width = d.width;
                    rectangle.height = d.height;

                    return rectangle;
                });

                paintGroupableCell(g, groupRect, cGroup);
                groupHeight += groupRect.height;
                cellRect.height = size.height - groupHeight;
                cellRect.y = groupHeight;
            }

            cellRect.width = aColumn.getWidth() + columnMargin;

            if (cellRect.intersects(clipBounds)) {
                paintGroupableCell(g, cellRect, column);
            }

            cellRect.x += cellRect.width;
            column++;
        }

        map.clear();
    }

    private Dimension createGroupableHeaderSize(final long width) {
        long w = width;

        final TableColumnModel columnModel = header.getColumnModel();
        w += (long) columnModel.getColumnMargin() * columnModel.getColumnCount();

        if (w > Integer.MAX_VALUE) {
            w = Integer.MAX_VALUE;
        }

        return new Dimension((int) w, getGroupableHeaderHeight());
    }

    private int getGroupableHeaderHeight() {
        // if (true) {
        // return 30;
        // }

        int height = 0;
        final TableColumnModel columnModel = header.getColumnModel();

        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            final TableColumn aColumn = columnModel.getColumn(column);

            // final  TableCellRenderer renderer = aColumn.getHeaderRenderer();
            // int cHeight = 0;
            // if (renderer != null) {
            // final  Component comp =
            // renderer.getTableCellRendererComponent(
            // header.getTable(), aColumn.getHeaderValue(), false, false, -1,
            // column
            // );
            //
            // cHeight += comp.getPreferredSize().height;
            // }
            int cHeight = getHeaderRendererComponent(column).getPreferredSize().height;

            final List<Object> columnGroups = ((GroupableTableHeader) header).getColumnGroups(aColumn);

            for (Object col : columnGroups) {
                final GroupableColumn cGroup = (GroupableColumn) col;
                cHeight += cGroup.getSize(header.getTable()).height;
            }

            height = Math.max(height, cHeight);
        }

        return height;
    }

    private Component getHeaderRendererComponent(final int columnIndex) {
        final TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();

        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }

        return renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);
    }

    private void paintGroupableCell(final Graphics g, final Rectangle cellRect, final GroupableColumn cGroup) {
        final TableCellRenderer renderer = cGroup.getHeaderRenderer();
        final Component component = renderer.getTableCellRendererComponent(header.getTable(), cGroup.getHeaderValue(), false, false, -1, -1);
        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private void paintGroupableCell(final Graphics g, final Rectangle cellRect, final int columnIndex) {
        // final TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        // final TableCellRenderer renderer = aColumn.getHeaderRenderer();
        //
        // final Component component =
        // renderer.getTableCellRendererComponent(
        // header.getTable(), aColumn.getHeaderValue(), false, false, -1,
        // columnIndex
        // );
        final Component component = getHeaderRendererComponent(columnIndex);
        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }
}
