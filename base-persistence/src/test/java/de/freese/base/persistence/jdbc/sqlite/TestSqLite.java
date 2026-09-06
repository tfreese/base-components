package de.freese.base.persistence.jdbc.sqlite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

/**
 * @author Thomas Freese
 * @since 25.03.2015
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSqLite {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSqLite.class);

    private static final String SQL_CREATE_TABLE = """
                    create table COMPANY (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        NAME TEXT NOT NULL,
                        AGE INTEGER NOT NULL,
                        ADDRESS CHAR(50),
                        SALARY REAL
                    )
            """;
    private static final String SQL_DROP_TABLE = "drop table if exists COMPANY";
    private static final String SQL_INSERT = "insert into COMPANY (NAME, AGE, ADDRESS, SALARY) values (?, ?, ?, ?)";
    private static final String SQL_SELECT = "select * from COMPANY";

    private static SQLiteDataSource dataSource;

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path pathTest;

    @AfterAll
    static void afterAll() {
        if (dataSource instanceof final AutoCloseable ac) {
            try {
                ac.close();
            }
            catch (final Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        else if (dataSource instanceof final org.springframework.beans.factory.DisposableBean db) {
            try {
                db.destroy();
            }
            catch (final Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        // JUL-Logger auf slf4j umleiten.
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Class.forName("org.sqlite.JDBC");

        // Deactivate native Libraries for File-Access.
        // System.setProperty("sqlite.purejava", "true");

        // Paths for native Libraries.
        // System.setProperty("org.sqlite.lib.path", "/home/tommy");
        // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");
        //
        // Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH_TEST.toString() + "/sqlite.db")

        final SQLiteConfig config = new SQLiteConfig();
        // config.setReadOnly(false);
        // config.setReadUncommitted(false);
        // config.setGetGeneratedKeys(true);

        dataSource = new SQLiteConnectionPoolDataSource(config);
        dataSource.setUrl("jdbc:sqlite:" + pathTest + "/sqlite.db");
    }

    @Test
    void testSqliteJdbc() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                assertEquals(0, statement.executeUpdate(SQL_DROP_TABLE));
                assertEquals(0, statement.executeUpdate(SQL_CREATE_TABLE));
            }

            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, "Paul");
                stmt.setInt(2, 32);
                stmt.setString(3, "California");
                stmt.setDouble(4, 20000.0D);

                assertEquals(1, stmt.executeUpdate());

                // try (ResultSet generatedKeys = stmt.executeQuery("SELECT last_insert_rowid()")) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    assertNotNull(generatedKeys);
                    assertTrue(generatedKeys.next());
                    assertEquals(1, generatedKeys.getInt(1));

                    LOGGER.debug("Key: {}", generatedKeys.getInt(1));
                }
            }

            connection.commit();

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(SQL_SELECT)) {
                assertNotNull(rs);

                while (rs.next()) {
                    final Map<String, Object> row = new LinkedHashMap<>();
                    row.put("ID", rs.getInt("ID"));
                    row.put("NAME", rs.getInt("NAME"));
                    row.put("AGE", rs.getInt("AGE"));
                    row.put("ADDRESS", rs.getInt("ADDRESS"));
                    row.put("SALARY", rs.getInt("SALARY"));

                    LOGGER.debug("{}", row);
                }
            }
        }

        assertTrue(true);
    }

    @Test
    void testSqliteSpring() {
        // final SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        // dataSource.setDriverClassName("org.sqlite.JDBC");
        // dataSource.setUrl("jdbc:sqlite:" + PATH_TEST.toString() + "/sqlite.db");
        // dataSource.setSuppressClose(true);

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        assertEquals(0, jdbcTemplate.update(SQL_DROP_TABLE));
        assertEquals(0, jdbcTemplate.update(SQL_CREATE_TABLE));

        final PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        // final int affectedRows = jdbcTemplate.update("INSERT INTO COMPANY (NAME, AGE, ADDRESS, SALARY) VALUES ('Paul', 32, 'California', 20000)");
        final int affectedRows = jdbcTemplate.update(connection -> {
            final PreparedStatement ps = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "Paul");
            ps.setInt(2, 32);
            ps.setString(3, "California");
            ps.setDouble(4, 20000.0D);
            return ps;
        }, keyHolder);

        assertEquals(1, affectedRows);
        assertNotNull(keyHolder.getKey());

        transactionManager.commit(transactionStatus);
        // transactionManager.rollback(transactionStatus);

        // SqlRowSet result = jdbcTemplate.extract(SQL_SELECT, new SqlRowSetResultSetExtractor());
        final List<Map<String, Object>> result = jdbcTemplate.query(SQL_SELECT, (rs, rowNum) -> {
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put("ID", rs.getInt("ID"));
            row.put("NAME", rs.getInt("NAME"));
            row.put("AGE", rs.getInt("AGE"));
            row.put("ADDRESS", rs.getInt("ADDRESS"));
            row.put("SALARY", rs.getInt("SALARY"));

            return row;
        });

        assertNotNull(result);
        assertEquals(1, result.size());
        LOGGER.debug("{}", result.getFirst());

        // if (dataSource instanceof SingleConnectionDataSource ds) {
        // ds.destroy();
        // }

        assertTrue(true);
    }
}
