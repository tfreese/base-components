package de.freese.base.reports.importer.excel;

import java.io.InputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Konkrete Implementierung (POI) des Excelinterfaces für das neue xlsx-Format.
 *
 * @author Thomas Freese
 */
public final class POIExcelXlsxImpl extends AbstractPOIExcelImpl
{
    /**
     * Creates a new {@link POIExcelXlsxImpl} object.
     */
    public POIExcelXlsxImpl()
    {
        super();
    }

    /**
     * @see de.freese.base.reports.importer.excel.AbstractPOIExcelImpl#openWorkbook(java.io.InputStream)
     */
    @Override
    protected Workbook openWorkbook(final InputStream inputStream) throws Exception
    {
        Workbook workbook = new XSSFWorkbook(inputStream);

        return workbook;
    }
}
