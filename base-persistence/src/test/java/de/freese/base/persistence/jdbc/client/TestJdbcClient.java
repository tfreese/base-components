// Created: 12.11.23
package de.freese.base.persistence.jdbc.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
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
    @DisplayName("testDelete")
    void testDelete(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final JdbcClient jdbcClient = new JdbcClient(server.getDataSource());
        jdbcClient.execute(createTableSql(databaseType));

        final List<String> names = List.of("name1", "name2", "name3");

        int affectedRows = jdbcClient.insert("insert into person (name) values (?)").executeBatch(names, (ps, name) -> ps.setString(1, name), 2);
        assertEquals(names.size(), affectedRows);

        affectedRows = jdbcClient.delete("delete from person where id = ?").statementSetter(ps -> ps.setLong(1, 3)).execute();
        assertEquals(1, affectedRows);

        final List<Map<String, Object>> result = jdbcClient.select("select * from person order by name asc").executeAsMap();
        assertNotNull(result);
        assertEquals(names.size() - 1, result.size());

        for (int i = 0; i < names.size() - 1; i++) {
            assertEquals(2, result.get(i).size());
            assertEquals(i + 1L, result.get(i).get("ID"));
            assertEquals("name" + (i + 1), result.get(i).get("NAME"));
        }

        jdbcClient.execute("drop table person");
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
    @DisplayName("testInsertWithKeyConsumer")
    void testInsertWithKeyConsumer(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final JdbcClient jdbcClient = new JdbcClient(server.getDataSource());
        jdbcClient.execute(createTableSql(databaseType));

        final List<Long> keys = new ArrayList<>();
        final int affectedRows = jdbcClient.insert("insert into person (name) values (?)")
                .statementSetter(ps -> ps.setString(1, "name1"))
                .execute(keys::add);
        assertEquals(1, affectedRows);
        assertEquals(1L, keys.get(0));

        final List<Map<String, Object>> result = jdbcClient.select("select * from person").executeAsMap();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).size());
        assertEquals(1L, result.get(0).get("ID"));
        assertEquals("name1", result.get(0).get("NAME"));

        jdbcClient.execute("drop table person");
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

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testUpdate")
    void testUpdate(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final JdbcClient jdbcClient = new JdbcClient(server.getDataSource());
        jdbcClient.execute(createTableSql(databaseType));

        final List<Long> keys = new ArrayList<>();
        int affectedRows = jdbcClient.insert("insert into person (name) values (?)")
                .statementSetter(ps -> ps.setString(1, "name1"))
                .execute(keys::add);
        assertEquals(1, affectedRows);
        assertEquals(1L, keys.get(0));

        affectedRows = jdbcClient.update("update person set name = ? where id = ?")
                .statementSetter(ps -> {
                    ps.setString(1, "name11");
                    ps.setLong(2, 1L);
                })
                .execute();
        assertEquals(1, affectedRows);

        final List<Map<String, Object>> result = jdbcClient.select("select * from person").executeAsMap();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).size());
        assertEquals(1L, result.get(0).get("ID"));
        assertEquals("name11", result.get(0).get("NAME"));

        jdbcClient.execute("drop table person");
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("getDatabases")
    @DisplayName("testUpdateBatch")
    void testUpdateBatch(final EmbeddedDatabaseType databaseType, final DbServerExtension server) {
        final JdbcClient jdbcClient = new JdbcClient(server.getDataSource());
        jdbcClient.execute(createTableSql(databaseType));

        final List<String> names = List.of("name1", "name2", "name3");

        int affectedRows = jdbcClient.insert("insert into person (name) values (?)").executeBatch(names, (ps, name) -> ps.setString(1, name), 2);
        assertEquals(names.size(), affectedRows);

        final Map<Long, String> map = Map.of(1L, "name11", 2L, "name22", 3L, "name33");
        affectedRows = jdbcClient.update("update person set name = ? where id = ?").executeBatch(map.entrySet(), (ps, entry) -> {
            ps.setString(1, entry.getValue());
            ps.setLong(2, entry.getKey());
        }, 2);
        assertEquals(map.size(), affectedRows);

        final List<Map<String, Object>> result = jdbcClient.select("select * from person order by name asc").executeAsMap();
        assertNotNull(result);
        assertEquals(names.size(), result.size());

        for (int i = 0; i < names.size(); i++) {
            assertEquals(2, result.get(i).size());
            assertEquals(i + 1L, result.get(i).get("ID"));
            assertEquals("name" + (i + 1) + (i + 1), result.get(i).get("NAME"));
        }

        jdbcClient.execute("drop table person");
    }

    private String createTableSql(final EmbeddedDatabaseType databaseType) {
        return switch (databaseType) {
            // "GENERATED ALWAYS AS IDENTITY" - Always provides auto-incremented sequence values. You are not allowed to specify your own values.
            // "GENERATED BY DEFAULT AS IDENTITY" - Provides auto-incremented sequence values only when you are not providing values.
            case DERBY, H2 -> """
                    CREATE TABLE person (
                        id BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
                    )
                    """;

            // Is always NOT NULL.
            case HSQL -> """
                    CREATE TABLE person (
                        id BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1) NOT NULL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL
                    )
                    """;
        };
    }
}
