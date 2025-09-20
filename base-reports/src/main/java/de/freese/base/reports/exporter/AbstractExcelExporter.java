// Created: 07.01.23
package de.freese.base.reports.exporter;

import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Thomas Freese
 */
public abstract class AbstractExcelExporter<T> extends AbstractExporter<T> {
    @Override
    public void export(final OutputStream outputStream, final T model) throws Exception {
        try (Workbook workbook = createWorkbook()) {
            export(workbook, model);

            workbook.write(outputStream);
        }
    }

    public abstract void export(Workbook workbook, T model) throws Exception;

    protected Workbook createWorkbook() {
        // SXSSFWorkbook for Streaming.
        //
        // XSSFWorkbookType.XLSM for Templates.
        // ((XSSFWorkbook) workbook).setWorkbookType(XSSFWorkbookType.XLSM);

        return new XSSFWorkbook();
    }
}
