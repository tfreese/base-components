// Created: 25.03.2015
package de.freese.base.persistence.jdbc.sqlite;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
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
    private static SQLiteDataSource dataSource;
    /**
     * Paths.get(System.getProperty("user.dir"), "target")<br>
     * Paths.get(System.getProperty("java.io.tmpdir"), "java")
     */
    private static final Path PATH_TEST = Paths.get(System.getProperty("java.io.tmpdir"), "java", TestSqLite.class.getSimpleName());

    /**
     * Verzeichnis-Struktur zum Testen löschen.
     *
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    protected static void afterAll() throws Exception
    {
        // Würde auch die Dateien anderer Tests löschen.
        // deleteDirectoryRecursiv(PATH_TEST);
    }

    /**
     * @throws Exception Falls was schief geht.
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
        dataSource.setUrl("jdbc:sqlite:" + PATH_TEST.toString() + "/sqlite.db");
    }

    /**
     * @throws Exception Falls was schief geht.
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
                        System.out.println("Key: " + generatedKeys.getInt(1));
                    }
                }
            }

            connection.commit();

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM COMPANY"))
            {
                while (rs.next())
                {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int age = rs.getInt("age");
                    String address = rs.getString("address");
                    float salary = rs.getFloat("salary");

                    System.out.println("ID = " + id);
                    System.out.println("NAME = " + name);
                    System.out.println("AGE = " + age);
                    System.out.println("ADDRESS = " + address);
                    System.out.println("SALARY = " + salary);
                    System.out.println();
                }
            }
        }

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
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
        jdbcTemplate.query("SELECT * FROM COMPANY", (RowCallbackHandler) rs -> {

            int id = rs.getInt("id");
            String name = rs.getString("name");
            int age = rs.getInt("age");
            String address = rs.getString("address");
            double salary = rs.getDouble("salary");

            System.out.println("ID = " + id);
            System.out.println("NAME = " + name);
            System.out.println("AGE = " + age);
            System.out.println("ADDRESS = " + address);
            System.out.println("SALARY = " + salary);
            System.out.println();
        });

        // if (dataSource instanceof SingleConnectionDataSource ds)
        // {
        // ds.destroy();
        // }

        assertTrue(true);
    }
}
