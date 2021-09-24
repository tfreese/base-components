// Created: 27.07.2006
package de.freese.base.reports.importer.excel;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import de.freese.base.core.exception.StackTraceLimiter;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Konkrete Implementierung (JExcel) des Excelinterfaces.
 *
 * @author Thomas Freese
 */
public class JExcelImpl extends AbstractExcelImport
{
    /**
     *
     */
    private DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    /**
     *
     */
    private Sheet sheet;
    /**
     *
     */
    private Workbook workbook;

    /**
     * @see de.freese.base.reports.importer.excel.AbstractExcelImport#closeExcelFile()
     */
    @Override
    public void closeExcelFile() throws Exception
    {
        this.workbook.close();
        this.workbook = null;

        super.closeExcelFile();
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#closeSheet()
     */
    @Override
    public void closeSheet()
    {
        this.sheet = null;
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getNumberOfSheets()
     */
    @Override
    public int getNumberOfSheets()
    {
        return this.workbook.getNumberOfSheets();
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getNumColumns()
     */
    @Override
    public int getNumColumns()
    {
        return this.sheet.getColumns();
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getNumRows()
     */
    @Override
    public int getNumRows()
    {
        return this.sheet.getRows();
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getSheetName()
     */
    @Override
    public String getSheetName()
    {
        return this.sheet.getName();
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getValueAt(int, int)
     */
    @Override
    public String getValueAt(final int row, final int column) throws ExcelException
    {
        String value = null;

        try
        {
            Cell cell = this.sheet.getCell(column, row);

            if (cell.getType() == CellType.DATE)
            {
                value = this.dateFormatter.format(((DateCell) cell).getDate());
            }
            else
            {
                value = cell.getContents();
            }
        }
        catch (Exception ex)
        {
            ExcelException eex = new ExcelException(getSheetName(), row, column, ex);

            if (isThrowExcelException())
            {
                throw eex;
            }

            StringBuilder sb = new StringBuilder();
            StackTraceLimiter.printStackTrace(eex, 4, sb);
            getLogger().warn(sb.toString());
        }

        if (value == null)
        {
            value = "";
        }

        return value;
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#isSheetReadable()
     */
    @Override
    public boolean isSheetReadable()
    {
        return true;
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#openExcelFile(java.io.InputStream)
     */
    @Override
    public void openExcelFile(final InputStream inputStream) throws Exception
    {
        this.workbook = Workbook.getWorkbook(inputStream);
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#selectSheet(int)
     */
    @Override
    public void selectSheet(final int sheetIndex) throws Exception
    {
        this.sheet = this.workbook.getSheet(sheetIndex);

        // Sheet vorhanden ?
        if (this.sheet == null)
        {
            throw new Exception("Excel Sheet not found at index: " + sheetIndex);
        }
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#selectSheet(java.lang.String)
     */
    @Override
    public void selectSheet(final String sheetName) throws Exception
    {
        this.sheet = this.workbook.getSheet(sheetName);

        // Sheet vorhanden ?
        if (this.sheet == null)
        {
            throw new Exception("Excel Sheet not found: " + sheetName);
        }
    }
}
