// Created: 22.09.2006
package de.freese.base.reports;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.reports.exporter.AbstractExcelExporter;
import de.freese.base.reports.exporter.Exporter;

/**
 * @author Thomas Freese
 */
public final class TestExcelExporterMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestExcelExporterMain.class);

    private static CellStyle cellStyleDefault;
    private static CellStyle cellStyleDefaultBackground;

    public static void main(final String[] args) throws Exception {
        final Exporter<Integer> exporter = new AbstractExcelExporter<>() {
            @Override
            public void export(final Workbook workbook, final Integer dataCount) throws Exception {
                final Sheet sheet = workbook.createSheet("SHEET_NAME");
                sheet.setZoom(100);

                // Header
                Row row = CellUtil.getRow(0, sheet);

                Cell cell = CellUtil.getCell(row, 0);
                cell.setCellValue("Key");
                cell.setCellStyle(getCellStyleDefaultBackground(workbook));

                cell = CellUtil.getCell(row, 1);
                cell.setCellValue("Value");
                cell.setCellStyle(getCellStyleDefaultBackground(workbook));

                // Daten
                for (int rowIndex = 1; rowIndex <= dataCount; rowIndex++) {
                    row = CellUtil.getRow(rowIndex, sheet);

                    cell = CellUtil.getCell(row, 0);
                    cell.setCellValue("Key - " + rowIndex);
                    cell.setCellStyle(getCellStyleDefault(workbook));

                    cell = CellUtil.getCell(row, 1);
                    cell.setCellValue("Value - " + rowIndex);
                    cell.setCellStyle(getCellStyleDefault(workbook));
                }

                sheet.setAutoFilter(new CellRangeAddress(0, dataCount, 0, 1));
                sheet.createFreezePane(0, 1);

                for (int c = 0; c < 2; c++) {
                    sheet.autoSizeColumn(c);
                }
            }
        };

        final Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), "test.xlsx");
        exporter.export(filePath, 50);

        final Runnable task = () -> {
            try {
                Desktop.getDesktop().open(filePath.toFile());
            }
            catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        };

        task.run();
    }

    private static CellStyle getCellStyleDefault(final Workbook workbook) {
        if (cellStyleDefault == null) {
            final Font fontHeader = workbook.createFont();
            fontHeader.setBold(false);
            fontHeader.setFontHeightInPoints((short) 12);

            final CellStyle cs = workbook.createCellStyle();
            cs.setFont(fontHeader);
            cs.setAlignment(HorizontalAlignment.CENTER);
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            cs.setBorderTop(BorderStyle.THIN);
            cs.setBorderLeft(BorderStyle.THIN);
            cs.setBorderBottom(BorderStyle.THIN);
            cs.setBorderRight(BorderStyle.THIN);

            cellStyleDefault = cs;
        }

        return cellStyleDefault;
    }

    private static CellStyle getCellStyleDefaultBackground(final Workbook workbook) {
        if (cellStyleDefaultBackground == null) {
            final CellStyle cs = ((XSSFCellStyle) getCellStyleDefault(workbook)).copy();
            cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cs.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

            cellStyleDefaultBackground = cs;
        }

        return cellStyleDefaultBackground;
    }

    private TestExcelExporterMain() {
        super();
    }
}
