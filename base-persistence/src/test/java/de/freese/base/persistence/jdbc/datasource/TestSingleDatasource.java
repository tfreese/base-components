/**
 * Created: 24.05.2016
 */

package de.freese.base.persistence.jdbc.datasource;

import de.freese.base.persistence.jdbc.TestSuiteJdbc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestSingleDatasource
{
    /**
     *
     */
    private static SingleDataSource dataSource;

    /**
     *
     */
    @AfterAll
    public static void afterClass()
    {
        dataSource.destroy();
    }

    /**
     *
     */
    @BeforeAll
    public static void beforeClass()
    {
        dataSource = new SingleDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:" + TestSuiteJdbc.ATOMIC_INTEGER.getAndIncrement());
        dataSource.setAutoCommit(false);
    }

    /**
     * Erstellt ein neues {@link TestSingleDatasource} Object.
     */
    public TestSingleDatasource()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010Create() throws Exception
    {
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement())
        {
            stmt.execute("CREATE TABLE PERSON(ID BIGINT NOT NULL, NAME VARCHAR(25) NOT NULL, VORNAME VARCHAR(25), PRIMARY KEY (ID))");
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020Insert() throws Exception
    {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into PERSON (ID, NAME) values (?, ?)"))
        {
            stmt.setLong(1, System.currentTimeMillis());
            stmt.setString(2, "Test");

            stmt.execute();
            con.commit();
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030Select() throws Exception
    {
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select * from PERSON"))
        {
            final boolean hasNext = rs.next();

            if (hasNext)
            {
                do
                {
                    assertTrue(true);
                    assertNotNull(rs.getLong("ID"));
                    assertNotNull(rs.getString("NAME"));
                }
                while (rs.next());
            }
            else
            {
                assertTrue(false);
            }
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test040Delete() throws Exception
    {
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement())
        {
            stmt.execute("delete from PERSON");
        }
    }
}
