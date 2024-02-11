// Created: 01.03.23
package de.freese.base.reports.importer.excel;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Thomas Freese
 */
public interface ExcelImporter {

    static List<ExcelSheet> ofPoiXls(final Path filePath) throws Exception {
        final ExcelImporter excelImporter = new ExcelImporterPoiXls();

        return excelImporter.readSheets(filePath);
    }

    static List<ExcelSheet> ofPoiXlsx(final InputStream inputStream) throws Exception {
        final ExcelImporter excelImporter = new ExcelImporterPoiXlsx();

        return excelImporter.readSheets(inputStream);
    }

    static List<ExcelSheet> ofPoiXlsx(final Path filePath) throws Exception {
        final ExcelImporter excelImporter = new ExcelImporterPoiXlsx();

        return excelImporter.readSheets(filePath);
    }

    List<ExcelSheet> readSheets(InputStream inputStream) throws Exception;

    default List<ExcelSheet> readSheets(final Path filePath) throws Exception {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return readSheets(inputStream);
        }
    }
}
