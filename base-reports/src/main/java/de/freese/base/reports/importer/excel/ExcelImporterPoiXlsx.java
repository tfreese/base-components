package de.freese.base.reports.importer.excel;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Thomas Freese
 * @since 01.03.2023
 */
public class ExcelImporterPoiXlsx extends AbstractPoiExcelImporter {
    @Override
    protected Workbook openWorkbook(final InputStream inputStream) throws Exception {
        return new XSSFWorkbook(inputStream);
    }
}
