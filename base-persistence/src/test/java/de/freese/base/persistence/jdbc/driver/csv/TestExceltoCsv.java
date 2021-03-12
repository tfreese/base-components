// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.csv;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestExceltoCsv
{
    /**
    *
    */
    static final Logger LOGGER = LoggerFactory.getLogger(TestExceltoCsv.class);

    /**
     *
     */
    private static PrintStream PRINT_STREAM = System.out;
    // private static PrintStream PRINT_STREAM = new PrintStream(new LoggingOutputStream(LOGGER, Level.DEBUG));

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    static void shutdown() throws Exception
    {
        PRINT_STREAM.flush();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testExcelToCsv01() throws Exception
    {
        // Path excelSource = Paths.get(System.getProperty("user.home"), "Downloads", "test1.xlsx");
        Path excelSource = Paths.get("src/test/resources/test1.xlsx");
        // Path csvDest = Files.createTempFile(Paths.get("."), "tmp-excelTocsv" + System.currentTimeMillis(), ".csv");
        // Path csvDest = Files.createTempFile("tmp-exceltocsv-" + System.currentTimeMillis(), ".csv");;
        // csvDest.toFile().deleteOnExit();

        ExcelToCsv toCsv = new ExcelToCsv();
        toCsv.setColumnIndicies(0, 1, 2, 3, 4);

        // Datum formatieren: 1/1/16 -> 2016-01-01
        toCsv.setFunction(1, value -> {
            String[] date = value.split("/");
            return "20" + date[2] + "-" + date[1] + "-" + date[0];
        });

        // Zahlen formatieren: 0,1 -> 0.1
        Function<String, String> toNumberFunction = value -> value.replace(',', '.');
        toCsv.setFunction(2, toNumberFunction);
        toCsv.setFunction(4, toNumberFunction);

        // toCsv.convert(excelSource, csvDest);
        toCsv.convert(excelSource, new PrintWriter(PRINT_STREAM));

        PRINT_STREAM.println();

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testExcelToCsv02() throws Exception
    {
        Path excelSource = Paths.get("src/test/resources/test1.xlsx");
        // Path csvDest = Files.createTempFile("tmp-exceltocsv-" + System.currentTimeMillis(), ".csv");
        // csvDest.toFile().deleteOnExit();

        ExcelToCsv toCsv = new ExcelToCsv();
        toCsv.setFieldSeparator('\t');
        toCsv.setQuoteCharacter(null);
        toCsv.setColumnIndicies(0, 1, 2, 3, 4);

        // Datum formatieren: 1/1/16 -> 2016-01-01
        toCsv.setFunction(1, value -> {
            String[] date = value.split("/");
            return "20" + date[2] + "-" + date[1] + "-" + date[0];
        });

        // Zahlen formatieren: 0,1 -> 0.1
        Function<String, String> toNumberFunction = value -> value.replace(',', '.');
        toCsv.setFunction(2, toNumberFunction);
        toCsv.setFunction(4, toNumberFunction);

        // toCsv.convert(excelSource, csvDest);
        toCsv.convert(excelSource, new PrintWriter(PRINT_STREAM));

        PRINT_STREAM.println();

        assertTrue(true);
    }
}
