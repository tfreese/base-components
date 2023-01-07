// Created: 07.01.23
package de.freese.base.reports.exporter;

import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Thomas Freese
 */
public abstract class AbstractExcelExporter<T> extends AbstractExporter<T>
{
    @Override
    public void export(final OutputStream outputStream, final T model) throws Exception
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            export(workbook, model);

            workbook.write(outputStream);
        }
    }

    public abstract void export(Workbook workbook, final T model) throws Exception;
}
