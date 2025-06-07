package de.freese.base.reports.importer.excel.view;

import java.io.Serial;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

import de.freese.base.reports.importer.excel.ExcelSheet;

/**
 * @author Thomas Freese
 */
public class ExcelSheetTableModel extends AbstractTableModel {
    @Serial
    private static final long serialVersionUID = -9093380478461819827L;

    private final ExcelSheet excelSheet;

    public ExcelSheetTableModel(final ExcelSheet excelSheet) {
        super();

        this.excelSheet = Objects.requireNonNull(excelSheet, "excelSheet required");
    }

    @Override
    public int getColumnCount() {
        return excelSheet.getColumnCount();
    }

    @Override
    public String getColumnName(final int column) {
        // The first Column does not have a name.
        if (column == 0) {
            return " ";
        }

        // -1: Second Column starts with A.
        return super.getColumnName(column - 1);
    }

    @Override
    public int getRowCount() {
        return excelSheet.getRowCount();
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (columnIndex == 0) {
            // return new JLabel(new Integer(row+1).toString());
            return rowIndex + 1;
        }

        if (columnIndex < getColumnCount() && rowIndex < getRowCount()) {
            return excelSheet.getValueAt(rowIndex, columnIndex - 1);
        }

        return "";
    }
}
