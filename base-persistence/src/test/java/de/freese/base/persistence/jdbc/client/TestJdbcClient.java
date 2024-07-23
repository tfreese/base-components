// Created: 12.11.23
package de.freese.base.persistence.jdbc.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.MultiDatabaseExtension;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;
import de.freese.base.persistence.jdbc.transaction.Transaction;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestJdbcClient {
    @RegisterExtension
    static final MultiDatabaseExtension DATABASE_EXTENSION = new MultiDatabaseExtension(true);

    static Stream<Arguments> getDatabases() {
        return DATABASE_EXTENSION.getServers().stream().map(server -> Arguments.of(server.getDatabaseType(), server));
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testCall")
    void testCall(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        if (EmbeddedDatabaseType.HSQL.equals(server.getDatabaseType())) {
            // SQL Syntax won't work with HSQLDB.
            return;
        }

        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final double result = jdbcClient.sql("{? = call sin(?)}").call(stmt -> {
                    stmt.setDouble(2, 180D);
                    stmt.registerOutParameter(1, Types.DOUBLE);
                },
                stmt -> stmt.getDouble(1));

        assertEquals(Math.sin(180D), result);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testExecute")
    void testExecute(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testExecuteUpdate")
    void testExecuteUpdate(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testExecuteUpdatePrepared")
    void testExecuteUpdatePrepared(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)").executeUpdate(stmt -> stmt.setString(1, "myName"));
        assertEquals(1, affectedRows);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testExecuteUpdatePreparedGeneratedKeys")
    void testExecuteUpdatePreparedGeneratedKeys(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final List<Long> generatedKeys = new ArrayList<>();
        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)").executeUpdate(stmt -> stmt.setString(1, "myName"), generatedKeys::add);
        assertEquals(1, affectedRows);
        assertEquals(1, generatedKeys.size());
        assertEquals(1, generatedKeys.getFirst());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testInsertBatch")
    void testInsertBatch(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final JdbcClient jdbcClient = new JdbcClient(server.getDataSource());
        jdbcClient.execute(createTableSql(databaseType));

        final List<String> names = List.of("name1", "name2", "name3");

        final int affectedRows = jdbcClient.insert("insert into person (name) values (?)").executeBatch(names, (ps, name) -> ps.setString(1, name), 2);
        assertEquals(names.size(), affectedRows);

        final List<Map<String, Object>> result = jdbcClient.select("select * from person order by name asc").executeAsMap();
        assertNotNull(result);
        assertEquals(names.size(), result.size());

        for (int i = 0; i < names.size(); i++) {
            assertEquals(2, result.get(i).size());
            assertEquals(i + 1L, result.get(i).get("ID"));
            assertEquals("name" + (i + 1), result.get(i).get("NAME"));
        }

        jdbcClient.execute("drop table person");
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testQueryAsList")
    void testQueryAsList(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        final List<Person> resultList = jdbcClient.sql("select * from person").query().asList(new PersonRowMapper());
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.getFirst().id());
        assertEquals("myName", resultList.getFirst().name());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testQueryAsListOfMaps")
    void testQueryAsListOfMaps(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        final List<Map<String, Object>> resultList = jdbcClient.sql("select * from person").query().asListOfMaps();
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(2, resultList.getFirst().size());

        resultList.forEach(map -> {
            assertEquals(1L, map.get("ID"));
            assertEquals("myName", map.get("NAME"));
        });
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testQueryAsListPrepared")
    void testQueryAsListPrepared(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        final List<Person> resultList = jdbcClient.sql("select * from person where id = ?").query().asList(new PersonRowMapper(), stmt -> stmt.setLong(1, 1));
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.getFirst().id());
        assertEquals("myName", resultList.getFirst().name());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testQueryAsMap")
    void testQueryAsMap(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        final Map<Long, List<Person>> resultMap = jdbcClient.sql("select * from person").query().asMap(new PersonRowMapper(), Person::id, Function.identity());
        assertNotNull(resultMap);
        assertEquals(1, resultMap.size());
        assertEquals(1, resultMap.get(1L).size());

        resultMap.get(1L).forEach(person -> {
            assertEquals(1L, person.id());
            assertEquals("myName", person.name());
        });
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testQueryAsSet")
    void testQueryAsSet(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        final Set<Person> resultSet = jdbcClient.sql("select * from person").query().asSet(new PersonRowMapper());
        assertNotNull(resultSet);
        assertEquals(1, resultSet.size());
        resultSet.forEach(person -> {
            assertEquals(1L, person.id());
            assertEquals("myName", person.name());
        });
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testQueryResultSetExtractor")
    void testQueryResultSetExtractor(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        final Person person = jdbcClient.sql("select * from person").query().as(resultSet -> {
            resultSet.next();
            return new Person(resultSet.getLong("ID"), resultSet.getString("NAME"));
        });
        assertNotNull(person);
        assertEquals(1L, person.id());
        assertEquals("myName", person.name());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testQueryResultSetExtractorPrepared")
    void testQueryResultSetExtractorPrepared(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final AbstractJdbcClient jdbcClient = new AbstractJdbcClient(server.getDataSource()) {
        };

        final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        final Person person = jdbcClient.sql("select * from person where id = ?").query().as(resultSet -> {
                    resultSet.next();
                    return new Person(resultSet.getLong("ID"), resultSet.getString("NAME"));
                },
                stmt -> stmt.setLong(1, 1));
        assertNotNull(person);
        assertEquals(1L, person.id());
        assertEquals("myName", person.name());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testTransaction")
    void testTransaction(final EmbeddedDatabaseType databaseType, final DbServerExtension server) throws Exception {
        final JdbcClient jdbcClient = new JdbcClient(server.getDataSource());
        jdbcClient.execute(createTableSql(databaseType));

        final List<String> names = List.of("name1", "name2");

        final Callable<Integer> insertCallable = () -> jdbcClient.insert("insert into person (name) values (?)").executeBatch(names, (ps, name) -> ps.setString(1, name), 2);

        try (Transaction transaction = jdbcClient.createTransaction()) {
            transaction.begin();

            final int affectedRows = ScopedValue.callWhere(JdbcClient.TRANSACTION, transaction, insertCallable);
            assertEquals(names.size(), affectedRows);

            // Out of TransactionScope.
            // final List<Map<String, Object>> result = new JdbcClient(server.getDataSource()).select("select * from person order by name asc").executeAsMap();
            // assertNotNull(result);
            // assertEquals(0, result.size());

            transaction.commit();
        }

        final List<Map<String, Object>> result = jdbcClient.select("select * from person order by name asc").executeAsMap();
        assertNotNull(result);
        assertEquals(names.size(), result.size());

        jdbcClient.execute("drop table person");
    }

    private String createTableSql(final EmbeddedDatabaseType databaseType) {
        return switch (databaseType) {
            // "GENERATED ALWAYS AS IDENTITY" - Always provides auto-incremented sequence values. You are not allowed to specify your own values.
            // "GENERATED BY DEFAULT AS IDENTITY" - Provides auto-incremented sequence values only when you are not providing values.
            case DERBY, H2, HSQL -> """
                    CREATE TABLE person (
                        id BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
                    )
                    """;
        };
    }
}
