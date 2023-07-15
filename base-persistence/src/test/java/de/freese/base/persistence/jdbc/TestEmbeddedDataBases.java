package de.freese.base.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestEmbeddedDataBases {
    @RegisterExtension
    static final MultiDatabaseExtension DATABASE_EXTENSION = new MultiDatabaseExtension();

    private static final Runnable DUMMY_CODE = () -> {
        // Empty
    };

    static Stream<Arguments> getDatabases() {
        return DATABASE_EXTENSION.getServers().stream().map(server -> Arguments.of(server.getDatabaseType(), server));
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("createTableWithAutoIncrement")
    void createTableWithAutoIncrement(final EmbeddedDatabaseType databaseType, final DbServerExtension server) throws Exception {
        String sqlCreateTable = switch (databaseType) {
            // "GENERATED ALWAYS AS IDENTITY" - Always provides auto-incremented sequence values. You are not allowed to specify your own values.
            // "GENERATED BY DEFAULT AS IDENTITY" - Provides auto-incremented sequence values only when you are not providing values.
            case DERBY, H2 ->
                    "CREATE TABLE person (id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL PRIMARY KEY, last_name VARCHAR(50) NOT NULL, first_name VARCHAR(50) NOT NULL)";

            // Ist immer NOT NULL.
            case HSQL ->
                    "CREATE TABLE person (id BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL PRIMARY KEY, last_name VARCHAR(50) NOT NULL, first_name VARCHAR(50) NOT NULL)";
        };

        try (Connection connection = server.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sqlCreateTable);

            // DERBY braucht commit.
            connection.commit();
        }

        String sqlInsert = "INSERT INTO person (last_name, first_name) values (?, ?)";

        try (Connection connection = server.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "LastName1");
            preparedStatement.setString(2, "FirstName1");

            int affectedRows = preparedStatement.executeUpdate();
            assertEquals(1, affectedRows);

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                assertTrue(resultSet.next());
                assertEquals(1, resultSet.getLong(1));
            }
        }

        try (Connection connection = server.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE person");
        }

        assertTrue(true);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("dropTables")
    void dropTables(final EmbeddedDatabaseType databaseType, final DbServerExtension server) throws Exception {
        try (Connection connection = server.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            if (EmbeddedDatabaseType.DERBY.equals(databaseType)) {
                // IF EXISTS is not supported by DERBY.
                // Error by trying to drop existing tables.
                // statement.execute("DROP TABLE person CASCADE");
                DUMMY_CODE.run();
            }
            else {
                statement.execute("DROP TABLE IF EXISTS person CASCADE");
            }

            if (EmbeddedDatabaseType.HSQL.equals(databaseType)) {
                // H2 is shutdown immediately.
                // HSQLDB delayed.
                // statement.execute("SHUTDOWN COMPACT");
                DUMMY_CODE.run();
            }
        }

        assertTrue(true);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("getNextSequenceId")
    void getNextSequenceId(final EmbeddedDatabaseType databaseType, final DbServerExtension server) throws Exception {
        String sqlCreateSequence = switch (databaseType) {
            case DERBY -> "CREATE SEQUENCE person_seq AS BIGINT start with 1 increment by 1";

            // Sequence is always BIGINT.
            case H2 -> "CREATE SEQUENCE IF NOT EXISTS person_seq start with 1 increment by 1";

            case HSQL -> "CREATE SEQUENCE IF NOT EXISTS person_seq AS BIGINT start with 1 increment by 1";
        };

        String sqlSelectSequence = switch (databaseType) {
            // INSERT INTO ... VALUES (next value FOR person_seq, ...);

            case DERBY -> "values next value for person_seq";
            case H2 -> "select next value for person_seq";
            case HSQL -> "call next value for person_seq";
        };

        String sqlDropSequence = switch (databaseType) {
            case DERBY -> "DROP SEQUENCE person_seq RESTRICT";
            case H2, HSQL -> "DROP SEQUENCE IF EXISTS person_seq";
        };

        try (Connection connection = server.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sqlCreateSequence);

            try (ResultSet resultSet = statement.executeQuery(sqlSelectSequence)) {
                resultSet.next();
                long id = resultSet.getLong(1);
                assertEquals(1, id);
            }

            statement.execute(sqlDropSequence);
        }

        assertTrue(true);
    }
}
