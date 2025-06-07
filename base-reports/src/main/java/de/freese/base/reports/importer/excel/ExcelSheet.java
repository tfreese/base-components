package de.freese.base.reports.importer.excel;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class ExcelSheet implements Serializable {
    @Serial
    private static final long serialVersionUID = 574094444465628429L;

    private final transient List<String[]> rowValues;
    private final String sheetName;

    public ExcelSheet(final String sheetName, final List<String[]> rowValues) {
        super();

        this.sheetName = Objects.requireNonNull(sheetName, "sheetName required");
        this.rowValues = Objects.requireNonNull(rowValues, "rowValues required");
    }

    public int getColumnCount() {
        if (rowValues == null) {
            return 0;
        }

        return rowValues.getFirst().length;
    }

    public int getRowCount() {
        if (rowValues == null) {
            return 0;
        }

        return rowValues.size();
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getValueAt(final int row, final int column) {
        if (rowValues == null) {
            return null;
        }

        return rowValues.get(row)[column];
    }
}
