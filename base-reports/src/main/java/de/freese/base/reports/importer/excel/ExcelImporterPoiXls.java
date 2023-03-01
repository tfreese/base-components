// Created: 01.03.23
package de.freese.base.reports.importer.excel;

import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author Thomas Freese
 */
public class ExcelImporterPoiXls extends AbstractPoiExcelImporter {
    @Override
    protected Workbook openWorkbook(final InputStream inputStream) throws Exception {
        // return new HSSFWorkbook(new POIFSFileSystem(inputStream));
        return new HSSFWorkbook(inputStream);
    }
}
