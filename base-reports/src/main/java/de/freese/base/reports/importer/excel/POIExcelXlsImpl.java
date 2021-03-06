package de.freese.base.reports.importer.excel;

import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Konkrete Implementierung (POI) des Excelinterfaces für das alte xls-Format.
 *
 * @author Thomas Freese
 */
public final class POIExcelXlsImpl extends AbstractPOIExcelImpl
{
    /**
     * Creates a new {@link POIExcelXlsImpl} object.
     */
    public POIExcelXlsImpl()
    {
        super();
    }

    /**
     * @see de.freese.base.reports.importer.excel.AbstractPOIExcelImpl#openWorkbook(java.io.InputStream)
     */
    @Override
    protected Workbook openWorkbook(final InputStream inputStream) throws Exception
    {
        // POIFSFileSystem fs = new POIFSFileSystem(inputStream);
        // Workbook workbook = new HSSFWorkbook(fs);
        Workbook workbook = new HSSFWorkbook(inputStream);

        return workbook;
    }
}
