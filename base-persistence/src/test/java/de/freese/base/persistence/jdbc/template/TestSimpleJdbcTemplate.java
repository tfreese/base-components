// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import reactor.core.Disposable;
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

        int affectedRows = jdbcTemplate.update(sql).execute();

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

        int[] affectedRows = jdbcTemplate.update(sql).executeBatch(personen, (ps, person) -> {
            long id = getNextID("PERSON_SEQ");
            ps.setLong(1, id);
            ps.setString(2, person.getLastName());
            ps.setString(3, person.getFirstName());
        }, 5);

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

        // long id = jdbcTemplate.extract("call next value for kontakt_seq", rs -> {
        // rs.next();
        //
        // return rs.getLong(1);
        // });
        long id = getNextID("PERSON_SEQ");

        assertEquals(3L, id);

        int affectedRows = jdbcTemplate.update(sql).preparedStatementSetter(ps -> {
            ps.setLong(1, id);
            ps.setString(2, "LastName3");
            ps.setString(3, "FirstName3");
        }).execute();

        assertEquals(1, affectedRows);
    }

    @Test
    void testQueryAsConsumable() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON order by LAST_NAME desc");

        List<Person> results = new ArrayList<>();

        jdbcTemplate.select(sql).extract(new RowMapperConsumableResultSetExtractor<>(new PersonRowMapper(), results::add));

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
    void testQueryAsFlux() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON order by ID asc");

        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).flux(new PersonRowMapper());

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
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON order by ID asc");

        Flux<Person> flux = jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).flux(new PersonRowMapper());

        // @formatter:off
        Disposable disposable = flux.parallel()
            .runOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
            .subscribe(p -> LOGGER.debug("FluxParallel: {}, {}", Thread.currentThread().getName(), p));
        // @formatter:on

        assertNotNull(disposable);
    }

    @Test
    void testQueryAsFluxPreparedParam() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).param("LastName%").flux(new PersonRowMapper());

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
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).preparedStatementSetter(ps -> ps.setString(1, "LastName%")).flux(new PersonRowMapper());

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
    void testQueryAsList() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON order by LAST_NAME desc");

        List<Person> results = jdbcTemplate.select(sql).list(new PersonRowMapper());

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
    void testQueryAsListPreparedParam() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        List<Person> results = jdbcTemplate.select(sql).param("LastName%").list(new PersonRowMapper());

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
    void testQueryAsListPreparedSetter() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        List<Person> results = jdbcTemplate.select(sql).preparedStatementSetter(ps -> ps.setString(1, "LastName%")).list(new PersonRowMapper());

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
    void testQueryAsMap() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON");

        List<Map<String, Object>> results = jdbcTemplate.select(sql).list();

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
    void testQueryAsPublisherPreparedParam() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        List<Person> result = new ArrayList<>();

        List<Flow.Subscriber<Person>> subscribers = List.of(new ResultSetSubscriberForAll<>(result::add), new ResultSetSubscriberForEachObject<>(result::add), new ResultSetSubscriberForFetchSize<>(result::add, 2));

        for (Flow.Subscriber<Person> subscriber : subscribers) {
            result.clear();

            Publisher<Person> publisher = jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).param("LastName%").publisher(new PersonRowMapper());
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
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        List<Person> result = new ArrayList<>();

        List<Flow.Subscriber<Person>> subscribers = List.of(new ResultSetSubscriberForAll<>(result::add), new ResultSetSubscriberForEachObject<>(result::add), new ResultSetSubscriberForFetchSize<>(result::add, 2));

        for (Flow.Subscriber<Person> subscriber : subscribers) {
            result.clear();

            Publisher<Person> publisher = jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).preparedStatementSetter(ps -> ps.setString(1, "LastName%")).publisher(new PersonRowMapper());
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
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON order by id asc");

        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).stream(new PersonRowMapper());

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
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON order by id asc");

        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).stream(new PersonRowMapper());

        try (Stream<Person> stream = supplier.get()) {
            assertNotNull(stream);

            // @formatter:off
            stream
                .parallel()
                .forEach(p -> LOGGER.debug("StreamParallel: {}, {}", Thread.currentThread().getName(), p));
            // @formatter:on
        }
    }

    @Test
    void testQueryAsStreamPreparedParam() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).param("LastName%").stream(new PersonRowMapper());

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
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON where LAST_NAME like ? order by LAST_NAME desc");

        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setFetchSize(1)).preparedStatementSetter(ps -> ps.setString(1, "LastName%")).stream(new PersonRowMapper());

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
    void testQueryListWithMaxRows() throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from PERSON order by LAST_NAME desc");

        List<Person> results = jdbcTemplate.select(sql).statementConfigurer(stmt -> stmt.setMaxRows(1)).list(new PersonRowMapper());

        assertNotNull(results);
        assertEquals(1, results.size());

        assertEquals(2, results.get(0).getId());
        assertEquals("LastName2", results.get(0).getLastName());
        assertEquals("FirstName2", results.get(0).getFirstName());
    }

    @Test
    void testTransaction() throws Exception {
        if (jdbcTemplate.isSpringManaged()) {
            return;
        }

        StringBuilder sqlInsert = new StringBuilder();
        sqlInsert.append("INSERT INTO PERSON (ID, LAST_NAME, FIRST_NAME)");
        sqlInsert.append(" VALUES");
        sqlInsert.append(" (next value for person_seq, ?, ?)");

        StringBuilder sqlSelect = new StringBuilder();
        sqlSelect.append("select * from PERSON order by LAST_NAME desc");

        List<Person> results = jdbcTemplate.select(sqlSelect).list(new PersonRowMapper());
        assertNotNull(results);
        assertEquals(2, results.size());

        jdbcTemplate.beginTransaction();

        Exception exception = assertThrows(SQLException.class, () -> jdbcTemplate.beginTransaction());
        assertEquals("Transaction already started", exception.getMessage());

        int affectedRows = jdbcTemplate.update(sqlInsert).param("a").param("b").execute();
        assertEquals(1, affectedRows);

        results = jdbcTemplate.select(sqlSelect).list(new PersonRowMapper());
        assertNotNull(results);
        assertEquals(3, results.size());

        jdbcTemplate.commitTransaction();

        jdbcTemplate.beginTransaction();
        affectedRows = jdbcTemplate.update(sqlInsert).param("x").param("y").execute();
        assertEquals(1, affectedRows);

        results = jdbcTemplate.select(sqlSelect).list(new PersonRowMapper());
        assertNotNull(results);
        assertEquals(4, results.size());

        jdbcTemplate.rollbackTransaction();

        results = jdbcTemplate.select(sqlSelect).list(new PersonRowMapper());
        assertNotNull(results);
        assertEquals(3, results.size());
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

        // long id = jdbcTemplate.extract("call next value for kontakt_seq", rs -> {
        // rs.next();
        //
        // return rs.getLong(1);
        // });

        return id;
    }
}
