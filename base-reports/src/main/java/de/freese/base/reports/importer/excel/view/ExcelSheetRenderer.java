package de.freese.base.reports.importer.excel.view;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer für die Tabelle der ExcelSheets.
 *
 * @author Thomas Freese
 */
public class ExcelSheetRenderer extends DefaultTableCellRenderer
{
    /**
     * 
     */
    private static final long serialVersionUID = 2719021291033059644L;

    /**
     * Creates a new {@link ExcelSheetRenderer} object.
     */
    public ExcelSheetRenderer()
    {
        super();
    }

    /**
     * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
                                                   final int column)
    {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Number)
        {
            setHorizontalAlignment(RIGHT);
        }
        else
        {
            setHorizontalAlignment(LEFT);
        }

        if (column == 0)
        {
            setHorizontalAlignment(CENTER);
        }

        Double num = null;

        // Wenn mögl. Ganzzahlen anzeigen
        try
        {
            num = value != null ? Double.valueOf(value.toString()) : null;
        }
        catch (Throwable th)
        {
            // th.printStackTrace();
        }

        if ((num != null) && (((num.intValue()) - num.doubleValue()) == 0.0D))
        {
            setText("" + num.intValue());
        }
        else
        {
            setText((value == null) ? "" : value.toString());
        }

        return this;
    }
}
