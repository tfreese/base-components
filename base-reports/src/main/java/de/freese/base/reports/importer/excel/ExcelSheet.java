package de.freese.base.reports.importer.excel;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container für die Daten eines ExcelSheets.
 *
 * @author Thomas Freese
 */
public class ExcelSheet implements Serializable {
    @Serial
    private static final long serialVersionUID = 574094444465628429L;
    // /**
    // * Wie viele Zeilen sollen MINDESTENS angezeigt werden ?
    // */
    // private static int MIN_ROWNUM = 10;
    //
    // /**
    // * Wie viele Spalten sollen MINDESTENS angezeigt werden ?
    // */
    // private static int MIN_COLUMNNUM = 10;

    private final String fileName;

    private final String sheetName;

    private transient List<String[]> rowValues;

    public ExcelSheet(final String fileName, final String sheetName) {
        super();

        this.fileName = fileName;
        this.sheetName = sheetName;
    }

    public int getColumnCount() {
        if (this.rowValues == null) {
            return 0;
        }

        return this.rowValues.get(0).length;
    }

    public String getFileName() {
        return this.fileName;
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

    public void readCurrentSheet(final ExcelImport excel) throws Exception {
        int rows = excel.getNumRows();
        int cols = excel.getNumColumns();

        // if (rows < MIN_ROWNUM)
        // {
        // rows = MIN_ROWNUM;
        // }
        //
        // if (cols < MIN_COLUMNNUM)
        // {
        // cols = MIN_COLUMNNUM;
        // }

        this.rowValues = new ArrayList<>(rows);

        for (int row = 0; row < rows; row++) {
            String[] rowValue = new String[cols];

            for (int col = 0; col < cols; col++) {
                String value = excel.getValueAt(row, col);

                rowValue[col] = value;
            }

            this.rowValues.add(rowValue);
        }
    }

    public void removeEmptyRows() {
        if (this.rowValues == null) {
            return;
        }

        for (Iterator<String[]> iterator = this.rowValues.iterator(); iterator.hasNext(); ) {
            String[] rowValue = iterator.next();

            boolean isEmpty = true;

            for (String element : rowValue) {
                if ((element != null) && (element.strip().length() > 0)) {
                    isEmpty = false;
                    break;
                }
            }

            if (isEmpty) {
                iterator.remove();
            }
        }
    }
}
