// Created: 12.11.23
package de.freese.base.persistence.jdbc.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.JanitorInvocationInterceptor;
import de.freese.base.persistence.jdbc.MultiDatabaseExtension;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;
import de.freese.base.persistence.jdbc.transaction.Transaction;

/**
 * @author Thomas Freese
 */
// @TestMethodOrder(MethodOrderer.MethodName.class)
@ExtendWith(JanitorInvocationInterceptor.class)
class TestJdbcClient {
    @RegisterExtension
    static final MultiDatabaseExtension DATABASE_EXTENSION = new MultiDatabaseExtension(true);

    private static final class DefaultJdbcClient extends AbstractJdbcClient {
        public DefaultJdbcClient(final DataSource dataSource) {
            super(dataSource);
        }
    }

    static Stream<Arguments> getJdbcClients() {
        return DATABASE_EXTENSION.getServers().stream().map(server -> Arguments.of(server, new DefaultJdbcClient(server.getDataSource())));
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testCall")
    void testCall(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
        if (EmbeddedDatabaseType.HSQL.equals(dbServerExtension.getDatabaseType())) {
            // SQL Syntax won't work with HSQLDB.
            return;
        }

        final double result = jdbcClient.sql("{? = call sin(?)}").call(stmt -> {
                    stmt.setDouble(2, 180D);
                    stmt.registerOutParameter(1, Types.DOUBLE);
                },
                stmt -> stmt.getDouble(1));

        assertEquals(Math.sin(180D), result);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testExecuteUpdate")
    void testExecuteUpdate(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
        // final boolean result = jdbcClient.sql(createTableSql(databaseType)).execute();
        // assertFalse(result);

        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        // jdbcClient.sql("drop table person").execute();
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testExecuteUpdateBatch")
    void testExecuteUpdateBatch(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
        final List<String> names = List.of("name1", "name2", "name3");

        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)").executeUpdateBatch(2, names, (ps, name) -> ps.setString(1, name));
        assertEquals(names.size(), affectedRows);

        final List<Map<String, Object>> result = jdbcClient.sql("select * from person order by name asc").query().asListOfMaps();
        assertNotNull(result);
        assertEquals(names.size(), result.size());

        for (int i = 0; i < names.size(); i++) {
            assertEquals(2, result.get(i).size());
            assertEquals(i + 1L, result.get(i).get("ID"));
            assertEquals("name" + (i + 1), result.get(i).get("NAME"));
        }
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testExecuteUpdatePrepared")
    void testExecuteUpdatePrepared(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)").executeUpdate(stmt -> stmt.setString(1, "myName"));
        assertEquals(1, affectedRows);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testExecuteUpdatePreparedGeneratedKeys")
    void testExecuteUpdatePreparedGeneratedKeys(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
        final List<Long> generatedKeys = new ArrayList<>();
        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)").executeUpdate(stmt -> stmt.setString(1, "myName"), generatedKeys::add);
        assertEquals(1, affectedRows);
        assertEquals(1, generatedKeys.size());
        assertEquals(1, generatedKeys.getFirst());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsList")
    void testQueryAsList(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        final List<Person> resultList = jdbcClient.sql("select * from person").query().asList(new PersonRowMapper());
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.getFirst().id());
        assertEquals("myName", resultList.getFirst().name());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsListOfMaps")
    void testQueryAsListOfMaps(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
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
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsListPrepared")
    void testQueryAsListPrepared(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        final List<Person> resultList = jdbcClient.sql("select * from person where id = ?").query().asList(new PersonRowMapper(), stmt -> stmt.setLong(1, 1));
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.getFirst().id());
        assertEquals("myName", resultList.getFirst().name());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsMap")
    void testQueryAsMap(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
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
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsSet")
    void testQueryAsSet(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
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
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryResultSetExtractor")
    void testQueryResultSetExtractor(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
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
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryResultSetExtractorPrepared")
    void testQueryResultSetExtractorPrepared(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) {
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
    @MethodSource("getJdbcClients")
    @DisplayName("testTransaction")
    void testTransaction(final DbServerExtension dbServerExtension, final AbstractJdbcClient jdbcClient) throws Exception {
        final List<String> names = List.of("name1", "name2", "name3");

        final Callable<Integer> insertCallable = () -> jdbcClient.sql("insert into person (name) values (?)").executeUpdateBatch(2, names, (ps, name) -> ps.setString(1, name));

        try (Transaction transaction = jdbcClient.createTransaction()) {
            transaction.begin();

            final int affectedRows = ScopedValue.callWhere(JdbcClient.TRANSACTION, transaction, insertCallable);
            assertEquals(names.size(), affectedRows);

            // Out of TransactionScope -> Is Blocking ?!
            // final List<Map<String, Object>> result = new DefaultJdbcClient(jdbcClient.getDataSource()).sql("select * from person").query().asListOfMaps();
            // assertNotNull(result);
            // assertEquals(0, result.size());

            transaction.commit();
        }

        final List<Map<String, Object>> result = jdbcClient.sql("select * from person").query().asListOfMaps();
        assertNotNull(result);
        assertEquals(names.size(), result.size());
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
