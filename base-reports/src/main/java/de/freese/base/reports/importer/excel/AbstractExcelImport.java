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
public abstract class AbstractExcelImport implements ExcelImport {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private InputStream inputStream;

    private boolean throwExcelException = true;

    /**
     * @see ExcelImport#closeExcelFile()
     */
    @Override
    public void closeExcelFile() throws Exception {
        IOUtils.closeQuietly(this.inputStream);
        this.inputStream = null;
    }

    /**
     * @see ExcelImport#openExcelFile(java.lang.String)
     */
    @Override
    public void openExcelFile(final String fileName) throws Exception {
        this.inputStream = new FileInputStream(fileName);

        openExcelFile(this.inputStream);
    }

    /**
     * @see ExcelImport#setThrowExcelException(boolean)
     */
    @Override
    public void setThrowExcelException(final boolean value) {
        this.throwExcelException = value;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected boolean isThrowExcelException() {
        return this.throwExcelException;
    }
}
