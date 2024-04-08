package de.freese.base.swing.fontchange.handler;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author Thomas Freese
 */
public class TableFontChangeHandler extends ComponentFontChangeHandler {
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        final JTable table = (JTable) object;
        final int rowHeightNew = newFont.getSize() + 5;

        // if (table.getRowHeight() < rowHeightNew) {
        table.setRowHeight(rowHeightNew);

        if (table.getTableHeader() != null) {
            super.fontChanged(newFont, table.getTableHeader());
        }

        // if (table.getDefaultRenderer(Object.class) instanceof Component)
        // {
        // super.fontChanged(newFont, table.getDefaultRenderer(Object.class));
        // }

        // CellRenderer
        for (int c = 0; c < table.getColumnCount(); c++) {
            final Class<?> columnClass = table.getColumnClass(c);

            final TableCellRenderer cellRenderer = table.getDefaultRenderer(columnClass);

            if (cellRenderer instanceof Component) {
                super.fontChanged(newFont, cellRenderer);
            }
        }
    }
}
