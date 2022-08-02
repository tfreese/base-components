package de.freese.base.reports.importer.excel;

import java.io.InputStream;

/**
 * Schnittstelle für ExcelImplementierungen, z.B. POI oder JExcel.
 *
 * @author Thomas Freese
 */
public interface ExcelImport
{
    /**
     * Schliesst die Exceldatei.
     *
     * @throws Exception Falls was schiefgeht.
     */
    void closeExcelFile() throws Exception;

    /**
     * Schliesst das ExcelSheet.
     */
    void closeSheet();

    /**
     * Liefert die Anzahl der Spalten.
     *
     * @return int
     */
    int getNumColumns();

    /**
     * Liefert die Anzahl der Zeilen.
     *
     * @return int
     */
    int getNumRows();

    /**
     * Liefert die Anzahl der ExcelSheets.
     *
     * @return int
     */
    int getNumberOfSheets();

    /**
     * Liefert den Namen des ExcelSheets.
     *
     * @return String
     */
    String getSheetName();

    /**
     * Liefert den Wert in betreffender Zelle.
     *
     * @param row int
     * @param column int
     *
     * @return String
     *
     * @throws ExcelException Falls was schiefgeht.
     */
    String getValueAt(int row, int column) throws ExcelException;

    /**
     * Ist das ExcelSheet lesbar ?
     *
     * @return boolean
     */
    boolean isSheetReadable();

    /**
     * Öffnet eine Exceldatei.
     *
     * @param inputStream {@link InputStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    void openExcelFile(InputStream inputStream) throws Exception;

    /**
     * Öffnet eine Exceldatei.
     *
     * @param fileName String
     *
     * @throws Exception Falls was schiefgeht.
     */
    void openExcelFile(String fileName) throws Exception;

    /**
     * Selektiert ein ExcelSheet zum Bearbeiten.
     *
     * @param sheetIndex int
     *
     * @throws Exception Falls was schiefgeht.
     */
    void selectSheet(int sheetIndex) throws Exception;

    /**
     * Selektiert ein ExcelSheet zum Bearbeiten.
     *
     * @param sheetName String
     *
     * @throws Exception Falls was schiefgeht.
     */
    void selectSheet(String sheetName) throws Exception;

    /**
     * Steuert das werfen der {@link ExcelException}.<br>
     * true=wirft Exceptions, false= loggt Exceptions.<br>
     * Default: true
     *
     * @param value boolean
     */
    void setThrowExcelException(boolean value);
}
