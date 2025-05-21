// Created: 12.11.23
package de.freese.base.persistence.jdbc.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;

/**
 * @author Thomas Freese
 */
class TestJdbcRepository {
    @RegisterExtension
    static final DbServerExtension SERVER = new DbServerExtension(EmbeddedDatabaseType.H2, true);

    private static final class MyJdbcRepository extends AbstractJdbcRepository {
        private MyJdbcRepository(final DataSource dataSource) {
            super(dataSource);
        }
    }

    @BeforeAll
    static void beforeAll() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db-schema.sql"));
        populator.addScript(new ClassPathResource("db-data.sql"));
        populator.execute(SERVER.getDataSource());
    }

    @Test
    void testCall() {
        final MyJdbcRepository jdbcRepository = new MyJdbcRepository(SERVER.getDataSource());

        final double result = jdbcRepository.call("{? = call sin(?)}", stmt -> {
                    stmt.setDouble(2, 180D);
                    stmt.registerOutParameter(1, Types.DOUBLE);
                },
                stmt -> stmt.getDouble(1)
        );

        assertEquals(Math.sin(180D), result, 0.000_000_1D);
    }

    @Test
    void testExecuteUpdate() {
        final MyJdbcRepository jdbcRepository = new MyJdbcRepository(SERVER.getDataSource());
        // final List<Long> generateKeys = new ArrayList<>();

        // next value FOR person_seq,
        final int affectedRows = jdbcRepository.update("insert into person (id, name) values (next value FOR person_seq, 'myName1')");
        // final int affectedRows = jdbcRepository.update("insert into person (name) values ('myName1')", null, generateKeys::add);

        assertEquals(1, affectedRows);
        // assertEquals(1, generateKeys.size());
        // assertEquals(1L, generateKeys.getFirst());
    }

    @Test
    void testExecuteUpdatePrepared() {
        final MyJdbcRepository jdbcRepository = new MyJdbcRepository(SERVER.getDataSource());

        final int affectedRows = jdbcRepository.update("insert into person (id, name) values (next value FOR person_seq, ?)",
                stmt -> stmt.setString(1, "myName2"));

        assertEquals(1, affectedRows);
    }

    @Test
    void testOffsetDateTime() {
        final MyJdbcRepository jdbcRepository = new MyJdbcRepository(SERVER.getDataSource());

        // if not exists
        String sql = """
                create sequence
                MY_SEQ
                    start with 1 increment by 1
                """;
        jdbcRepository.execute(sql);

        sql = """
                CREATE TABLE events (
                    id BIGINT PRIMARY KEY,
                    event_time TIMESTAMP WITH TIME ZONE
                )
                """;
        jdbcRepository.execute(sql);

        // "call next value for MY_SEQ";
        // next value for MY_SEQ
        //
        // @Column(name = "event_time")
        // private OffsetDateTime eventTime;

        sql = """
                INSERT INTO
                events
                    (id, event_time)
                VALUES
                    (next value for MY_SEQ, ?)
                """;
        final int affectedRows = jdbcRepository.update(sql, stmt -> stmt.setObject(1, OffsetDateTime.now()));
        assertEquals(1, affectedRows);

        final List<Map<Long, OffsetDateTime>> result = jdbcRepository.query("select * from events",
                resultSet -> Map.of(resultSet.getLong("ID"), resultSet.getObject("EVENT_TIME", OffsetDateTime.class)));
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.getFirst().containsKey(1L));
        jdbcRepository.getLogger().info("{}", result.getFirst());
    }

    @Test
    void testQueryAsList() {
        final MyJdbcRepository jdbcRepository = new MyJdbcRepository(SERVER.getDataSource());

        final int affectedRows = jdbcRepository.update("insert into person (id, name) values (next value FOR person_seq, 'myName3')");

        assertEquals(1, affectedRows);

        List<Person> resultList = jdbcRepository.query("select * from person", new PersonRowMapper());

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertTrue(resultList.size() > 1);

        resultList = jdbcRepository.query("select * from person where id = ?", new PersonRowMapper(), stmt -> stmt.setLong(1, 1));

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(1, resultList.size());
    }
}
