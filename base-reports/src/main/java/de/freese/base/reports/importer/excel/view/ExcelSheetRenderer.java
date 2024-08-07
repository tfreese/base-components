package de.freese.base.reports.importer.excel.view;

import java.awt.Component;
import java.io.Serial;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Thomas Freese
 */
public class ExcelSheetRenderer extends DefaultTableCellRenderer {
    @Serial
    private static final long serialVersionUID = 2719021291033059644L;

    @SuppressWarnings("checkstyle:IllegalCatch")
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Number) {
            setHorizontalAlignment(RIGHT);
        }
        else {
            setHorizontalAlignment(LEFT);
        }

        if (column == 0) {
            setHorizontalAlignment(CENTER);
        }

        Double num = null;

        try {
            num = value != null ? Double.valueOf(value.toString()) : null;
        }
        catch (Throwable th) {
            // Empty;
        }

        if (num != null && Double.compare(num.intValue() - num, 0.0D) == 0) {
            setText("" + num.intValue());
        }
        else {
            setText((value == null) ? "" : value.toString());
        }

        return this;
    }
}
