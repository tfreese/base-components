package de.freese.base.swing.fontchange.handler;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author Thomas Freese
 */
public class TableFontChangeHandler extends ComponentFontChangeHandler
{
    /**
     * @see de.freese.base.swing.fontchange.handler.ComponentFontChangeHandler#fontChanged(java.awt.Font, java.lang.Object)
     */
    @Override
    public void fontChanged(final Font newFont, final Object object)
    {
        super.fontChanged(newFont, object);

        JTable table = (JTable) object;
        int rowHeightNew = newFont.getSize() + 5;

        // if (table.getRowHeight() < rowHeightNew) {
        table.setRowHeight(rowHeightNew);

        if (table.getTableHeader() != null)
        {
            super.fontChanged(newFont, table.getTableHeader());
        }

        // if (table.getDefaultRenderer(Object.class) instanceof Component)
        // {
        // super.fontChanged(newFont, table.getDefaultRenderer(Object.class));
        // }

        // CellRenderer
        for (int c = 0; c < table.getColumnCount(); c++)
        {
            Class<?> columnClass = table.getColumnClass(c);

            TableCellRenderer cellRenderer = table.getDefaultRenderer(columnClass);

            if (cellRenderer instanceof Component)
            {
                super.fontChanged(newFont, cellRenderer);
            }
        }
    }
}
