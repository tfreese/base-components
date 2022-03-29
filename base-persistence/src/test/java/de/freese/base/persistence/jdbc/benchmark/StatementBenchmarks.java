package de.freese.base.persistence.jdbc.benchmark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

/**
 * @author Thomas Freese
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
// @org.junit.platform.commons.annotation.Testable
public class StatementBenchmarks extends BenchmarkSettings
{
    /**
     * @author Thomas Freese
     */
    @State(Scope.Benchmark)
    public static class ConnectionHolder
    {
        /**
         *
         */
        private final Connection derby;
        /**
         *
         */
        private final Connection h2;
        /**
         *
         */
        private final Connection hsqldb;
        /**
         *
         */
        Connection connection;
        /**
         *
         */
        @Param(
                {
                        "h2", "hsqldb", "derby"
                })
        String db;

        /**
         * Erstellt ein neues {@link ConnectionHolder} Object.
         */
        public ConnectionHolder()
        {
            try
            {
                this.derby = DriverManager.getConnection("jdbc:derby:memory:jmh;create=true", "sa", "");
                this.h2 = DriverManager.getConnection("jdbc:h2:mem:jmh;DB_CLOSE_DELAY=-1", "sa", "");
                this.hsqldb = DriverManager.getConnection("jdbc:hsqldb:mem:jmh;shutdown=false", "sa", "");
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }

            populateDb(this.h2);
            populateDb(this.hsqldb);
            populateDb(this.derby);
        }

        /**
         * Es sind mehrere Methoden möglich mit unterschiedlichen {@link Level}.
         */
        @Setup
        public void setup()
        {
            this.connection = switch (this.db)
                    {
                        case "h2" -> this.h2;
                        case "hsqldb" -> this.hsqldb;
                        case "derby" -> this.derby;
                        default -> throw new IllegalStateException("Unknown Database: " + this.db);
                    };
        }

        /**
         * Es sind mehrere Methoden möglich mit unterschiedlichen {@link Level}.
         */
        @TearDown
        public void tearDown()
        {
            this.connection = null;
        }

        /**
         * @param connection {@link Connection}
         */
        private void populateDb(final Connection connection)
        {
            try (Statement statement = connection.createStatement())
            {
                String dbName = connection.getMetaData().getDatabaseProductName();

                if (dbName.toLowerCase().contains("derby"))
                {
                    try
                    {
                        statement.execute("DROP TABLE simple_test");
                    }
                    catch (SQLException ex)
                    {
                        // Empty
                    }
                }
                else
                {
                    statement.execute("DROP TABLE IF EXISTS simple_test");
                }

                statement.execute("CREATE TABLE simple_test (name VARCHAR(255))");
                statement.execute("INSERT INTO simple_test VALUES('foo')");
                statement.execute("INSERT INTO simple_test VALUES('bar')");
                statement.execute("INSERT INTO simple_test VALUES('baz')");
            }
            catch (SQLException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * @param connectionHolder {@link ConnectionHolder}
     * @param blackhole {@link Blackhole}
     *
     * @throws SQLException Falls was schiefgeht.
     */
    @Benchmark
    public void preparedStatement(final ConnectionHolder connectionHolder, final Blackhole blackhole) throws SQLException
    {
        try (PreparedStatement statement = connectionHolder.connection.prepareStatement("SELECT * FROM simple_test WHERE name = ?"))
        {
            statement.setString(1, "foo");

            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    blackhole.consume(resultSet.getString("name"));
                }
            }
        }
    }

    /**
     * @param connectionHolder {@link ConnectionHolder}
     * @param blackhole {@link Blackhole}
     *
     * @throws SQLException Falls was schiefgeht.
     */
    @Benchmark
    public void statement(final ConnectionHolder connectionHolder, final Blackhole blackhole) throws SQLException
    {
        try (Statement statement = connectionHolder.connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM simple_test"))
        {
            while (resultSet.next())
            {
                blackhole.consume(resultSet.getString("name"));
            }
        }
    }
}
