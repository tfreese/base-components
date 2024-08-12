// Created: 10 Juli 2024
package de.freese.base.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

import javax.swing.table.TableModel;

import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * @author Thomas Freese
 */
public final class ExcelUtils {
    public static void export(final OutputStream outputStream, final String sheetName, final TableModel tableModel) throws IOException {
        export(outputStream,
                sheetName,
                tableModel.getColumnCount(),
                row -> row < tableModel.getRowCount(),
                tableModel::getColumnName,
                tableModel::getValueAt);
    }

    /**
     * @param outputStream {@link OutputStream}; The Stream is not closed.
     * @param rowFinishPredicate {@link IntPredicate}; row -> true/false
     * @param headerFunction {@link IntFunction}; row -> value
     * @param valueFunction {@link BiFunction}; row, column -> value
     */
    public static void export(final OutputStream outputStream,
                              final String sheetName,
                              final int columnCount,
                              final IntPredicate rowFinishPredicate,
                              final IntFunction<String> headerFunction,
                              final BiFunction<Integer, Integer, Object> valueFunction) throws IOException {

        try (Workbook workbook = new SXSSFWorkbook()) {
            final Sheet sheet = workbook.createSheet(sheetName);

            // Headers
            Row row = sheet.createRow(0);

            final Font fontHeader = workbook.createFont();
            fontHeader.setBold(true);

            final CellStyle cellStyleHeader = workbook.createCellStyle();
            cellStyleHeader.setFont(fontHeader);
            cellStyleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

            // Spaltenbreiten
            if (sheet instanceof SXSSFSheet s) {
                s.trackAllColumnsForAutoSizing();
            }

            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                final Cell cell = row.createCell(columnIndex);
                cell.setCellStyle(cellStyleHeader);
                cell.setCellValue(headerFunction.apply(columnIndex));

                if (sheet instanceof SXSSFSheet s) {
                    s.trackColumnForAutoSizing(columnIndex);
                }
            }

            // Values
            int rowIndex = 0;

            while (rowFinishPredicate.test(rowIndex)) {
                row = sheet.createRow(rowIndex + 1);

                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    final Cell cell = row.createCell(columnIndex);
                    final Object value = valueFunction.apply(rowIndex, columnIndex);

                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }

                rowIndex++;
            }

            // Spaltenbreite anpassen
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                sheet.autoSizeColumn(columnIndex);

                if (sheet.getColumnWidth(columnIndex) == 0) {
                    sheet.setColumnWidth(columnIndex, 150);
                }
            }

            workbook.write(outputStream);

            writeMetaData(workbook);

            outputStream.flush();
        }
    }

    private static void writeMetaData(final Workbook workbook) {
        Workbook wb = workbook;

        if (workbook instanceof SXSSFWorkbook sxssfWorkbook) {
            wb = sxssfWorkbook.getXSSFWorkbook();
        }

        if (wb instanceof POIXMLDocument poixmlDocument) {
            final Date today = new Date();

            final POIXMLProperties.CoreProperties properties = poixmlDocument.getProperties().getCoreProperties();
            properties.setTitle("Title");
            properties.setCreator(System.getProperty("user.name"));
            properties.setLastModifiedByUser(System.getProperty("user.name"));
            properties.setCreated(Optional.of(today));
            properties.setModified(Optional.of(today));
        }
    }

    private ExcelUtils() {
        super();
    }
}
