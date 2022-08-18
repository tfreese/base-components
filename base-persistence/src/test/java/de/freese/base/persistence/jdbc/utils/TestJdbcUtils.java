package de.freese.base.persistence.jdbc.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import de.freese.base.utils.JdbcUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestJdbcUtils
{
    /**
     *
     */
    private static DataSource dataSource;

    /**
     *
     */
    @AfterAll
    static void afterAll()
    {
        if (dataSource instanceof JdbcConnectionPool pool)
        {
            pool.dispose();
        }

        dataSource = null;
    }

    /**
     * @throws Exception Falls was schiefgeht
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:mem:TestJdbcUtils;DB_CLOSE_DELAY=0;DB_CLOSE_ON_EXIT=true", "sa", "");
        pool.setMaxConnections(2);

        dataSource = pool;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement())
        {
            statement.execute("CREATE TABLE person (id BIGINT NOT NULL, nachname VARCHAR(50) NOT NULL, vorname VARCHAR(50) NOT NULL)");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO person (id, nachname, vorname) values (?, ?, ?)"))
        {
            for (int i = 0; i < 10; i++)
            {
                preparedStatement.setLong(1, i + 1);
                preparedStatement.setString(2, "a".repeat(i + 1));
                preparedStatement.setString(3, "b".repeat(i + 1));

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        }
    }

    /**
     * @throws Exception Falls was schiefgeht
     */
    @Test
    void testObjectTableResultSet() throws Exception
    {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from PERSON"))
        {
            JdbcUtils.write(resultSet, System.out);
        }
    }

    /**
     * @throws Exception Falls was schiefgeht
     */
    @Test
    void testObjectTableResultSetMetaData() throws Exception
    {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from PERSON"))
        {
            JdbcUtils.write(resultSet.getMetaData(), System.out);
        }
    }
}
