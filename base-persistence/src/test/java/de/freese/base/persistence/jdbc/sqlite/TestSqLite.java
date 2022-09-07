// Created: 25.03.2015
package de.freese.base.persistence.jdbc.sqlite;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSqLite
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSqLite.class);
    /**
     * Paths.get(System.getProperty("user.dir"), "target")<br>
     * Paths.get(System.getProperty("java.io.tmpdir"), "java")
     */
    private static final Path PATH_TEST = Paths.get(System.getProperty("java.io.tmpdir"), "java", TestSqLite.class.getSimpleName());
    /**
     *
     */
    private static SQLiteDataSource dataSource;

    /**
     * Verzeichnis-Struktur zum Testen löschen.
     *
     * @throws Exception Falls was schiefgeht.
     */
    @AfterAll
    protected static void afterAll() throws Exception
    {
        // Würde auch die Dateien anderer Tests löschen.
        // deleteDirectoryRecursive(PATH_TEST);
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @BeforeAll
    protected static void beforeAll() throws Exception
    {
        if (Files.notExists(PATH_TEST))
        {
            Files.createDirectories(PATH_TEST);
        }

        Class.forName("org.sqlite.JDBC");

        // Native Libraries deaktivieren für den Zugriff auf die Dateien.
        // System.setProperty("sqlite.purejava", "true");

        // Pfade für native Libraries.
        // System.setProperty("org.sqlite.lib.path", "/home/tommy");
        // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");
        //
        // Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PATH_TEST.toString() + "/sqlite.db")

        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(false);
        config.setReadUncommited(false);

        dataSource = new SQLiteConnectionPoolDataSource(config);
        dataSource.setUrl("jdbc:sqlite:" + PATH_TEST + "/sqlite.db");
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testSqliteJDBC() throws Exception
    {
        try (Connection connection = dataSource.getConnection())
        {
            try (Statement statement = connection.createStatement())
            {
                statement.executeUpdate("drop table if exists COMPANY");
            }

            try (Statement stmt = connection.createStatement())
            {
                StringBuilder sql = new StringBuilder();
                sql.append("CREATE TABLE COMPANY");
                sql.append(" (ID      INTEGER PRIMARY KEY AUTOINCREMENT");
                sql.append(", NAME    TEXT    NOT NULL");
                sql.append(", AGE     INTEGER NOT NULL");
                sql.append(", ADDRESS CHAR(50)");
                sql.append(", SALARY  REAL)");

                stmt.executeUpdate(sql.toString());
            }

            connection.setAutoCommit(false);

            try (Statement stmt = connection.createStatement())
            {
                String sql = "INSERT INTO COMPANY (NAME,AGE,ADDRESS,SALARY) VALUES ('Paul', 32, 'California', 20000)";
                stmt.executeUpdate(sql);

                try (ResultSet generatedKeys = stmt.getGeneratedKeys())
                {
                    while (generatedKeys.next())
                    {
                        LOGGER.debug("Key: {}", generatedKeys.getInt(1));
                    }
                }
            }

            connection.commit();

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM COMPANY"))
            {
                while (rs.next())
                {
                    Map<String, Object> row = new LinkedHashMap<>();
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

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testSqliteSpring() throws Exception
    {
        // SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        // dataSource.setDriverClassName("org.sqlite.JDBC");
        // dataSource.setUrl("jdbc:sqlite:" + PATH_TEST.toString() + "/sqlite.db");
        // dataSource.setSuppressClose(true);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.update("drop table if exists COMPANY");

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE COMPANY");
        sql.append(" (ID      INTEGER PRIMARY KEY AUTOINCREMENT");
        sql.append(", NAME    TEXT    NOT NULL");
        sql.append(", AGE     INTEGER NOT NULL");
        sql.append(", ADDRESS CHAR(50)");
        sql.append(", SALARY  REAL)");
        jdbcTemplate.update(sql.toString());

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        jdbcTemplate.update("INSERT INTO COMPANY (NAME,AGE,ADDRESS,SALARY) VALUES ('Paul', 32, 'California', 20000)");
        transactionManager.commit(transactionStatus);
        // transactionManager.rollback(transactionStatus);

        // SqlRowSet result = jdbcTemplate.query("SELECT * FROM COMPANY", new SqlRowSetResultSetExtractor());
        jdbcTemplate.query("SELECT * FROM COMPANY", rs ->
        {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ID", rs.getInt("ID"));
            row.put("NAME", rs.getInt("NAME"));
            row.put("AGE", rs.getInt("AGE"));
            row.put("ADDRESS", rs.getInt("ADDRESS"));
            row.put("SALARY", rs.getInt("SALARY"));

            LOGGER.debug("{}", row);
        });

        // if (dataSource instanceof SingleConnectionDataSource ds)
        // {
        // ds.destroy();
        // }

        assertTrue(true);
    }
}
