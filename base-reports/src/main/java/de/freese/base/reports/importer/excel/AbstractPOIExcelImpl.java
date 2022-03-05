package de.freese.base.reports.importer.excel;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import de.freese.base.core.exception.StackTraceLimiter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Basis Implementierung des Excelinterfaces für POI.
 *
 * @author Thomas Freese
 */
public abstract class AbstractPOIExcelImpl extends AbstractExcelImport
{
    /**
     *
     */
    private final Map<Short, Format> cacheFormat = new HashMap<>();
    /**
     *
     */
    private final DataFormatter dataFormatter = new DataFormatter();
    /**
     *
     */
    private FormulaEvaluator formulaEvaluator;
    /**
     *
     */
    private Row row;
    /**
     *
     */
    private Sheet sheet;
    /**
     *
     */
    private Workbook workBook;

    /**
     * @see de.freese.base.reports.importer.excel.AbstractExcelImport#closeExcelFile()
     */
    @Override
    public final void closeExcelFile() throws Exception
    {
        this.cacheFormat.clear();

        if (this.workBook != null)
        {
            this.workBook = null;
        }

        if (this.formulaEvaluator != null)
        {
            this.formulaEvaluator = null;
        }

        super.closeExcelFile();
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#closeSheet()
     */
    @Override
    public final void closeSheet()
    {
        // NOOP
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getNumColumns()
     */
    @Override
    public final int getNumColumns()
    {
        int numRows = getNumRows();
        int maxCol = 0;

        for (int i = 0; i < numRows; i++)
        {
            Row r = this.sheet.getRow(i);

            if ((r != null) && (maxCol < r.getLastCellNum()))
            {
                maxCol = r.getLastCellNum();
            }
        }

        return maxCol + 1;
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getNumRows()
     */
    @Override
    public final int getNumRows()
    {
        int numRows = this.sheet.getLastRowNum();

        // if(numRows==0) { return 1; }

        return numRows + 1;
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getNumberOfSheets()
     */
    @Override
    public final int getNumberOfSheets()
    {
        return this.workBook.getNumberOfSheets();
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getSheetName()
     */
    @Override
    public final String getSheetName()
    {
        return this.sheet.getSheetName();
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#getValueAt(int, int)
     */
    @Override
    public final String getValueAt(final int row, final int column) throws ExcelException
    {
        String value = null;

        try
        {
            selectRow(row);

            if (this.row != null)
            {
                Cell cell = this.row.getCell(column);

                if (cell != null)
                {
                    if (!CellType.FORMULA.equals(cell.getCellType()))
                    {
                        value = this.dataFormatter.formatCellValue(cell);
                    }
                    else
                    {
                        // this.formulaEvaluator.evaluate(cell).getNumberValue();
                        value = this.dataFormatter.formatCellValue(cell, this.formulaEvaluator);
                    }
                    // switch (cell.getCellTypeEnum())
                    // {
                    // case STRING:
                    // value = cell.getRichStringCellValue().getString();
                    //
                    // break;
                    //
                    // case NUMERIC:
                    //
                    // short format = cell.getCellStyle().getDataFormat();
                    //
                    // if (DateUtil.isCellDateFormatted(cell) || (format == 165) || (format == 167) || (format == 168) || (format == 169)
                    // || (format == 170) || (format == 171) || (format == 191) || (format == 201))
                    // {
                    // Format formatter = getDateFormatter(format);
                    // String date = formatter.format(cell.getDateCellValue());
                    //
                    // value = date;
                    // }
                    // else
                    // {
                    // value = Double.toString(cell.getNumericCellValue());
                    // }
                    //
                    // break;
                    //
                    // case FORMULA:
                    //
                    // // Lieferte ArrayIndexOutOfBoundsException
                    // // return cell.getCellFormula();
                    //
                    // try
                    // {
                    // value = Double.toString(cell.getNumericCellValue());
                    // }
                    // catch (IllegalStateException ex)
                    // {
                    // // Nimm einfach den String aus der Zelle...
                    // value = cell.getRichStringCellValue().getString();
                    // }
                    //
                    // if (value.equals("NaN"))
                    // {
                    // value = cell.getRichStringCellValue().getString();
                    // }
                    //
                    // break;
                    //
                    // default:
                    // break;
                    // }
                }
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
    public final boolean isSheetReadable()
    {
        /*
         * if(_sheet!=null && sheet.getProtect()==true) { System.out.println("****\n\n\n Protected\n\n\n **** "); return false; } for(int
         * i=0;i<getNumRows();i++) { if(sheet!=null && sheet.isRowBroken(i)) { System.out.println("****\n\n\n Row is broken\n\n\n **** "); } }
         */
        return true;
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#openExcelFile(java.io.InputStream)
     */
    @Override
    public final void openExcelFile(final InputStream inputStream) throws Exception
    {
        this.workBook = openWorkbook(inputStream);

        // this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        // this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        this.formulaEvaluator = this.workBook.getCreationHelper().createFormulaEvaluator();
        this.workBook.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#selectSheet(int)
     */
    @Override
    public final void selectSheet(final int sheetIndex) throws Exception
    {
        this.sheet = this.workBook.getSheetAt(sheetIndex);

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
    public final void selectSheet(final String sheetName) throws Exception
    {
        this.sheet = this.workBook.getSheet(sheetName);

        // Sheet vorhanden ?
        if (this.sheet == null)
        {
            int sheetCount = this.workBook.getNumberOfSheets();

            // Durch alle Sheets gehen und Namen trimmen
            for (int i = 0; i < sheetCount; i++)
            {
                String sName = this.workBook.getSheetName(i).trim();

                if (sName.equals(sheetName.trim()))
                {
                    this.sheet = this.workBook.getSheetAt(i);

                    break;
                }
            }
        }

        // Sheet vorhanden ?
        if (this.sheet == null)
        {
            throw new Exception("Excel Sheet not found: " + sheetName);
        }
    }

    /**
     * Liefert einen {@link DateFormat}ter für das Excelformat.
     *
     * @param format short
     *
     * @return {@link Format}
     */
    protected Format getDateFormatter(final short format)
    {
        Format formatter = this.cacheFormat.get(format);

        if (formatter == null)
        {
            formatter = new SimpleDateFormat(getDateFormatByExcelIndex(format));
            this.cacheFormat.put(format, formatter);
        }

        return formatter;
    }

    /**
     * Öffnen eine konkretes {@link Workbook}.
     *
     * @param inputStream {@link InputStream}
     *
     * @return {@link Workbook}
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract Workbook openWorkbook(InputStream inputStream) throws Exception;

    /**
     * Liefert den Formatierungsstring eines Datums für ein ExcelXP Format.
     *
     * @param index int, Excel XP Formatindex
     *
     * @return String
     */
    private String getDateFormatByExcelIndex(final int index)
    {
        return switch (index)
                {
                    // case 165 -> "dd.MM.yy HH:mm";
                    // case 167 -> "dd.M.yyyy";
                    // case 169 -> "dd.MMMM.yyyy";
                    // case 170 -> "dd.MMM.yy";
                    // case 171, 191 -> "dd.MMM yy";
                    // case 201 -> "dd.MM.yy";

                    default -> "dd.MM.yyyy";
                };
    }

    /**
     * Selektiert die gewaehlte Zeile im Excelsheet.
     *
     * @param rowNum int
     */
    private void selectRow(final int rowNum)
    {
        if ((this.row == null) || (this.row.getRowNum() != rowNum))
        {
            this.row = this.sheet.getRow(rowNum);
        }
    }
}
