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
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
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
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForAll;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForFetchSize;
import de.freese.base.persistence.jdbc.transaction.Transaction;

/**
 * @author Thomas Freese
 */
@SuppressWarnings({"preview", "unused"})
@TestMethodOrder(MethodOrderer.MethodName.class)
@ExtendWith(JanitorInvocationInterceptor.class)
class TestJdbcClient {
    @RegisterExtension
    static final MultiDatabaseExtension DATABASE_EXTENSION = new MultiDatabaseExtension(true);

    static Stream<Arguments> getJdbcClients() {
        return DATABASE_EXTENSION.getServers().stream().map(server -> Arguments.of(server, new JdbcClient(server.getDataSource())));
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testCall")
    void testCall(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        if (EmbeddedDatabaseType.HSQL.equals(dbServerExtension.getDatabaseType())) {
            // Won't work with HSQLDB -> "parameter index out of range: 2"
            return;
        }

        final double result = jdbcClient.sql("{? = call sin(?)}")
                .call(stmt -> {
                            stmt.setDouble(2, 180D);
                            stmt.registerOutParameter(1, Types.DOUBLE);
                        },
                        stmt -> stmt.getDouble(1)
                );

        assertEquals(Math.sin(180D), result, 0.000_000_1D);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testExecuteUpdate")
    void testExecuteUpdate(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();

        assertEquals(1, affectedRows);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testExecuteUpdateBatch")
    void testExecuteUpdateBatch(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final List<String> names = List.of("name1", "name2", "name3");

        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)").executeUpdateBatch(2, names, (ps, name) -> ps.setString(1, name));

        assertEquals(names.size(), affectedRows);

        final List<Map<String, Object>> result = jdbcClient.sql("select * from person order by id asc")
                .query()
                .asListOfMaps();

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
    void testExecuteUpdatePrepared(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)")
                .statementSetter(stmt -> stmt.setString(1, "myName"))
                .executeUpdate();

        assertEquals(1, affectedRows);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testExecuteUpdatePreparedGeneratedKeys")
    void testExecuteUpdatePreparedGeneratedKeys(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final List<Long> generatedKeys = new ArrayList<>();
        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)")
                .statementSetter(stmt -> stmt.setString(1, "myName"))
                .executeUpdate(generatedKeys::add);

        assertEquals(1, affectedRows);
        assertEquals(1, generatedKeys.size());
        assertEquals(1, generatedKeys.getFirst());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsFlux")
    void testQueryAsFlux(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final List<String> names = List.of("name1", "name2", "name3");

        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)")
                .executeUpdateBatch(2, names, (ps, name) -> ps.setString(1, name));

        assertEquals(names.size(), affectedRows);

        final List<Person> result = jdbcClient.sql("select * from person order by id asc")
                .query()
                .asFlux(new PersonRowMapper())
                .collectList()
                .block();

        assertNotNull(result);
        assertEquals(3, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(i + 1L, result.get(i).id());
            assertEquals("name" + (i + 1), result.get(i).name());
        }
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsList")
    void testQueryAsList(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();

        assertEquals(1, affectedRows);

        List<Person> resultList = jdbcClient.sql("select * from person")
                .query()
                .asList(new PersonRowMapper());

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.getFirst().id());
        assertEquals("myName", resultList.getFirst().name());

        resultList = jdbcClient.sql("select * from person where id = ?")
                .statementSetter(stmt -> stmt.setLong(1, 1))
                .query()
                .asList(new PersonRowMapper());

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.getFirst().id());
        assertEquals("myName", resultList.getFirst().name());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsListOfMaps")
    void testQueryAsListOfMaps(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();

        assertEquals(1, affectedRows);

        final List<Map<String, Object>> resultList = jdbcClient.sql("select * from person")
                .query()
                .asListOfMaps();

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
    @DisplayName("testQueryAsMap")
    void testQueryAsMap(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();

        assertEquals(1, affectedRows);

        final Map<Long, List<Person>> resultMap = jdbcClient.sql("select * from person")
                .query()
                .asMap(new PersonRowMapper(), Person::id, Function.identity());

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
    @DisplayName("testQueryAsPublisher")
    void testQueryAsPublisher(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final List<String> names = List.of("name1", "name2", "name3");

        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)")
                .executeUpdateBatch(2, names, (ps, name) -> ps.setString(1, name));

        assertEquals(names.size(), affectedRows);

        final List<Person> result = new ArrayList<>();

        final List<Flow.Subscriber<Person>> subscribers = List.of(
                new ResultSetSubscriberForAll<>(result::add),
                new ResultSetSubscriberForEachObject<>(result::add),
                new ResultSetSubscriberForFetchSize<>(result::add, 2));

        for (Flow.Subscriber<Person> subscriber : subscribers) {
            result.clear();

            final Flow.Publisher<Person> publisher = jdbcClient.sql("select * from person order by id asc").query().asPublisher(new PersonRowMapper());

            publisher.subscribe(subscriber);

            assertNotNull(result);
            assertEquals(3, result.size());

            for (int i = 0; i < result.size(); i++) {
                assertEquals(i + 1L, result.get(i).id());
                assertEquals("name" + (i + 1), result.get(i).name());
            }
        }
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsSet")
    void testQueryAsSet(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();

        assertEquals(1, affectedRows);

        final Set<Person> resultSet = jdbcClient.sql("select * from person")
                .query()
                .asSet(new PersonRowMapper());

        assertNotNull(resultSet);
        assertEquals(1, resultSet.size());
        resultSet.forEach(person -> {
            assertEquals(1L, person.id());
            assertEquals("myName", person.name());
        });
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryAsStream")
    void testQueryAsStream(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final List<String> names = List.of("name1", "name2", "name3");

        final int affectedRows = jdbcClient.sql("insert into person (name) values (?)")
                .executeUpdateBatch(2, names, (ps, name) -> ps.setString(1, name));

        assertEquals(names.size(), affectedRows);

        List<Person> result = null;

        try (Stream<Person> stream = jdbcClient.sql("select * from person order by id asc").query().asStream(new PersonRowMapper())) {
            result = stream.toList();
        }

        assertNotNull(result);
        assertEquals(3, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(i + 1L, result.get(i).id());
            assertEquals("name" + (i + 1), result.get(i).name());
        }
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testQueryResultSetExtractor")
    void testQueryResultSetExtractor(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) {
        final int affectedRows = jdbcClient.sql("insert into person (name) values ('myName')").executeUpdate();
        assertEquals(1, affectedRows);

        Person person = jdbcClient.sql("select * from person")
                .query()
                .as(resultSet -> {
                    resultSet.next();
                    return new Person(resultSet.getLong("ID"), resultSet.getString("NAME"));
                });

        assertNotNull(person);
        assertEquals(1L, person.id());
        assertEquals("myName", person.name());

        person = jdbcClient.sql("select * from person where id = ?")
                .statementSetter(stmt -> stmt.setLong(1, 1))
                .query()
                .as(resultSet -> {
                    resultSet.next();
                    return new Person(resultSet.getLong("ID"), resultSet.getString("NAME"));
                });

        assertNotNull(person);
        assertEquals(1L, person.id());
        assertEquals("myName", person.name());
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getJdbcClients")
    @DisplayName("testTransaction")
    void testTransaction(final DbServerExtension dbServerExtension, final JdbcClient jdbcClient) throws Exception {
        final List<String> names = List.of("name1", "name2", "name3");

        final Callable<Integer> insertCallable = () -> jdbcClient.sql("insert into person (name) values (?)").executeUpdateBatch(2, names, (ps, name) -> ps.setString(1, name));

        Transaction transaction = null;

        try {
            transaction = jdbcClient.createTransaction();
            transaction.begin();

            final int affectedRows = ScopedValue.callWhere(JdbcClient.TRANSACTION, transaction, insertCallable);
            assertEquals(names.size(), affectedRows);

            // Out of TransactionScope ->  Blocking for Derby and HSQLDB.
            if (EmbeddedDatabaseType.H2.equals(dbServerExtension.getDatabaseType())) {
                final List<Map<String, Object>> result = new JdbcClient(jdbcClient.getDataSource()).sql("select * from person").query().asListOfMaps();
                assertNotNull(result);
                assertEquals(0, result.size());
            }

            transaction.commit();
        }
        catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
        }

        final List<Map<String, Object>> result = jdbcClient.sql("select * from person")
                .query()
                .asListOfMaps();

        assertNotNull(result);
        assertEquals(names.size(), result.size());
    }

}
