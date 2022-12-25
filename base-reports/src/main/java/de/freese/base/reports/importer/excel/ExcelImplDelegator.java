// Created: 28.07.2006
package de.freese.base.reports.importer.excel;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.freese.base.core.exception.StackTraceLimiter;
import org.apache.commons.io.input.ProxyInputStream;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wenn beim Öffnen der Datei mit POI was schiefläuft, wirds mit JExcel versucht.
 *
 * @author Thomas Freese
 */
public class ExcelImplDelegator implements ExcelImport
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelImplDelegator.class);

    /**
     * @author Thomas Freese
     */
    private static class NotClosingInputStream extends ProxyInputStream
    {
        NotClosingInputStream(final InputStream proxy)
        {
            super(proxy);
        }

        /**
         * @see org.apache.commons.io.input.ProxyInputStream#close()
         */
        @Override
        public void close() throws IOException
        {
            // POI schliesst den InputStream auch beim fehlerhaften lesen.
            // super.close();
            this.in = new InputStream()
            {
                /**
                 * @see java.io.InputStream#read()
                 */
                @Override
                public int read() throws IOException
                {
                    return -1;
                }
            };
        }
    }

    private ExcelImport excelImpl;

    private InputStream inputStream;

    private boolean throwExcelException = true;

    /**
     * @see ExcelImport#closeExcelFile()
     */
    @Override
    public void closeExcelFile() throws Exception
    {
        this.excelImpl.closeExcelFile();
        this.excelImpl = null;

        IOUtils.closeQuietly(this.inputStream);
        this.inputStream = null;
    }

    /**
     * @see ExcelImport#closeSheet()
     */
    @Override
    public void closeSheet()
    {
        this.excelImpl.closeSheet();
    }

    /**
     * @see ExcelImport#getNumColumns()
     */
    @Override
    public int getNumColumns()
    {
        return this.excelImpl.getNumColumns();
    }

    /**
     * @see ExcelImport#getNumRows()
     */
    @Override
    public int getNumRows()
    {
        return this.excelImpl.getNumRows();
    }

    /**
     * @see ExcelImport#getNumberOfSheets()
     */
    @Override
    public int getNumberOfSheets()
    {
        return this.excelImpl.getNumberOfSheets();
    }

    /**
     * @see ExcelImport#getSheetName()
     */
    @Override
    public String getSheetName()
    {
        return this.excelImpl.getSheetName();
    }

    /**
     * @see ExcelImport#getValueAt(int, int)
     */
    @Override
    public String getValueAt(final int row, final int column) throws ExcelException
    {
        try
        {
            return this.excelImpl.getValueAt(row, column);
        }
        catch (ExcelException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ExcelException(this.excelImpl.getSheetName(), row, column, ex);
        }
    }

    /**
     * @see ExcelImport#isSheetReadable()
     */
    @Override
    public boolean isSheetReadable()
    {
        return this.excelImpl.isSheetReadable();
    }

    /**
     * @see ExcelImport#openExcelFile(java.io.InputStream)
     */
    @Override
    public void openExcelFile(InputStream inputStream) throws Exception
    {
        if (!(inputStream instanceof BufferedInputStream))
        {
            inputStream = new BufferedInputStream(inputStream);
        }

        // POI schliesst den InputStream auch beim fehlerhaften Lesen.
        // Deswegen wird der Stream in einem Proxy verpackt.
        InputStream is = null;
        Exception lastException = null;

        boolean open = false;
        inputStream.mark(Integer.MAX_VALUE);

        if (!open)
        {
            try
            {
                // Versuchen mit POI (XLSX) zu öffnen.
                lastException = null;
                inputStream.reset();
                is = new NotClosingInputStream(inputStream);
                this.excelImpl = new PoiExcelXlsxImpl();
                this.excelImpl.openExcelFile(is);
                open = true;
            }
            catch (Exception ex)
            {
                lastException = ex;
            }
            finally
            {
                IOUtils.closeQuietly(is);
            }
        }

        if (!open)
        {
            try
            {
                // Versuchen mit POI (XLS) zu öffnen.
                lastException = null;
                inputStream.reset();
                is = new NotClosingInputStream(inputStream);
                this.excelImpl = new PoiExcelXlsImpl();
                this.excelImpl.openExcelFile(is);
                open = true;
            }
            catch (Exception ex)
            {
                lastException = ex;
            }
            finally
            {
                IOUtils.closeQuietly(is);
            }
        }

        is = null;

        if (lastException != null)
        {
            StringBuilder sb = new StringBuilder();
            StackTraceLimiter.printStackTrace(lastException, 4, sb);

            if (LOGGER.isWarnEnabled())
            {
                LOGGER.warn(sb.toString());
            }

            throw lastException;
        }

        this.excelImpl.setThrowExcelException(this.throwExcelException);
    }

    /**
     * @see ExcelImport#openExcelFile(java.lang.String)
     */
    @Override
    public void openExcelFile(final String fileName) throws Exception
    {
        this.inputStream = new BufferedInputStream(new FileInputStream(fileName));
        // this.inputStream = new FileInputStream(fileName);

        openExcelFile(this.inputStream);
    }

    /**
     * @see ExcelImport#selectSheet(int)
     */
    @Override
    public void selectSheet(final int sheetIndex) throws Exception
    {
        this.excelImpl.selectSheet(sheetIndex);
    }

    /**
     * @see ExcelImport#selectSheet(java.lang.String)
     */
    @Override
    public void selectSheet(final String sheetName) throws Exception
    {
        this.excelImpl.selectSheet(sheetName);
    }

    /**
     * @see ExcelImport#setThrowExcelException(boolean)
     */
    @Override
    public void setThrowExcelException(final boolean value)
    {
        this.throwExcelException = value;
    }
}
