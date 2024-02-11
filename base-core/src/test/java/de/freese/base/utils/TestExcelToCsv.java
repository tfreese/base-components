// Created: 08.09.2016
package de.freese.base.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    static void afterAll() {
        PRINT_STREAM.flush();
    }

    @Test
    void testExcelToCsv() throws Exception {
        final Path excelSource = Paths.get("src/test/resources/test1.xlsx");

        final ExcelToCsv toCsv = new ExcelToCsv();
        toCsv.setParseableColumns(List.of(0, 1, 2, 3, 4));

        toCsv.setColumnValueConverter((column, value) -> {
            if (column == 1) {
                // Format date: 1/1/16 -> 2016-01-01
                final String[] date = value.split("/");

                return "20" + date[2] + "-" + date[1] + "-" + date[0];

            }

            if (column == 2 || column == 4) {
                // Format numbers: 0,1 -> 0.1
                return value.replace(',', '.');
            }

            return value;
        });

        // toCsv.convert(excelSource, csvDest);
        toCsv.convert(excelSource, new PrintWriter(PRINT_STREAM, true));

        PRINT_STREAM.println();

        assertTrue(true);
    }
}
