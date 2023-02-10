// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import de.freese.base.core.logging.LoggingOutputStream;
import de.freese.base.utils.JdbcUtils;

/**
 * @author Thomas Freese
 */
class TestCsvDriver {
    static final Logger LOGGER = LoggerFactory.getLogger(TestCsvDriver.class);
    /**
     * System.out
     */
    private static final PrintStream PRINT_STREAM = new PrintStream(new LoggingOutputStream(LOGGER, Level.DEBUG));

    @AfterAll
    static void afterAll() throws Exception {
        PRINT_STREAM.flush();
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        Class.forName(CsvDriver.class.getName());

        DriverManager.setLogWriter(new PrintWriter(PRINT_STREAM, true));
    }

    @Test
    void testCsvBackedTextTables() throws Exception {
        // Text-Tables should work in Memory-Mode.
        System.setProperty("textdb.allow_full_path", "true");

        StringBuilder url = new StringBuilder();
        url.append("jdbc:hsqldb:mem:").append(System.currentTimeMillis());
        url.append(";shutdown=true");
        url.append(";readonly=true");
        url.append(";files_readonly=true");

        StringBuilder createTable = new StringBuilder();
        createTable.append("create text table MY_CSV (");
        createTable.append(" TEXT varchar(10) PRIMARY KEY,");
        createTable.append(" DATE date,");
        createTable.append(" TIMESTAMP timestamp,");
        createTable.append(" LONG bigint,");
        createTable.append(" DOUBLE decimal(4,3)");
        createTable.append(")");

        StringBuilder setTable = new StringBuilder();
        setTable.append("set table");
        setTable.append(" MY_CSV");
        setTable.append(" source");
        setTable.append(" \"");
        setTable.append("src/test/resources/test1.csv;");
        setTable.append(" ignore_first=true;"); // Header
        setTable.append(" fs=\\semi;"); // Field Separator; \space, \
        setTable.append(" qs=\\quote;"); // Quote Character if not '"
        setTable.append(" all_quoted=true;"); // Data in DoubleQuotes
        setTable.append(" encoding=UTF-8;");
        setTable.append(" cache_rows=10000;"); // max. n Rows ic Cache
        setTable.append(" cache_size=4096"); // max. Cache-Size in kB
        setTable.append('"');

        try (Connection connection = DriverManager.getConnection(url.toString())) {
            try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                statement.execute(createTable.toString());
                statement.execute(setTable.toString());

                try (ResultSet resultSet = statement.executeQuery("select * from MY_CSV")) {
                    JdbcUtils.write(resultSet, PRINT_STREAM);

                    assertEquals("abc", resultSet.getString("TEXT"));
                    assertNotNull(resultSet.getDate("DATE"));
                    assertNotNull(resultSet.getTimestamp("TIMESTAMP"));
                    assertEquals(4321L, resultSet.getLong("LONG"));
                    assertEquals(4.135D, resultSet.getDouble("DOUBLE"));

                    resultSet.next();
                    assertEquals("xyz", resultSet.getString("TEXT"));
                    assertNotNull(resultSet.getDate("DATE"));
                    assertNotNull(resultSet.getTimestamp("TIMESTAMP"));
                    assertEquals(1234L, resultSet.getLong("LONG"));
                    assertEquals(3.146D, resultSet.getDouble("DOUBLE"));
                }
            }
        }
    }

    @Test
    void testCsvDriver01() throws Exception {
        // Struktur
        StringBuilder file1 = new StringBuilder();
        file1.append("[");
        file1.append("src/test/resources/test1.csv");
        file1.append(";TEXT varchar(10) PRIMARY KEY");
        file1.append(",DATE date");
        file1.append(",TIMESTAMP timestamp");
        file1.append(",LONG bigint");
        file1.append(",DOUBLE decimal(4,3)");

        // Layout
        file1.append(";ignore_first=true"); // Header
        file1.append(",fs=\\semi"); // Field Separator
        file1.append(",all_quoted=true"); // Data in DoubleQuotes
        file1.append(",encoding=UTF-8");
        file1.append(",cache_rows=10000"); // max. n Rows in Cache
        file1.append(",cache_size=1024"); // max. Cache-Size in kB
        file1.append("]");

        try (Connection connection = DriverManager.getConnection("jdbc:csv:" + file1); Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet resultSet = statement.executeQuery("select * from TEST1_CSV")) {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals("abc", resultSet.getString("TEXT"));

            resultSet.next();
            assertEquals("xyz", resultSet.getString("TEXT"));
        }
    }

    @Test
    void testCsvDriver02() throws Exception {
        // Struktur
        StringBuilder file1 = new StringBuilder();
        file1.append("[");
        file1.append("src/test/resources/test1.csv");
        file1.append(";TEXT varchar(10) PRIMARY KEY");
        file1.append(",DATE date");
        file1.append(",TIMESTAMP timestamp");
        file1.append(",LONG bigint");
        file1.append(",DOUBLE decimal(4,3)");

        // Layout
        file1.append(";ignore_first=true"); // Header
        file1.append(",fs=\\semi"); // Field Separator
        file1.append(",all_quoted=true"); // Data in DoubleQuotes
        file1.append(",encoding=UTF-8");
        file1.append(",cache_rows=10000"); // max. n Rows in Cache
        file1.append(",cache_size=1024"); // max. Cache-Size in kB
        file1.append("]");

        StringBuilder file2 = new StringBuilder();
        file2.append("[");
        file2.append("src/test/resources/test2.csv");
        file2.append(";TEXT varchar(10) PRIMARY KEY");
        file2.append(",DATE date");
        file2.append(",TIMESTAMP timestamp");
        file2.append(",LONG bigint");
        file2.append(",DOUBLE decimal(4,3)");
        file2.append(";fs=\\comma"); // Field Separator
        file2.append(",tableName=test2");
        file2.append("]");

        StringBuilder file3 = new StringBuilder();
        file3.append("[");
        file3.append("src/test/resources/test3.csv");
        file3.append(";TEXT varchar(10) PRIMARY KEY");
        file3.append(",DATE date");
        file3.append(",TIMESTAMP timestamp");
        file3.append(",LONG bigint");
        file3.append(";fs=\\t"); // Field Separator
        file3.append(",tableName=test3");
        file3.append("]");

        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" min(t1.LONG) as MIN_T1_LONG");
        sql.append(", max(t2.LONG) as MAX_T2_LONG");
        sql.append(", sum(t3.LONG) as SUM_T3_LONG");
        sql.append(" from TEST1_CSV t1");
        sql.append(" inner join TEST2 t2 on t2.TEXT = t1.TEXT");
        sql.append(" inner join TEST3 t3 on t3.TEXT = t1.TEXT");

        try (Connection connection = DriverManager.getConnection("jdbc:csv:" + file1 + file2 + file3); Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet resultSet = statement.executeQuery(sql.toString())) {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(1234, resultSet.getInt("MIN_T1_LONG"), 0);
            assertEquals(4321, resultSet.getInt("MAX_T2_LONG"), 0);
            assertEquals(5555, resultSet.getInt("SUM_T3_LONG"), 0);
        }
    }
}
