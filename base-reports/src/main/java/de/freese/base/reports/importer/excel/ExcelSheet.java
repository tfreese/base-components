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
        if (this.rowValues == null) {
            return 0;
        }

        return this.rowValues.get(0).length;
    }

    public int getRowCount() {
        if (this.rowValues == null) {
            return 0;
        }

        return this.rowValues.size();
    }

    public String getSheetName() {
        return this.sheetName;
    }

    public String getValueAt(final int row, final int column) {
        if (this.rowValues == null) {
            return null;
        }

        return this.rowValues.get(row)[column];
    }
}
