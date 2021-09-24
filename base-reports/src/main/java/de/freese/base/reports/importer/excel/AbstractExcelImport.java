package de.freese.base.reports.importer.excel;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis Implementierung des Excelinterfaces für POI.
 *
 * @author Thomas Freese
 */
public abstract class AbstractExcelImport implements IExcelImport
{
    /**
     *
     */
    private InputStream inputStream;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private boolean throwExcelException = true;

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#closeExcelFile()
     */
    @Override
    public void closeExcelFile() throws Exception
    {
        IOUtils.closeQuietly(this.inputStream);
        this.inputStream = null;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return boolean
     */
    protected boolean isThrowExcelException()
    {
        return this.throwExcelException;
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#openExcelFile(java.lang.String)
     */
    @Override
    public void openExcelFile(final String fileName) throws Exception
    {
        this.inputStream = new FileInputStream(fileName);

        openExcelFile(this.inputStream);
    }

    /**
     * @see de.freese.base.reports.importer.excel.IExcelImport#setThrowExcelException(boolean)
     */
    @Override
    public void setThrowExcelException(final boolean value)
    {
        this.throwExcelException = value;
    }
}
