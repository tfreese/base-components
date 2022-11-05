package de.freese.base.reports.importer.excel;

import java.io.InputStream;

/**
 * Schnittstelle für ExcelImplementierungen, z.B. POI oder JExcel.
 *
 * @author Thomas Freese
 */
public interface ExcelImport
{
    void closeExcelFile() throws Exception;

    void closeSheet();

    int getNumColumns();

    int getNumRows();

    int getNumberOfSheets();

    String getSheetName();

    String getValueAt(int row, int column) throws ExcelException;

    boolean isSheetReadable();

    void openExcelFile(InputStream inputStream) throws Exception;

    void openExcelFile(String fileName) throws Exception;

    void selectSheet(int sheetIndex) throws Exception;

    void selectSheet(String sheetName) throws Exception;

    /**
     * Steuert das werfen der {@link ExcelException}.<br>
     * true=wirft Exceptions, false= loggt Exceptions.<br>
     * Default: true
     */
    void setThrowExcelException(boolean value);
}
