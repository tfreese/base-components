package de.freese.base.persistence.jdbc.benchmark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
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
public class StagedResultSizeBenchmarks extends BenchmarkSettings {
    /**
     * @author Thomas Freese
     */
    @State(Scope.Benchmark)
    public static class ConnectionHolder {
        private final Connection derby;
        private final Connection h2;
        private final Connection hsqldb;

        private Connection connection;
        @Param({"h2", "hsqldb", "derby"})
        private String db;
        @Param({"1", "10", "100", "200"})
        private int resultSize;

        public ConnectionHolder() {
            super();

            try {
                derby = DriverManager.getConnection("jdbc:derby:memory:jmh;create=true", "sa", "");
                h2 = DriverManager.getConnection("jdbc:h2:mem:jmh;DB_CLOSE_DELAY=-1", "sa", "");
                hsqldb = DriverManager.getConnection("jdbc:hsqldb:mem:jmh;shutdown=false", "sa", "");
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        /**
         * Es sind mehrere Methoden möglich mit unterschiedlichen {@link Level}.
         */
        @Setup
        public void setup() {
            connection = switch (db) {
                case "h2" -> h2;
                case "hsqldb" -> hsqldb;
                case "derby" -> derby;
                default -> throw new IllegalStateException("Unknown Database: " + db);
            };

            populateDb(connection);
        }

        /**
         * Multiple Methods possible with different {@link Level}.
         */
        @TearDown
        public void tearDown() {
            connection = null;
        }

        private void populateDb(final Connection connection) {
            try (Statement statement = connection.createStatement()) {
                final String dbName = connection.getMetaData().getDatabaseProductName();

                if (dbName.toLowerCase().contains("derby")) {
                    try {
                        statement.execute("DROP TABLE result_sizes");
                    }
                    catch (SQLException _) {
                        // Empty
                    }
                }
                else {
                    statement.execute("DROP TABLE IF EXISTS result_sizes");
                }

                statement.execute("CREATE TABLE result_sizes (id int, name VARCHAR(255))");

                for (int i = 0; i < resultSize; i++) {
                    statement.execute(String.format("INSERT INTO result_sizes VALUES(%d, '%s')", i, UUID.randomUUID()));
                }
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Benchmark
    public void preparedStatement(final ConnectionHolder connectionHolder, final Blackhole blackhole) throws SQLException {
        try (PreparedStatement statement = connectionHolder.connection.prepareStatement("SELECT * FROM result_sizes WHERE name != ?")) {
            statement.setString(1, "foo");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    blackhole.consume(resultSet.getString("name"));
                }
            }
        }
    }

    @Benchmark
    public void statement(final ConnectionHolder connectionHolder, final Blackhole blackhole) throws SQLException {
        try (Statement statement = connectionHolder.connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM result_sizes")) {
            while (resultSet.next()) {
                blackhole.consume(resultSet.getString("name"));
            }
        }
    }
}
