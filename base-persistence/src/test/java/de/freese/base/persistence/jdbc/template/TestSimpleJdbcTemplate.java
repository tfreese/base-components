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
import java.util.concurrent.Flow;
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
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;
import de.freese.base.persistence.jdbc.reactive.ResultSetIterator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForAll;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForFetchSize;

/**
 * @author Thomas Freese
 */
class TestSimpleJdbcTemplate {
    /**
     * close() is called in {@link DbServerExtension#afterAll(org.junit.jupiter.api.extension.ExtensionContext)}.
     */
    @RegisterExtension
    static final DbServerExtension SERVER = new DbServerExtension(EmbeddedDatabaseType.H2);

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSimpleJdbcTemplate.class);

    private static SimpleJdbcTemplate jdbcTemplate;

    @AfterEach
    void afterEach() {
        // Delete Db happens in hsqldb-schema.sql.
    }

    @BeforeEach
    void beforeEach() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db-schema.sql"));
        populator.addScript(new ClassPathResource("db-data.sql"));
        populator.execute(SERVER.getDataSource());

        jdbcTemplate = new SimpleJdbcTemplate(SERVER.getDataSource());
    }

    @Test
    void testInsert() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO PERSON (ID, LAST_NAME, FIRST_NAME)");
        sql.append(" VALUES");
        sql.append(" (next value for person_seq, 'Freese', 'Thomas')");

        int affectedRows = jdbcTemplate.update(sql.toString());

        assertEquals(1, affectedRows);
    }

    @Test
    void testInsertBatch() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO PERSON (ID, LAST_NAME, FIRST_NAME)");
        sql.append(" VALUES");
        sql.append(" (?, ?, ?)");

        List<Person> personen = new ArrayList<>();
        personen.add(new Person(0, "LastName3", "FirstName3"));
        personen.add(new Person(0, "Nachname4", "Vorname5"));

        int[] affectedRows = jdbcTemplate.updateBatch(sql.toString(), personen, (ps, person) -> {
            long id = getNextID("PERSON_SEQ");
            ps.setLong(1, id);
            ps.setString(2, person.getLastName());
            ps.setString(3, person.getFirstName());
        });

        assertEquals(2, affectedRows.length);
        assertEquals(1, affectedRows[0]);
        assertEquals(1, affectedRows[1]);
    }

    @Test
    void testInsertPrepared() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO PERSON (ID, LAST_NAME, FIRST_NAME)");
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
            ps.setString(2, "LastName3");
            ps.setString(3, "FirstName3");
        });

        assertEquals(1, affectedRows);
    }

    @Test
    void testQueryAsFlux() throws Exception {
        jdbcTemplate.setFetchSize(1);

        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.queryAsFlux("select * from PERSON order by ID asc", new PersonRowMapper());

        AtomicInteger counter = new AtomicInteger(0);
        Flux<Person> flux = supplier.get();
        assertEquals(2, flux.doOnNext(p -> counter.incrementAndGet()).count().block());
        assertEquals(2, counter.get());

        flux = supplier.get();
        Person p = flux.take(1).blockFirst();
        assertNotNull(p);
        assertEquals(1, p.getId());
        assertEquals("LastName1", p.getLastName());
        assertEquals("FirstName1", p.getFirstName());

        flux = supplier.get();
        p = flux.skip(1).take(1).blockFirst();
        assertNotNull(p);
        assertEquals(2, p.getId());
        assertEquals("LastName2", p.getLastName());
        assertEquals("FirstName2", p.getFirstName());
    }

    @Test
    void testQueryAsFluxParallel() {
        jdbcTemplate.setFetchSize(1);

        Flux<Person> flux = jdbcTemplate.queryAsFlux("select * from PERSON order by ID asc", new PersonRowMapper());

        // @formatter:off
        flux.parallel()
            .runOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
            .subscribe(p -> LOGGER.debug("FluxParallel: {}, {}", Thread.currentThread().getName(), p));
        // @formatter:on

        assertTrue(true);
    }

    @Test
    void testQueryAsFluxPreparedParam() throws Exception {
        jdbcTemplate.setFetchSize(1);

        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.queryAsFlux("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc", new PersonRowMapper(), "LastName%");

        Flux<Person> flux = supplier.get();
        assertEquals(2, flux.count().block());

        flux = supplier.get();
        Person p = flux.take(1).blockFirst();
        assertNotNull(p);
        assertEquals(2, p.getId());
        assertEquals("LastName2", p.getLastName());
        assertEquals("FirstName2", p.getFirstName());

        flux = supplier.get();
        p = flux.skip(1).take(1).blockFirst();
        assertNotNull(p);
        assertEquals(1, p.getId());
        assertEquals("LastName1", p.getLastName());
        assertEquals("FirstName1", p.getFirstName());
    }

    @Test
    void testQueryAsFluxPreparedSetter() throws Exception {
        jdbcTemplate.setFetchSize(1);

        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.queryAsFlux("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc", new PersonRowMapper(), ps -> ps.setString(1, "LastName%"));

        Flux<Person> flux = supplier.get();
        assertEquals(2, flux.count().block());

        flux = supplier.get();
        Person p = flux.take(1).blockFirst();
        assertNotNull(p);
        assertEquals(2, p.getId());
        assertEquals("LastName2", p.getLastName());
        assertEquals("FirstName2", p.getFirstName());

        flux = supplier.get();
        p = flux.skip(1).take(1).blockFirst();
        assertNotNull(p);
        assertEquals(1, p.getId());
        assertEquals("LastName1", p.getLastName());
        assertEquals("FirstName1", p.getFirstName());
    }

    @Test
    void testQueryAsMap() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON");

        List<Map<String, Object>> results = jdbcTemplate.query(sql.toString());

        assertNotNull(results);
        assertEquals(2, results.size());

        // ColumnNames
        Set<String> columnNames = results.get(0).keySet();
        Iterator<String> nameIterator = columnNames.iterator();

        assertEquals("ID", nameIterator.next());
        assertEquals("LAST_NAME", nameIterator.next());
        assertEquals("FIRST_NAME", nameIterator.next());

        // Values
        Map<String, Object> map = results.get(0);

        assertEquals("1", map.get("ID").toString());
        assertEquals("LastName1", map.get("LAST_NAME"));
        assertEquals("FirstName1", map.get("FIRST_NAME"));
    }

    @Test
    void testQueryAsMapPreparedParam() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        List<Map<String, Object>> results = jdbcTemplate.query(sql.toString(), new ColumnMapRowMapper(), "LastName%");

        assertNotNull(results);
        assertEquals(2, results.size());

        // ColumnNames
        Set<String> columnNames = results.get(0).keySet();
        Iterator<String> nameIterator = columnNames.iterator();

        assertEquals("ID", nameIterator.next());
        assertEquals("LAST_NAME", nameIterator.next());
        assertEquals("FIRST_NAME", nameIterator.next());

        // Values
        Map<String, Object> map = results.get(0);
        assertEquals("2", map.get("ID").toString());
        assertEquals("LastName2", map.get("LAST_NAME"));
        assertEquals("FirstName2", map.get("FIRST_NAME"));

        map = results.get(1);
        assertEquals("1", map.get("ID").toString());
        assertEquals("LastName1", map.get("LAST_NAME"));
        assertEquals("FirstName1", map.get("FIRST_NAME"));
    }

    @Test
    void testQueryAsMapPreparedSetter() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        List<Map<String, Object>> results = jdbcTemplate.query(sql.toString(), new ColumnMapRowMapper(), ps -> ps.setString(1, "LastName%"));

        assertNotNull(results);
        assertEquals(2, results.size());

        // ColumnNames
        Set<String> columnNames = results.get(0).keySet();
        Iterator<String> nameIterator = columnNames.iterator();

        assertEquals("ID", nameIterator.next());
        assertEquals("LAST_NAME", nameIterator.next());
        assertEquals("FIRST_NAME", nameIterator.next());

        // Values
        Map<String, Object> map = results.get(0);
        assertEquals("2", map.get("ID").toString());
        assertEquals("LastName2", map.get("LAST_NAME"));
        assertEquals("FirstName2", map.get("FIRST_NAME"));

        map = results.get(1);
        assertEquals("1", map.get("ID").toString());
        assertEquals("LastName1", map.get("LAST_NAME"));
        assertEquals("FirstName1", map.get("FIRST_NAME"));
    }

    @Test
    void testQueryAsPreparedParam() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        List<Person> results = jdbcTemplate.query(sql.toString(), new PersonRowMapper(), "LastName%");

        assertNotNull(results);
        assertEquals(2, results.size());

        assertEquals(2, results.get(0).getId());
        assertEquals("LastName2", results.get(0).getLastName());
        assertEquals("FirstName2", results.get(0).getFirstName());

        assertEquals(1, results.get(1).getId());
        assertEquals("LastName1", results.get(1).getLastName());
        assertEquals("FirstName1", results.get(1).getFirstName());
    }

    @Test
    void testQueryAsPreparedSetter() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        List<Person> results = jdbcTemplate.query(sql.toString(), new PersonRowMapper(), ps -> ps.setString(1, "LastName%"));

        assertNotNull(results);
        assertEquals(2, results.size());

        assertEquals(2, results.get(0).getId());
        assertEquals("LastName2", results.get(0).getLastName());
        assertEquals("FirstName2", results.get(0).getFirstName());

        assertEquals(1, results.get(1).getId());
        assertEquals("LastName1", results.get(1).getLastName());
        assertEquals("FirstName1", results.get(1).getFirstName());
    }

    @Test
    void testQueryAsPublisherPreparedParam() throws Exception {
        jdbcTemplate.setFetchSize(1);

        List<Person> result = new ArrayList<>();

        List<Flow.Subscriber<Person>> subscribers = List.of(new ResultSetSubscriberForAll<>(result::add), new ResultSetSubscriberForEachObject<>(result::add), new ResultSetSubscriberForFetchSize<>(result::add, 2));

        for (Flow.Subscriber<Person> subscriber : subscribers) {
            result.clear();

            Publisher<Person> publisher = jdbcTemplate.queryAsPublisher("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc", new PersonRowMapper(), "LastName%");
            publisher.subscribe(subscriber);

            assertEquals(2, result.size());

            Person p = result.get(0);
            assertEquals(2, p.getId());
            assertEquals("LastName2", p.getLastName());
            assertEquals("FirstName2", p.getFirstName());

            p = result.get(1);
            assertEquals(1, p.getId());
            assertEquals("LastName1", p.getLastName());
            assertEquals("FirstName1", p.getFirstName());
        }
    }

    @Test
    void testQueryAsPublisherPreparedSetter() throws Exception {
        jdbcTemplate.setFetchSize(1);

        List<Person> result = new ArrayList<>();

        List<Flow.Subscriber<Person>> subscribers = List.of(new ResultSetSubscriberForAll<>(result::add), new ResultSetSubscriberForEachObject<>(result::add), new ResultSetSubscriberForFetchSize<>(result::add, 2));

        for (Flow.Subscriber<Person> subscriber : subscribers) {
            result.clear();

            Publisher<Person> publisher = jdbcTemplate.queryAsPublisher("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc", new PersonRowMapper(), ps -> ps.setString(1, "LastName%"));
            publisher.subscribe(subscriber);

            assertEquals(2, result.size());

            Person p = result.get(0);
            assertEquals(2, p.getId());
            assertEquals("LastName2", p.getLastName());
            assertEquals("FirstName2", p.getFirstName());

            p = result.get(1);
            assertEquals(1, p.getId());
            assertEquals("LastName1", p.getLastName());
            assertEquals("FirstName1", p.getFirstName());
        }
    }

    @Test
    void testQueryAsStream() throws Exception {
        jdbcTemplate.setFetchSize(1);

        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON order by id asc", new PersonRowMapper());

        AtomicInteger counter = new AtomicInteger(0);

        try (Stream<Person> stream = supplier.get()) {
            assertEquals(2, stream.peek(p -> counter.incrementAndGet()).count());
        }

        assertEquals(2, counter.get());

        try (Stream<Person> stream = supplier.get()) {
            stream.limit(1).forEach(p -> {
                assertEquals(1, p.getId());
                assertEquals("LastName1", p.getLastName());
                assertEquals("FirstName1", p.getFirstName());
            });
        }

        try (Stream<Person> stream = supplier.get()) {
            stream.skip(1).limit(1).forEach(p -> {
                assertEquals(2, p.getId());
                assertEquals("LastName2", p.getLastName());
                assertEquals("FirstName2", p.getFirstName());
            });
        }
    }

    /**
     * The Methods in {@link ResultSetIterator} must be synchronized, when {@link Stream#parallel()} is used.
     */
    @Test
    void testQueryAsStreamParallel() throws Exception {
        jdbcTemplate.setFetchSize(1);

        // RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON order by id asc", new PersonRowMapper());

        try (Stream<Person> stream = supplier.get()) {
            // TimeUnit.MILLISECONDS.sleep(1000);

            // @formatter:off
            stream
                .parallel()
                .forEach(p -> LOGGER.debug("StreamParallel: {}, {}", Thread.currentThread().getName(), p));
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

    @Test
    void testQueryAsStreamPreparedParam() throws Exception {
        jdbcTemplate.setFetchSize(1);

        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc", new PersonRowMapper(), "LastName%");

        try (Stream<Person> stream = supplier.get()) {
            assertEquals(2, stream.count());
        }

        try (Stream<Person> stream = supplier.get()) {
            stream.limit(1).forEach(p -> {
                assertEquals(2, p.getId());
                assertEquals("LastName2", p.getLastName());
                assertEquals("FirstName2", p.getFirstName());
            });
        }

        try (Stream<Person> stream = supplier.get()) {
            stream.skip(1).limit(1).forEach(p -> {
                assertEquals(1, p.getId());
                assertEquals("LastName1", p.getLastName());
                assertEquals("FirstName1", p.getFirstName());
            });
        }
    }

    @Test
    void testQueryAsStreamPreparedSetter() throws Exception {
        jdbcTemplate.setFetchSize(1);

        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc", new PersonRowMapper(), ps -> ps.setString(1, "LastName%"));

        try (Stream<Person> stream = supplier.get()) {
            assertEquals(2, stream.count());
        }

        try (Stream<Person> stream = supplier.get()) {
            stream.limit(1).forEach(p -> {
                assertEquals(2, p.getId());
                assertEquals("LastName2", p.getLastName());
                assertEquals("FirstName2", p.getFirstName());
            });
        }

        try (Stream<Person> stream = supplier.get()) {
            stream.skip(1).limit(1).forEach(p -> {
                assertEquals(1, p.getId());
                assertEquals("LastName1", p.getLastName());
                assertEquals("FirstName1", p.getFirstName());
            });
        }
    }

    @Test
    void testQueryWithMaxRows() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON order by LAST_NAME desc");

        int maxRows = jdbcTemplate.getMaxRows();
        jdbcTemplate.setMaxRows(1);

        final List<Person> results;

        try {
            results = jdbcTemplate.query(sql.toString(), new PersonRowMapper());
        }
        finally {
            jdbcTemplate.setMaxRows(maxRows);
        }

        assertNotNull(results);
        assertEquals(1, results.size());

        assertEquals(2, results.get(0).getId());
        assertEquals("LastName2", results.get(0).getLastName());
        assertEquals("FirstName2", results.get(0).getFirstName());
    }

    private long getNextID(final String sequence) throws SQLException {
        String sql = "call next value for " + sequence;
        long id = 0;

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
}
