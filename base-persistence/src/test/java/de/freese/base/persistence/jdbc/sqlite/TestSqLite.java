// Erzeugt: 25.03.2015
package de.freese.base.persistence.jdbc.sqlite;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSqLite
{
    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void setUp() throws Exception
    {
        Class.forName("org.sqlite.JDBC");
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testSqliteJDBC() throws Exception
    {
        // Native Libraries deaktivieren für den Zugriff auf die Dateien.
        // System.setProperty("sqlite.purejava", "true");

        // Pfade für native Libraries.
        // System.setProperty("org.sqlite.lib.path", "/home/tommy");
        // System.setProperty("org.sqlite.lib.name", "sqlite-libsqlitejdbc.so");

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:target/sqlite.db"))
        {
            System.out.println("Opened database successfully");

            try (Statement statement = connection.createStatement())
            {
                statement.setQueryTimeout(30); // set timeout to 30 sec.
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
                 ResultSet rs = stmt.executeQuery("SELECT * FROM COMPANY;"))
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
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:target/sqlite.db");
        dataSource.setSuppressClose(true);
        // dataSource.setUsername(this.user);
        // dataSource.setPassword(this.password);
        // dataSource.setAutoCommit(false);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        System.out.println("Opened database successfully");

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

        // List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM COMPANY");
        // for (Map<String, Object> row : result)
        // {
        // int id = (Integer) row.get("id");
        // String name = (String) row.get("name");
        // int age = (Integer) row.get("age");
        // String address = (String) row.get("address");
        // double salary = (Double) row.get("salary");
        // }
        //
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

        dataSource.destroy();

        assertTrue(true);
    }
}
