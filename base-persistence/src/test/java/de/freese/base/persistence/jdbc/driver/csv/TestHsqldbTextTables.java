// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

import de.freese.base.core.logging.LoggingOutputStream;
import de.freese.base.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestHsqldbTextTables
{
    /**
     *
     */
    static final Logger LOGGER = LoggerFactory.getLogger(TestHsqldbTextTables.class);
    /**
     * System.out
     */
    private static final PrintStream PRINT_STREAM = new PrintStream(new LoggingOutputStream(LOGGER, Level.DEBUG));

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        PRINT_STREAM.flush();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        // Class.forName(JDBCDriver.class.getName());

        DriverManager.setLogWriter(new PrintWriter(PRINT_STREAM, true));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testTextTableBuilder01() throws Exception
    {
        // @formatter:off
        HsqldbTextTableBuilder builder = HsqldbTextTableBuilder.create()
                .setPath(Paths.get("src/test/resources/test1.csv"))
                .addColumn("TEXT varchar(10) PRIMARY KEY")
                .addColumn("DATE date")
                .addColumn("TIMESTAMP timestamp")
                .addColumn("LONG bigint")
                .addColumn("DOUBLE decimal(4,3)");
        // @formatter:on

        // ResultSet-Types = Damit der Courser wieder zurück gedreht werden kann.
        try (Connection connection = builder.build();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery("select * from TEST1_CSV"))
        {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals("abc", resultSet.getString("TEXT"));

            resultSet.next();
            assertEquals("xyz", resultSet.getString("TEXT"));
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testTextTableBuilder02() throws Exception
    {
        // @formatter:off
        HsqldbTextTableBuilder builder = HsqldbTextTableBuilder.create()
                .setPath(Paths.get("src/test/resources/test1.csv"))
                .setTableName("TEST_CSV")
                .addColumn("TEXT varchar(10) PRIMARY KEY")
                .addColumn("DATE date")
                .addColumn("TIMESTAMP timestamp")
                .addColumn("LONG bigint")
                .addColumn("DOUBLE decimal(4,3)");
        // @formatter:on

        // ResultSet-Types = Damit der Courser wieder zurück gedreht werden kann.
        try (Connection connection = builder.build();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY))
        {
            executeSelects(statement);
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testTextTableBuilder03() throws Exception
    {
        // @formatter:off
        HsqldbTextTableBuilder builder = HsqldbTextTableBuilder.create()
                .setPath(Paths.get("src/test/resources/test2.csv"))
                .setTableName("test_csv")
                .setFieldSeparator(",")
                .addColumn("TEXT varchar(10) PRIMARY KEY")
                .addColumn("DATE date")
                .addColumn("TIMESTAMP timestamp")
                .addColumn("LONG bigint")
                .addColumn("DOUBLE decimal(4,3)");
        // @formatter:on

        // ResultSet-Types = Damit der Courser wieder zurück gedreht werden kann.
        try (Connection connection = builder.build();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY))
        {
            executeSelects(statement);
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testTextTableBuilder04() throws Exception
    {
        // @formatter:off
        HsqldbTextTableBuilder builder = HsqldbTextTableBuilder.create()
                .setPath(Paths.get("src/test/resources/test3.csv"))
                .setTableName("test_csv")
                .setFieldSeparator("\\t")
                .addColumn("TEXT varchar(10) PRIMARY KEY")
                .addColumn("DATE date")
                .addColumn("TIMESTAMP timestamp")
                .addColumn("LONG bigint")
                .addColumn("DOUBLE decimal(4,3)");
        // @formatter:on

        // ResultSet-Types = Damit der Courser wieder zurück gedreht werden kann.
        try (Connection connection = builder.build();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY))
        {
            executeSelects(statement);
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testTextTableBuilder05() throws Exception
    {
        // @formatter:off
        HsqldbTextTableBuilder builder = HsqldbTextTableBuilder.create()
                .setPath(Paths.get("src/test/resources/test4.csv"))
                .setTableName("test_csv")
                .setFieldSeparator("\\t")
                .setAllQuoted(false)
                .addColumn("TEXT varchar(10) PRIMARY KEY")
                .addColumn("DATE date")
                .addColumn("TIMESTAMP timestamp")
                .addColumn("LONG bigint")
                .addColumn("DOUBLE decimal(4,3)");
        // @formatter:on

        // ResultSet-Types = Damit der Courser wieder zurück gedreht werden kann.
        try (Connection connection = builder.build();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY))
        {
            executeSelects(statement);
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testTextTableBuilder06() throws Exception
    {
        // @formatter:off
        HsqldbTextTableBuilder builder1 = HsqldbTextTableBuilder.create()
                .setPath(Paths.get("src/test/resources/test1.csv"))
                .addColumn("TEXT varchar(10) PRIMARY KEY")
                .addColumn("DATE date")
                .addColumn("TIMESTAMP timestamp")
                .addColumn("LONG bigint")
                .addColumn("DOUBLE decimal(4,3)");

        HsqldbTextTableBuilder builder2 = HsqldbTextTableBuilder.create()
                .setPath(Paths.get("src/test/resources/test2.csv"))
                .setTableName("test2")
                .setFieldSeparator(",")
                .addColumn("TEXT varchar(10) PRIMARY KEY")
                .addColumn("DATE date")
                .addColumn("TIMESTAMP timestamp")
                .addColumn("LONG bigint");

        HsqldbTextTableBuilder builder3 = HsqldbTextTableBuilder.create()
                .setPath(Paths.get("src/test/resources/test3.csv"))
                .setTableName("test3")
                .setFieldSeparator("\\t")
                .addColumn("TEXT varchar(10) PRIMARY KEY")
                .addColumn("DATE date")
                .addColumn("TIMESTAMP timestamp")
                .addColumn("LONG bigint");
        // @formatter:on

        StringBuilder sql = new StringBuilder();
        sql.append("select");
        sql.append(" min(t1.LONG) as MIN_T1_LONG");
        sql.append(", max(t2.LONG) as MAX_T2_LONG");
        sql.append(", sum(t3.LONG) as SUM_T3_LONG");
        sql.append(" from TEST1_CSV t1");
        sql.append(" inner join TEST2 t2 on t2.TEXT = t1.TEXT");
        sql.append(" inner join TEST3 t3 on t3.TEXT = t1.TEXT");

        // ResultSet-Types = Damit der Courser wieder zurück gedreht werden kann.
        try (Connection connection = builder1.build(builder2, builder3);
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery(sql.toString()))
        {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(1234, resultSet.getInt("MIN_T1_LONG"), 0);
            assertEquals(4321, resultSet.getInt("MAX_T2_LONG"), 0);
            assertEquals(5555, resultSet.getInt("SUM_T3_LONG"), 0);
        }
    }

    /**
     * @param statement {@link Statement}
     *
     * @throws SQLException Falls was schief geht.
     */
    private void executeSelects(final Statement statement) throws SQLException
    {
        try (ResultSet resultSet = statement.executeQuery("select * from test_csv"))
        {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals("abc", resultSet.getString("TEXT"));

            resultSet.next();
            assertEquals("xyz", resultSet.getString("TEXT"));
        }

        try (ResultSet resultSet = statement.executeQuery("select min(LONG) as minimum, max(LONG) as maximum, sum(LONG) as summe from test_csv"))
        {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(1234, resultSet.getInt("MINIMUM"));
            assertEquals(4321, resultSet.getInt("MAXIMUM"));
            assertEquals(5555, resultSet.getInt("SUMME"));
        }

        try (ResultSet resultSet = statement.executeQuery("select min(DOUBLE) as minimum, max(DOUBLE) as maximum, sum(DOUBLE) as summe from test_csv"))
        {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(3.146, resultSet.getDouble("MINIMUM"), 0);
            assertEquals(4.135, resultSet.getDouble("MAXIMUM"), 0);
            assertEquals(7.281, resultSet.getDouble("SUMME"), 0);
        }

        try (ResultSet resultSet = statement.executeQuery("select DATE, dayofmonth(DATE) as tagdesmonats from test_csv"))
        {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(LocalDate.of(2016, 9, 8), resultSet.getDate("DATE").toLocalDate());
            assertEquals(8, resultSet.getInt("TAGDESMONATS"));

            resultSet.next();
            assertEquals(LocalDate.of(2016, 9, 9), resultSet.getDate("DATE").toLocalDate());
            assertEquals(9, resultSet.getInt("TAGDESMONATS"));
        }

        try (ResultSet resultSet = statement.executeQuery("select TIMESTAMP, hour(TIMESTAMP) as stunde from test_csv"))
        {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals(LocalDateTime.of(2016, 9, 8, 18, 8, 18), resultSet.getTimestamp("TIMESTAMP").toLocalDateTime());
            assertEquals(18, resultSet.getInt("STUNDE"));

            resultSet.next();
            assertEquals(LocalDateTime.of(2016, 9, 9, 19, 9, 19), resultSet.getTimestamp("TIMESTAMP").toLocalDateTime());
            assertEquals(19, resultSet.getInt("STUNDE"));
        }

        try (ResultSet resultSet = statement.executeQuery("select * from test_csv order by TIMESTAMP desc"))
        {
            JdbcUtils.write(resultSet, PRINT_STREAM);

            assertEquals("xyz", resultSet.getString("TEXT"));

            resultSet.next();
            assertEquals("abc", resultSet.getString("TEXT"));
        }
    }
}
