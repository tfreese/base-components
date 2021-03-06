/**
 * 12.07.2006
 */
package de.freese.base.reports.importer.excel;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.core.progress.LoggerProgressCallback;
import de.freese.base.core.progress.ProgressCallback;

/**
 * Zwischenspeichern von Excelinhalten, CacheKey setzt sich aus dem Namen der Datei und dessen Zeitstempel zusammen.
 *
 * @author Thomas Freese
 */
public final class ExcelCache
{
    /**
     *
     */
    private static final ExcelCache INSTANCE = new ExcelCache();

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelCache.class);

    /**
     * Liefert die Instanz des ExcelCaches.
     *
     * @return {@link ExcelCache}
     */
    public static final ExcelCache getInstance()
    {
        return INSTANCE;
    }

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // ExcelSheet[] sheets =
        // getInstance().getExcelSheets("Y:\\Eigene Dateien\\FBU_1206.xls");
        //
        // getInstance().clear();
    }

    /**
     *
     */
    private final Map<String, ExcelSheet[]> cache = new ConcurrentHashMap<>();

    /**
     * Creates a new {@link ExcelCache} object.
     */
    private ExcelCache()
    {
        super();
    }

    /**
     * Leeren des ExcelCaches.
     */
    public void clear()
    {
        this.cache.clear();
    }

    /**
     * Liefert alle Inhalte der ExcelSheets als DatenContainer.
     *
     * @param fileName String
     * @param throwExceptions boolean; true=wirft Exceptions, false= loggt Exceptions.
     * @return {@link ExcelSheet}[]
     * @throws Exception Falls was schief geht.
     */
    public ExcelSheet[] getExcelSheets(final String fileName, final boolean throwExceptions) throws Exception
    {
        return getExcelSheets(fileName, throwExceptions, new LoggerProgressCallback(LOGGER));
    }

    /**
     * Liefert alle Inhalte der ExcelSheets als DatenContainer.
     *
     * @param fileName String
     * @param throwExceptions boolean; true=wirft Exceptions, false= loggt Exceptions.
     * @param progressCallback {@link ProgressCallback}
     * @return {@link ExcelSheet}[]
     * @throws Exception Falls was schief geht.
     */
    public ExcelSheet[] getExcelSheets(final String fileName, final boolean throwExceptions, final ProgressCallback progressCallback) throws Exception
    {
        ExcelSheet[] sheets = null;

        File file = new File(fileName);

        String cacheKey = fileName + "_" + file.lastModified() + "_" + throwExceptions;

        sheets = this.cache.get(cacheKey);

        if (sheets == null)
        {
            IExcelImport excel = new ExcelImplDelegator();
            excel.setThrowExcelException(throwExceptions);
            excel.openExcelFile(fileName);

            int numSheets = excel.getNumberOfSheets();
            sheets = new ExcelSheet[numSheets];

            for (int i = 0; i < numSheets; i++)
            {
                if (excel.isSheetReadable())
                {
                    excel.selectSheet(i);

                    ExcelSheet excelSheet = new ExcelSheet(fileName, excel.getSheetName());
                    excelSheet.readCurrentSheet(excel);

                    sheets[i] = excelSheet;
                    progressCallback.setProgress(i + 1L, numSheets);
                }
            }

            this.cache.put(cacheKey, sheets);
        }

        return sheets;
    }
}
