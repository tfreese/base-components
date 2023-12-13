// Created: 01.03.23
package de.freese.base.reports.importer.excel;

import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPoiExcelImporter implements ExcelImporter {

    private final Map<Short, Format> cacheFormat = new HashMap<>();

    private final DataFormatter dataFormatter = new DataFormatter();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private FormulaEvaluator formulaEvaluator;

    @Override
    public List<ExcelSheet> readSheets(final InputStream inputStream) throws Exception {
        Workbook workBook = openWorkbook(inputStream);

        // this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        // this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        this.formulaEvaluator = workBook.getCreationHelper().createFormulaEvaluator();
        workBook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        int numSheets = getNumberOfSheets(workBook);

        List<ExcelSheet> sheets = new ArrayList<>(numSheets);

        for (int i = 0; i < numSheets; i++) {
            Sheet sheet = selectSheet(workBook, i);

            if (!isSheetReadable(sheet)) {
                getLogger().warn("Sheet is not readable: Index = {}, Name = {}", i, sheet.getSheetName());
                continue;
            }

            int numRows = getNumRows(sheet);
            int numColumns = getNumColumns(sheet, numRows);

            List<String[]> rowValues = new ArrayList<>(numRows);

            for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
                String[] rowValue = new String[numColumns];

                Row row = selectRow(sheet, rowIndex);

                for (int columnIndex = 0; columnIndex < numColumns; columnIndex++) {
                    String value = getValueAt(row, rowIndex, columnIndex);

                    rowValue[columnIndex] = value;
                }

                rowValues.add(rowValue);
            }

            sheets.add(new ExcelSheet(sheet.getSheetName(), rowValues));
        }

        return sheets;
    }

    protected Format getDateFormatter(final short format) {
        Format formatter = this.cacheFormat.get(format);

        if (formatter == null) {
            formatter = new SimpleDateFormat(getDateFormatByExcelIndex(format));
            this.cacheFormat.put(format, formatter);
        }

        return formatter;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected int getNumColumns(final Sheet sheet, final int numRows) {
        int maxCol = 0;

        for (int i = 0; i < numRows; i++) {
            Row r = sheet.getRow(i);

            if ((r != null) && (maxCol < r.getLastCellNum())) {
                maxCol = r.getLastCellNum();
            }
        }

        return maxCol + 1;
    }

    protected int getNumRows(final Sheet sheet) {
        int numRows = sheet.getLastRowNum();

        // if(numRows==0) { return 1; }

        return numRows + 1;
    }

    protected int getNumberOfSheets(final Workbook workbook) {
        return workbook.getNumberOfSheets();
    }

    protected String getValueAt(final Row row, final int rowIndex, final int columnIndex) {
        String value = null;

        if (row != null) {
            Cell cell = row.getCell(columnIndex);

            if (cell != null) {
                if (!CellType.FORMULA.equals(cell.getCellType())) {
                    value = this.dataFormatter.formatCellValue(cell);
                }
                else {
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

        if (value == null) {
            value = "";
        }

        return value;
    }

    protected boolean isSheetReadable(final Sheet sheet) {
        /*
         * if(_sheet!=null && sheet.getProtect()==true) { getLogger().warn("****\n\n\n Protected\n\n\n **** "); return false; } for(int
         * i=0;i<getNumRows();i++) { if(sheet!=null && sheet.isRowBroken(i)) { getLogger().warn("****\n\n\n Row is broken\n\n\n **** "); } }
         */
        return sheet != null;
    }

    protected abstract Workbook openWorkbook(InputStream inputStream) throws Exception;

    protected Row selectRow(final Sheet sheet, final int rowIndex) {
        return sheet.getRow(rowIndex);
    }

    protected Sheet selectSheet(final Workbook workbook, final int sheetIndex) {
        return workbook.getSheetAt(sheetIndex);
    }

    private String getDateFormatByExcelIndex(final int index) {
        // case 165 -> "dd.MM.yy HH:mm";
        // case 167 -> "dd.M.yyyy";
        // case 169 -> "dd.MMMM.yyyy";
        // case 170 -> "dd.MMM.yy";
        // case 171, 191 -> "dd.MMM yy";
        // case 201 -> "dd.MM.yy";
        return "dd.MM.yyyy";
    }
}
