// Created: 24.05.2016
package de.freese.base.persistence.jdbc.datasource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import de.freese.base.persistence.jdbc.DbServerExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSingleDatasource
{
    /**
     *
     */
    @AfterAll
    static void afterClass()
    {
        dataSource.destroy();
    }

    /**
     *
     */
    @BeforeAll
    static void beforeClass()
    {
        dataSource = new SingleDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:" + DbServerExtension.ATOMIC_INTEGER.getAndIncrement());
        dataSource.setAutoCommit(false);
    }
    /**
     *
     */
    private static SingleDataSource dataSource;

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test010Create() throws Exception
    {
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement())
        {
            stmt.execute("create table PERSON(ID bigint not null, NAME varchar(25) not null, VORNAME varchar(25), primary key (ID))");
        }

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test020Insert() throws Exception
    {
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into PERSON (ID, NAME) values (?, ?)"))
        {
            stmt.setLong(1, System.currentTimeMillis());
            stmt.setString(2, "Test");

            stmt.execute();
            con.commit();
        }

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test030Select() throws Exception
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
                    assertTrue(rs.getLong("ID") > 0);
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
    void test040Delete() throws Exception
    {
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement())
        {
            stmt.execute("delete from PERSON");
        }

        assertTrue(true);
    }
}
