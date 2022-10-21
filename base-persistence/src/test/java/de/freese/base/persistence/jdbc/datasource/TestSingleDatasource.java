// Created: 24.05.2016
package de.freese.base.persistence.jdbc.datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestSingleDatasource
{
    private static SingleDataSource dataSource;

    @AfterAll
    static void afterAll()
    {
        dataSource.destroy();
    }

    @BeforeAll
    static void beforeAll()
    {
        dataSource = new SingleDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:SingleDataSource");
        dataSource.setAutoCommit(false);
    }

    @Test
    void testSingleDataSource() throws Exception
    {
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement())
        {
            stmt.execute("create table PERSON(ID bigint not null, NAME varchar(25) not null, VORNAME varchar(25), primary key (ID))");
        }

        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement("insert into PERSON (ID, NAME) values (?, ?)"))
        {
            stmt.setLong(1, System.currentTimeMillis());
            stmt.setString(2, "Test");

            stmt.execute();
            con.commit();
        }

        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select * from PERSON"))
        {
            if (rs.next())
            {
                assertTrue(rs.getLong("ID") > 0);
                assertEquals("Test", rs.getString("NAME"));
            }
            else
            {
                fail();
            }
        }

        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement())
        {
            stmt.execute("delete from PERSON");
            con.commit();
        }
    }
}
