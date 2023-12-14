// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.csv;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import de.freese.base.core.logging.LoggingOutputStream;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestExcelToCsv {
    static final Logger LOGGER = LoggerFactory.getLogger(TestExcelToCsv.class);
    /**
     * System.out
     */
    private static final PrintStream PRINT_STREAM = new PrintStream(new LoggingOutputStream(LOGGER, Level.DEBUG));

    @AfterAll
    static void afterAll() throws Exception {
        PRINT_STREAM.flush();
    }

    @Test
    void testExcelToCsv01() throws Exception {
        final Path excelSource = Paths.get("src/test/resources/test1.xlsx");

        final ExcelToCsv toCsv = new ExcelToCsv();
        toCsv.setColumnIndices(0, 1, 2, 3, 4);

        // Format date: 1/1/16 -> 2016-01-01
        toCsv.setConvertFunction(1, value -> {
            final String[] date = value.split("/");
            return "20" + date[2] + "-" + date[1] + "-" + date[0];
        });

        // Format numbers: 0,1 -> 0.1
        final UnaryOperator<String> toNumberFunction = value -> value.replace(',', '.');
        toCsv.setConvertFunction(2, toNumberFunction);
        toCsv.setConvertFunction(4, toNumberFunction);

        // toCsv.convert(excelSource, csvDest);
        toCsv.convert(excelSource, new PrintWriter(PRINT_STREAM, true));

        PRINT_STREAM.println();

        assertTrue(true);
    }

    @Test
    void testExcelToCsv02() throws Exception {
        final Path excelSource = Paths.get("src/test/resources/test1.xlsx");

        final ExcelToCsv toCsv = new ExcelToCsv();
        toCsv.setFieldSeparator('\t');
        toCsv.setQuoteCharacter(null);
        toCsv.setColumnIndices(0, 1, 2, 3, 4);

        // Format date: 1/1/16 -> 2016-01-01
        toCsv.setConvertFunction(1, value -> {
            final String[] date = value.split("/");
            return "20" + date[2] + "-" + date[1] + "-" + date[0];
        });

        // Format numbers: 0,1 -> 0.1
        final UnaryOperator<String> toNumberFunction = value -> value.replace(',', '.');
        toCsv.setConvertFunction(2, toNumberFunction);
        toCsv.setConvertFunction(4, toNumberFunction);

        // toCsv.convert(excelSource, csvDest);
        toCsv.convert(excelSource, new PrintWriter(PRINT_STREAM, true));

        PRINT_STREAM.println();

        assertTrue(true);
    }
}
