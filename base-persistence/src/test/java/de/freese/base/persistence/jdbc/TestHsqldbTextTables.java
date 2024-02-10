// Created: 08.09.2016
package de.freese.base.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
class TestHsqldbTextTables {
    static final Logger LOGGER = LoggerFactory.getLogger(TestHsqldbTextTables.class);
    /**
     * System.out
     */
    private static final PrintStream PRINT_STREAM = new PrintStream(new LoggingOutputStream(LOGGER, Level.DEBUG));

    @AfterAll
    static void afterAll() {
        PRINT_STREAM.flush();
    }

    @BeforeAll
    static void beforeAll() {
        // Class.forName("org.hsqldb.jdbc.JDBCDriver");

        DriverManager.setLogWriter(new PrintWriter(PRINT_STREAM, true));
    }

    @Test
    void testTextTablesWithCsv() throws Exception {
        // Text-Tables should work in Memory-Mode.
        System.setProperty("textdb.allow_full_path", "true");

        final String url = """
                jdbc:hsqldb:mem:%s
                ;shutdown=true
                ;readonly=true
                ;files_readonly=true
                """.formatted(System.currentTimeMillis());

        final String sqlCreateTable = """
                create text table TEST_CSV (
                    TEXT varchar(10) PRIMARY KEY,
                    DATE date,
                    TIMESTAMP timestamp,
                    LONG bigint,
                    DOUBLE decimal(4,3)
                )
                """;

        final String sqlSetTable = """
                set table TEST_CSV source
                "
                src/test/resources/test1.csv;
                ignore_first=true;
                fs=\\semi\\space;
                all_quoted=true;
                encoding=UTF-8;
                cache_rows=10000;
                cache_size=4096
                "
                """;

        try (Connection connection = DriverManager.getConnection(url)) {
            try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                statement.execute(sqlCreateTable);
                statement.execute(sqlSetTable);

                executeSelects(statement);
            }
        }
    }

    private void executeSelects(final Statement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery("select * from test_csv")) {
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

        try (ResultSet resultSet = statement.executeQuery("select min(LONG) as minimum, max(LONG) as maximum, sum(LONG) as summe from test_csv")) {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(1234, resultSet.getInt("MINIMUM"));
            assertEquals(4321, resultSet.getInt("MAXIMUM"));
            assertEquals(5555, resultSet.getInt("SUMME"));
        }

        try (ResultSet resultSet = statement.executeQuery("select min(DOUBLE) as minimum, max(DOUBLE) as maximum, sum(DOUBLE) as summe from test_csv")) {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(3.146, resultSet.getDouble("MINIMUM"), 0);
            assertEquals(4.135, resultSet.getDouble("MAXIMUM"), 0);
            assertEquals(7.281, resultSet.getDouble("SUMME"), 0);
        }

        try (ResultSet resultSet = statement.executeQuery("select DATE, dayofmonth(DATE) as MY_DAY from test_csv")) {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(LocalDate.of(2016, 9, 8), resultSet.getDate("DATE").toLocalDate());
            assertEquals(8, resultSet.getInt("MY_DAY"));

            resultSet.next();
            assertEquals(LocalDate.of(2016, 9, 9), resultSet.getDate("DATE").toLocalDate());
            assertEquals(9, resultSet.getInt("MY_DAY"));
        }

        try (ResultSet resultSet = statement.executeQuery("select TIMESTAMP, hour(TIMESTAMP) as MY_HOUR from test_csv")) {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(LocalDateTime.of(2016, 9, 8, 18, 8, 18), resultSet.getTimestamp("TIMESTAMP").toLocalDateTime());
            assertEquals(18, resultSet.getInt("MY_HOUR"));

            resultSet.next();
            assertEquals(LocalDateTime.of(2016, 9, 9, 19, 9, 19), resultSet.getTimestamp("TIMESTAMP").toLocalDateTime());
            assertEquals(19, resultSet.getInt("MY_HOUR"));
        }

        try (ResultSet resultSet = statement.executeQuery("select * from test_csv order by TIMESTAMP desc")) {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals("xyz", resultSet.getString("TEXT"));

            resultSet.next();
            assertEquals("abc", resultSet.getString("TEXT"));
        }
    }
}
