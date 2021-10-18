// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;
import de.freese.base.persistence.jdbc.reactive.ResultSetIterator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForAll;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * TestCase des eigenen {@link SimpleJdbcTemplate}.
 *
 * @author Thomas Freese
 */
// @TestMethodOrder(MethodOrderer.MethodName.class)
class TestSimpleJdbcTemplate
{
    /**
     *
     */
    private static SimpleJdbcTemplate jdbcTemplate;
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSimpleJdbcTemplate.class);

    /**
     * close() findet in {@link DbServerExtension#afterAll(org.junit.jupiter.api.extension.ExtensionContext)} statt.
     */
    @RegisterExtension
    static final DbServerExtension SERVER = new DbServerExtension(EmbeddedDatabaseType.H2);

    /**
     *
     */
    @AfterEach
    void afterEach()
    {
        // Delete Db passiert in hsqldb-schema.sql.
    }

    /**
     *
     */
    @BeforeEach
    void beforeEach()
    {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db-schema.sql"));
        populator.addScript(new ClassPathResource("db-data.sql"));
        populator.execute(SERVER.getDataSource());

        jdbcTemplate = new SimpleJdbcTemplate(SERVER.getDataSource());
    }

    /**
     * @param sequence String
     *
     * @return long
     *
     * @throws SQLException Falls was schief geht.
     */
    private long getNextID(final String sequence) throws SQLException
    {
        String sql = "call next value for " + sequence;
        long id = 0;

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            rs.next();
            id = rs.getLong(1);
        }

        // long id = jdbcTemplate.query("call next value for kontakt_seq", rs -> {
        // rs.next();
        //
        // return rs.getLong(1);
        // });

        return id;
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testInsert() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO PERSON (id, name, vorname)");
        sql.append(" VALUES");
        sql.append(" (next value for person_seq, 'Freese', 'Thomas')");

        int affectedRows = jdbcTemplate.update(sql.toString());

        assertEquals(1, affectedRows);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testInsertBatch() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO PERSON (id, name, vorname)");
        sql.append(" VALUES");
        sql.append(" (?, ?, ?)");

        List<Person> personen = new ArrayList<>();
        personen.add(new Person(0, "Nachname3", "Vorname3"));
        personen.add(new Person(0, "Nachname4", "Vorname5"));

        int[] affectedRows = jdbcTemplate.updateBatch(sql.toString(), personen, (ps, person) -> {
            long id = getNextID("PERSON_SEQ");
            ps.setLong(1, id);
            ps.setString(2, person.getNachname());
            ps.setString(3, person.getVorname());
        });

        assertEquals(2, affectedRows.length);
        assertEquals(1, affectedRows[0]);
        assertEquals(1, affectedRows[1]);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testInsertPrepared() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO PERSON (id, name, vorname)");
        sql.append(" VALUES");
        sql.append(" (?, ?, ?)");

        // long id = jdbcTemplate.query("call next value for kontakt_seq", rs -> {
        // rs.next();
        //
        // return rs.getLong(1);
        // });
        long id = getNextID("PERSON_SEQ");

        assertEquals(3L, id);

        int affectedRows = jdbcTemplate.update(sql.toString(), ps -> {
            ps.setLong(1, id);
            ps.setString(2, "Nachname3");
            ps.setString(3, "Vorname3");
        });

        assertEquals(1, affectedRows);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsFlux() throws Exception
    {
        jdbcTemplate.setFetchSize(1);

        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.queryAsFlux("select * from PERSON order by id asc", new PersonRowMapper());

        AtomicInteger counter = new AtomicInteger(0);
        Flux<Person> flux = supplier.get();
        assertEquals(2, flux.doOnNext(p -> counter.incrementAndGet()).count().block());
        assertEquals(2, counter.get());

        flux = supplier.get();
        Person p = flux.take(1).blockFirst();
        assertEquals(1, p.getId());
        assertEquals("Nachname1", p.getNachname());
        assertEquals("Vorname1", p.getVorname());

        flux = supplier.get();
        p = flux.skip(1).take(1).blockFirst();
        assertEquals(2, p.getId());
        assertEquals("Nachname2", p.getNachname());
        assertEquals("Vorname2", p.getVorname());
    }

    /**
     *
     */
    @Test
    void testQueryAsFluxParallel()
    {
        jdbcTemplate.setFetchSize(1);

        Flux<Person> flux = jdbcTemplate.queryAsFlux("select * from PERSON order by id asc", new PersonRowMapper());

        // @formatter:off
        flux.parallel()
            .runOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
            .subscribe(p -> LOGGER.info("FluxParallel: {}, {}", Thread.currentThread().getName(), p));
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsFluxPreparedParam() throws Exception
    {
        jdbcTemplate.setFetchSize(1);

        Supplier<Flux<Person>> supplier =
                () -> jdbcTemplate.queryAsFlux("select * from PERSON where name like ? order by name desc", new PersonRowMapper(), "Nachname%");

        Flux<Person> flux = supplier.get();
        assertEquals(2, flux.count().block());

        flux = supplier.get();
        Person p = flux.take(1).blockFirst();
        assertEquals(2, p.getId());
        assertEquals("Nachname2", p.getNachname());
        assertEquals("Vorname2", p.getVorname());

        flux = supplier.get();
        p = flux.skip(1).take(1).blockFirst();
        assertEquals(1, p.getId());
        assertEquals("Nachname1", p.getNachname());
        assertEquals("Vorname1", p.getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsFluxPreparedSetter() throws Exception
    {
        jdbcTemplate.setFetchSize(1);

        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.queryAsFlux("select * from PERSON where name like ? order by name desc", new PersonRowMapper(),
                ps -> ps.setString(1, "Nachname%"));

        Flux<Person> flux = supplier.get();
        assertEquals(2, flux.count().block());

        flux = supplier.get();
        Person p = flux.take(1).blockFirst();
        assertEquals(2, p.getId());
        assertEquals("Nachname2", p.getNachname());
        assertEquals("Vorname2", p.getVorname());

        flux = supplier.get();
        p = flux.skip(1).take(1).blockFirst();
        assertEquals(1, p.getId());
        assertEquals("Nachname1", p.getNachname());
        assertEquals("Vorname1", p.getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsMap() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, vorname from PERSON");

        List<Map<String, Object>> results = jdbcTemplate.query(sql.toString());

        assertNotNull(results);
        assertEquals(2, results.size());

        // ColumnNames
        Set<String> columnNames = results.get(0).keySet();
        Iterator<String> nameIterator = columnNames.iterator();

        assertEquals("ID", nameIterator.next());
        assertEquals("NAME", nameIterator.next());
        assertEquals("VORNAME", nameIterator.next());

        // Values
        Map<String, Object> map = results.get(0);

        assertEquals("1", map.get("ID").toString());
        assertEquals("Nachname1", map.get("NAME"));
        assertEquals("Vorname1", map.get("VORNAME"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsMapPreparedParam() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, vorname from PERSON where name like ? order by name desc");

        List<Map<String, Object>> results = jdbcTemplate.query(sql.toString(), new ColumnMapRowMapper(), "Nachname%");

        assertNotNull(results);
        assertEquals(2, results.size());

        // ColumnNames
        Set<String> columnNames = results.get(0).keySet();
        Iterator<String> nameIterator = columnNames.iterator();

        assertEquals("ID", nameIterator.next());
        assertEquals("NAME", nameIterator.next());
        assertEquals("VORNAME", nameIterator.next());

        // Values
        Map<String, Object> map = results.get(0);
        assertEquals("2", map.get("ID").toString());
        assertEquals("Nachname2", map.get("NAME"));
        assertEquals("Vorname2", map.get("VORNAME"));

        map = results.get(1);
        assertEquals("1", map.get("ID").toString());
        assertEquals("Nachname1", map.get("NAME"));
        assertEquals("Vorname1", map.get("VORNAME"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsMapPreparedSetter() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, vorname from PERSON where name like ? order by name desc");

        List<Map<String, Object>> results = jdbcTemplate.query(sql.toString(), new ColumnMapRowMapper(), ps -> ps.setString(1, "Nachname%"));

        assertNotNull(results);
        assertEquals(2, results.size());

        // ColumnNames
        Set<String> columnNames = results.get(0).keySet();
        Iterator<String> nameIterator = columnNames.iterator();

        assertEquals("ID", nameIterator.next());
        assertEquals("NAME", nameIterator.next());
        assertEquals("VORNAME", nameIterator.next());

        // Values
        Map<String, Object> map = results.get(0);
        assertEquals("2", map.get("ID").toString());
        assertEquals("Nachname2", map.get("NAME"));
        assertEquals("Vorname2", map.get("VORNAME"));

        map = results.get(1);
        assertEquals("1", map.get("ID").toString());
        assertEquals("Nachname1", map.get("NAME"));
        assertEquals("Vorname1", map.get("VORNAME"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsPreparedParam() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, vorname from PERSON where name like ? order by name desc");

        List<Person> results = jdbcTemplate.query(sql.toString(), new PersonRowMapper(), "Nachname%");

        assertNotNull(results);
        assertEquals(2, results.size());

        assertEquals(2, results.get(0).getId());
        assertEquals("Nachname2", results.get(0).getNachname());
        assertEquals("Vorname2", results.get(0).getVorname());

        assertEquals(1, results.get(1).getId());
        assertEquals("Nachname1", results.get(1).getNachname());
        assertEquals("Vorname1", results.get(1).getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsPreparedSetter() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, vorname from PERSON where name like ? order by name desc");

        List<Person> results = jdbcTemplate.query(sql.toString(), new PersonRowMapper(), ps -> ps.setString(1, "Nachname%"));

        assertNotNull(results);
        assertEquals(2, results.size());

        assertEquals(2, results.get(0).getId());
        assertEquals("Nachname2", results.get(0).getNachname());
        assertEquals("Vorname2", results.get(0).getVorname());

        assertEquals(1, results.get(1).getId());
        assertEquals("Nachname1", results.get(1).getNachname());
        assertEquals("Vorname1", results.get(1).getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsPublisherPreparedParam() throws Exception
    {
        jdbcTemplate.setFetchSize(1);

        Publisher<Person> publisher =
                jdbcTemplate.queryAsPublisher("select * from PERSON where name like ? order by name desc", new PersonRowMapper(), "Nachname%");

        List<Person> result = new ArrayList<>();
        publisher.subscribe(new ResultSetSubscriberForAll<>(result::add));

        assertEquals(2, result.size());

        Person p = result.get(0);
        assertEquals(2, p.getId());
        assertEquals("Nachname2", p.getNachname());
        assertEquals("Vorname2", p.getVorname());

        p = result.get(1);
        assertEquals(1, p.getId());
        assertEquals("Nachname1", p.getNachname());
        assertEquals("Vorname1", p.getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsPublisherPreparedSetter() throws Exception
    {
        jdbcTemplate.setFetchSize(1);

        Publisher<Person> publisher = jdbcTemplate.queryAsPublisher("select * from PERSON where name like ? order by name desc", new PersonRowMapper(),
                ps -> ps.setString(1, "Nachname%"));

        List<Person> result = new ArrayList<>();
        publisher.subscribe(new ResultSetSubscriberForAll<>(result::add));

        assertEquals(2, result.size());

        Person p = result.get(0);
        assertEquals(2, p.getId());
        assertEquals("Nachname2", p.getNachname());
        assertEquals("Vorname2", p.getVorname());

        p = result.get(1);
        assertEquals(1, p.getId());
        assertEquals("Nachname1", p.getNachname());
        assertEquals("Vorname1", p.getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsStream() throws Exception
    {
        jdbcTemplate.setFetchSize(1);

        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON order by id asc", new PersonRowMapper());

        AtomicInteger counter = new AtomicInteger(0);

        try (Stream<Person> stream = supplier.get())
        {
            assertEquals(2, stream.peek(p -> counter.incrementAndGet()).count());
        }

        assertEquals(2, counter.get());

        try (Stream<Person> stream = supplier.get())
        {
            stream.limit(1).forEach(p -> {
                assertEquals(1, p.getId());
                assertEquals("Nachname1", p.getNachname());
                assertEquals("Vorname1", p.getVorname());
            });
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.skip(1).limit(1).forEach(p -> {
                assertEquals(2, p.getId());
                assertEquals("Nachname2", p.getNachname());
                assertEquals("Vorname2", p.getVorname());
            });
        }
    }

    /**
     * Die Methoden im {@link ResultSetIterator} müssen bei {@link Stream#parallel()} synchronisiert werden.
     *
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsStreamParallel() throws Exception
    {
        jdbcTemplate.setFetchSize(1);

        // RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON order by id asc", new PersonRowMapper());

        try (Stream<Person> stream = supplier.get())
        {
            // Thread.sleep(1000);

            // @formatter:off
            stream
                .parallel()
                .forEach(p -> LOGGER.info("StreamParallel: {}, {}", Thread.currentThread().getName(), p));
            // @formatter:on
        }
        // });

        // Throwable cause = exception.getCause();
        //
        // while (!(cause instanceof SQLException))
        // {
        // cause = cause.getCause();
        // }
        //
        // assertEquals(
        // "invalid cursor state: identifier cursor not positioned on row in UPDATE, DELETE, SET, or GET statement: ; ResultSet is positioned after last row",
        // cause.getMessage());

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsStreamPreparedParam() throws Exception
    {
        jdbcTemplate.setFetchSize(1);

        Supplier<Stream<Person>> supplier =
                () -> jdbcTemplate.queryAsStream("select * from PERSON where name like ? order by name desc", new PersonRowMapper(), "Nachname%");

        try (Stream<Person> stream = supplier.get())
        {
            assertEquals(2, stream.count());
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.limit(1).forEach(p -> {
                assertEquals(2, p.getId());
                assertEquals("Nachname2", p.getNachname());
                assertEquals("Vorname2", p.getVorname());
            });
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.skip(1).limit(1).forEach(p -> {
                assertEquals(1, p.getId());
                assertEquals("Nachname1", p.getNachname());
                assertEquals("Vorname1", p.getVorname());
            });
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryAsStreamPreparedSetter() throws Exception
    {
        jdbcTemplate.setFetchSize(1);

        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON where name like ? order by name desc", new PersonRowMapper(),
                ps -> ps.setString(1, "Nachname%"));

        try (Stream<Person> stream = supplier.get())
        {
            assertEquals(2, stream.count());
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.limit(1).forEach(p -> {
                assertEquals(2, p.getId());
                assertEquals("Nachname2", p.getNachname());
                assertEquals("Vorname2", p.getVorname());
            });
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.skip(1).limit(1).forEach(p -> {
                assertEquals(1, p.getId());
                assertEquals("Nachname1", p.getNachname());
                assertEquals("Vorname1", p.getVorname());
            });
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testQueryWithMaxRows() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON order by name desc");

        int maxRows = jdbcTemplate.getMaxRows();
        jdbcTemplate.setMaxRows(1);

        final List<Person> results;

        try
        {
            results = jdbcTemplate.query(sql.toString(), new PersonRowMapper());
        }
        finally
        {
            jdbcTemplate.setMaxRows(maxRows);
        }

        assertNotNull(results);
        assertEquals(1, results.size());

        assertEquals(2, results.get(0).getId());
        assertEquals("Nachname2", results.get(0).getNachname());
        assertEquals("Vorname2", results.get(0).getVorname());
    }
}
