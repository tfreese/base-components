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
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;
import de.freese.base.persistence.jdbc.TestSuiteJdbc;
import de.freese.base.persistence.jdbc.reactive.ResultSetIterator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForList;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * TestCase des eigenen {@link SimpleJdbcTemplate}.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestSimpleJdbcTemplate
{
    /**
     *
     */
    private static SimpleJdbcTemplate jdbcTemplate = null;

    /**
     *
     */
    @AfterAll
    static void afterClass()
    {
        DataSource dataSource = jdbcTemplate.getDataSource();

        if (dataSource instanceof SingleConnectionDataSource)
        {
            ((SingleConnectionDataSource) dataSource).destroy();
        }
        else if (dataSource instanceof EmbeddedDatabase)
        {
            ((EmbeddedDatabase) dataSource).shutdown();
        }
    }

    /**
     *
     */
    @BeforeAll
    static void beforeClass()
    {
        EmbeddedDatabase database =
                new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).setName("" + TestSuiteJdbc.ATOMIC_INTEGER.getAndIncrement()).build();

        // SingleConnectionDataSource singleConnectionDataSource = new SingleConnectionDataSource();
        // singleConnectionDataSource.setDriverClassName("org.generic.jdbc.JDBCDriver");
        // singleConnectionDataSource.setUrl("jdbc:generic:mem:" + TestSuiteJdbc.ATOMIC_INTEGER.getAndIncrement());
        // // singleConnectionDataSource.setUrl("jdbc:generic:file:db/generic/generic;create=false;shutdown=true");
        // singleConnectionDataSource.setSuppressClose(true);
        // singleConnectionDataSource.setAutoCommit(true);

        // DataSource dataSource = singleConnectionDataSource;
        DataSource dataSource = database;

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("hsqldb-schema.sql"));
        // populator.addScript(new ClassPathResource("generic-data.sql"));
        populator.execute(dataSource);

        jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        jdbcTemplate.setFetchSize(1);
    }

    /**
     * @param sequence String
     * @return long
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

        return id;
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test010Insert() throws Exception
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
    void test011InsertBatch() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO PERSON (id, name, vorname)");
        sql.append(" VALUES");
        sql.append(" (?, ?, ?)");

        List<Person> personen = new ArrayList<>();
        personen.add(new Person(0, "Nachname1", "Vorname1"));
        personen.add(new Person(0, "Nachname2", "Vorname2"));

        int[] affectedRows = jdbcTemplate.updateBatch(sql.toString(), (ps, person) -> {
            long id = getNextID("PERSON_SEQ");
            ps.setLong(1, id);
            ps.setString(2, person.getNachname());
            ps.setString(3, person.getVorname());
        }, personen);

        assertEquals(2, affectedRows.length);
        assertEquals(1, affectedRows[0]);
        assertEquals(1, affectedRows[1]);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test020QueryAsMap() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, vorname from PERSON");

        List<Map<String, Object>> results = jdbcTemplate.query(sql.toString());

        assertNotNull(results);
        assertEquals(3, results.size());

        // ColumnNames
        Set<String> columnNames = results.get(0).keySet();
        Iterator<String> nameIterator = columnNames.iterator();

        assertEquals("ID", nameIterator.next());
        assertEquals("NAME", nameIterator.next());
        assertEquals("VORNAME", nameIterator.next());

        // Values
        Map<String, Object> map = results.get(0);

        assertEquals("1", map.get("ID").toString());
        assertEquals("Freese", map.get("NAME"));
        assertEquals("Thomas", map.get("VORNAME"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test021QueryAsMapPreparedSetter() throws Exception
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
        assertEquals("3", map.get("ID").toString());
        assertEquals("Nachname2", map.get("NAME"));
        assertEquals("Vorname2", map.get("VORNAME"));

        map = results.get(1);
        assertEquals("2", map.get("ID").toString());
        assertEquals("Nachname1", map.get("NAME"));
        assertEquals("Vorname1", map.get("VORNAME"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test022QueryAsMapPreparedParam() throws Exception
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
        assertEquals("3", map.get("ID").toString());
        assertEquals("Nachname2", map.get("NAME"));
        assertEquals("Vorname2", map.get("VORNAME"));

        map = results.get(1);
        assertEquals("2", map.get("ID").toString());
        assertEquals("Nachname1", map.get("NAME"));
        assertEquals("Vorname1", map.get("VORNAME"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test030QueryAsPreparedSetter() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, vorname from PERSON where name like ? order by name desc");

        List<Person> results = jdbcTemplate.query(sql.toString(), new PersonRowMapper(), ps -> ps.setString(1, "Nachname%"));

        assertNotNull(results);
        assertEquals(2, results.size());

        assertEquals(3, results.get(0).getId());
        assertEquals("Nachname2", results.get(0).getNachname());
        assertEquals("Vorname2", results.get(0).getVorname());

        assertEquals(2, results.get(1).getId());
        assertEquals("Nachname1", results.get(1).getNachname());
        assertEquals("Vorname1", results.get(1).getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test031QueryAsPreparedParam() throws Exception
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select id, name, vorname from PERSON where name like ? order by name desc");

        List<Person> results = jdbcTemplate.query(sql.toString(), new PersonRowMapper(), "Nachname%");

        assertNotNull(results);
        assertEquals(2, results.size());

        assertEquals(3, results.get(0).getId());
        assertEquals("Nachname2", results.get(0).getNachname());
        assertEquals("Vorname2", results.get(0).getVorname());

        assertEquals(2, results.get(1).getId());
        assertEquals("Nachname1", results.get(1).getNachname());
        assertEquals("Vorname1", results.get(1).getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test040QueryAsStream() throws Exception
    {
        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON order by id asc", new PersonRowMapper());

        AtomicInteger counter = new AtomicInteger(0);

        try (Stream<Person> stream = supplier.get())
        {
            assertEquals(3, stream.peek(p -> counter.incrementAndGet()).count());
        }

        assertEquals(3, counter.get());

        try (Stream<Person> stream = supplier.get())
        {
            stream.limit(1).forEach(p -> {
                assertEquals(1, p.getId());
                assertEquals("Freese", p.getNachname());
                assertEquals("Thomas", p.getVorname());
            });
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.skip(1).limit(1).forEach(p -> {
                assertEquals(2, p.getId());
                assertEquals("Nachname1", p.getNachname());
                assertEquals("Vorname1", p.getVorname());
            });
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.skip(2).limit(1).forEach(p -> {
                assertEquals(3, p.getId());
                assertEquals("Nachname2", p.getNachname());
                assertEquals("Vorname2", p.getVorname());
            });
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test041QueryAsStreamPreparedSetter() throws Exception
    {
        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON where name like ? order by name desc", new PersonRowMapper(),
                ps -> ps.setString(1, "Nachname%"));

        try (Stream<Person> stream = supplier.get())
        {
            assertEquals(2, stream.count());
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.limit(1).forEach(p -> {
                assertEquals(3, p.getId());
                assertEquals("Nachname2", p.getNachname());
                assertEquals("Vorname2", p.getVorname());
            });
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.skip(1).limit(1).forEach(p -> {
                assertEquals(2, p.getId());
                assertEquals("Nachname1", p.getNachname());
                assertEquals("Vorname1", p.getVorname());
            });
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test042QueryAsStreamPreparedParam() throws Exception
    {
        Supplier<Stream<Person>> supplier =
                () -> jdbcTemplate.queryAsStream("select * from PERSON where name like ? order by name desc", new PersonRowMapper(), "Nachname%");

        try (Stream<Person> stream = supplier.get())
        {
            assertEquals(2, stream.count());
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.limit(1).forEach(p -> {
                assertEquals(3, p.getId());
                assertEquals("Nachname2", p.getNachname());
                assertEquals("Vorname2", p.getVorname());
            });
        }

        try (Stream<Person> stream = supplier.get())
        {
            stream.skip(1).limit(1).forEach(p -> {
                assertEquals(2, p.getId());
                assertEquals("Nachname1", p.getNachname());
                assertEquals("Vorname1", p.getVorname());
            });
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test043QueryAsStreamForListPreparedSetter() throws Exception
    {
        Supplier<Stream<Map<String, Object>>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON where name like ? order by name desc",
                new ColumnMapRowMapper(), ps -> ps.setString(1, "Nachname%"));

        try (Stream<Map<String, Object>> stream = supplier.get())
        {
            assertEquals(2, stream.count());
        }

        try (Stream<Map<String, Object>> stream = supplier.get())
        {
            stream.forEach(m -> {
                // ColumnNames
                Set<String> columnNames = m.keySet();
                Iterator<String> nameIterator = columnNames.iterator();

                assertEquals("ID", nameIterator.next());
                assertEquals("NAME", nameIterator.next());
                assertEquals("VORNAME", nameIterator.next());
            });
        }

        try (Stream<Map<String, Object>> stream = supplier.get())
        {
            stream.limit(1).forEach(m -> {
                assertEquals("3", m.get("ID").toString());
                assertEquals("Nachname2", m.get("NAME"));
                assertEquals("Vorname2", m.get("VORNAME"));
            });
        }

        try (Stream<Map<String, Object>> stream = supplier.get())
        {
            stream.skip(1).limit(1).forEach(m -> {
                assertEquals("2", m.get("ID").toString());
                assertEquals("Nachname1", m.get("NAME"));
                assertEquals("Vorname1", m.get("VORNAME"));
            });
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test044QueryAsStreamForListPreparedParam() throws Exception
    {
        Supplier<Stream<Map<String, Object>>> supplier =
                () -> jdbcTemplate.queryAsStream("select * from PERSON where name like ? order by name desc", new ColumnMapRowMapper(), "Nachname%");

        try (Stream<Map<String, Object>> stream = supplier.get())
        {
            assertEquals(2, stream.count());
        }

        try (Stream<Map<String, Object>> stream = supplier.get())
        {
            stream.forEach(m -> {
                // ColumnNames
                Set<String> columnNames = m.keySet();
                Iterator<String> nameIterator = columnNames.iterator();

                assertEquals("ID", nameIterator.next());
                assertEquals("NAME", nameIterator.next());
                assertEquals("VORNAME", nameIterator.next());
            });
        }

        try (Stream<Map<String, Object>> stream = supplier.get())
        {
            stream.limit(1).forEach(m -> {
                assertEquals("3", m.get("ID").toString());
                assertEquals("Nachname2", m.get("NAME"));
                assertEquals("Vorname2", m.get("VORNAME"));
            });
        }

        try (Stream<Map<String, Object>> stream = supplier.get())
        {
            stream.skip(1).limit(1).forEach(m -> {
                assertEquals("2", m.get("ID").toString());
                assertEquals("Nachname1", m.get("NAME"));
                assertEquals("Vorname1", m.get("VORNAME"));
            });
        }
    }

    /**
     * Die Methoden im {@link ResultSetIterator} müssen bei {@link Stream#parallel()} synchronisiert werden.
     *
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test045QueryAsStreamParallel() throws Exception
    {
        // RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        Supplier<Stream<Person>> supplier = () -> jdbcTemplate.queryAsStream("select * from PERSON order by id asc", new PersonRowMapper());

        try (Stream<Person> stream = supplier.get())
        {
            // Thread.sleep(1000);

            // @formatter:off
            stream
                .parallel()
                .forEach(p -> System.out.printf("StreamParallel: %s, %s%n", Thread.currentThread().getName(), p));
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
    void test050QueryAsFlux() throws Exception
    {
        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.queryAsFlux("select * from PERSON order by id asc", new PersonRowMapper());

        AtomicInteger counter = new AtomicInteger(0);
        Flux<Person> flux = supplier.get();
        assertEquals(3, flux.doOnNext(p -> counter.incrementAndGet()).count().block().longValue());
        assertEquals(3, counter.get());

        flux = supplier.get();
        flux.take(1).subscribe(p -> {
            assertEquals(1, p.getId());
            assertEquals("Freese", p.getNachname());
            assertEquals("Thomas", p.getVorname());
        });

        flux = supplier.get();
        flux.skip(1).take(1).subscribe(p -> {
            assertEquals(2, p.getId());
            assertEquals("Nachname1", p.getNachname());
            assertEquals("Vorname1", p.getVorname());
        });

        flux = supplier.get();
        flux.skip(2).take(1).subscribe(p -> {
            assertEquals(3, p.getId());
            assertEquals("Nachname2", p.getNachname());
            assertEquals("Vorname2", p.getVorname());
        });
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test051QueryAsFluxPreparedSetter() throws Exception
    {
        Supplier<Flux<Person>> supplier = () -> jdbcTemplate.queryAsFlux("select * from PERSON where name like ? order by name desc", new PersonRowMapper(),
                ps -> ps.setString(1, "Nachname%"));

        Flux<Person> flux = supplier.get();
        assertEquals(2, flux.count().block().longValue());

        flux = supplier.get();
        flux.take(1).subscribe(p -> {
            assertEquals(3, p.getId());
            assertEquals("Nachname2", p.getNachname());
            assertEquals("Vorname2", p.getVorname());
        });

        flux = supplier.get();
        flux.skip(1).take(1).subscribe(p -> {

            assertEquals(2, p.getId());
            assertEquals("Nachname1", p.getNachname());
            assertEquals("Vorname1", p.getVorname());
        });
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test052QueryAsFluxPreparedParam() throws Exception
    {
        Supplier<Flux<Person>> supplier =
                () -> jdbcTemplate.queryAsFlux("select * from PERSON where name like ? order by name desc", new PersonRowMapper(), "Nachname%");

        Flux<Person> flux = supplier.get();
        assertEquals(2, flux.count().block().longValue());

        flux = supplier.get();
        flux.take(1).subscribe(p -> {
            assertEquals(3, p.getId());
            assertEquals("Nachname2", p.getNachname());
            assertEquals("Vorname2", p.getVorname());
        });

        flux = supplier.get();
        flux.skip(1).take(1).subscribe(p -> {

            assertEquals(2, p.getId());
            assertEquals("Nachname1", p.getNachname());
            assertEquals("Vorname1", p.getVorname());
        });
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test053QueryAsFluxForListPreparedSetter() throws Exception
    {
        Supplier<Flux<Map<String, Object>>> supplier = () -> jdbcTemplate.queryAsFlux("select * from PERSON where name like ? order by name desc",
                new ColumnMapRowMapper(), ps -> ps.setString(1, "Nachname%"));

        Flux<Map<String, Object>> flux = supplier.get();
        assertEquals(2, flux.count().block().longValue());

        flux = supplier.get();
        flux.subscribe(m -> {
            // ColumnNames
            Set<String> columnNames = m.keySet();
            Iterator<String> nameIterator = columnNames.iterator();

            assertEquals("ID", nameIterator.next());
            assertEquals("NAME", nameIterator.next());
            assertEquals("VORNAME", nameIterator.next());
        });

        flux = supplier.get();
        flux.take(1).subscribe(m -> {
            assertEquals("3", m.get("ID").toString());
            assertEquals("Nachname2", m.get("NAME"));
            assertEquals("Vorname2", m.get("VORNAME"));
        });

        flux = supplier.get();
        flux.skip(1).take(1).subscribe(m -> {
            assertEquals("2", m.get("ID").toString());
            assertEquals("Nachname1", m.get("NAME"));
            assertEquals("Vorname1", m.get("VORNAME"));
        });
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test054QueryAsFluxForListPreparedParam() throws Exception
    {
        Supplier<Flux<Map<String, Object>>> supplier =
                () -> jdbcTemplate.queryAsFlux("select * from PERSON where name like ? order by name desc", new ColumnMapRowMapper(), "Nachname%");

        Flux<Map<String, Object>> flux = supplier.get();
        assertEquals(2, flux.count().block().longValue());

        flux = supplier.get();
        flux.subscribe(m -> {
            // ColumnNames
            Set<String> columnNames = m.keySet();
            Iterator<String> nameIterator = columnNames.iterator();

            assertEquals("ID", nameIterator.next());
            assertEquals("NAME", nameIterator.next());
            assertEquals("VORNAME", nameIterator.next());
        });

        flux = supplier.get();
        flux.take(1).subscribe(m -> {
            assertEquals("3", m.get("ID").toString());
            assertEquals("Nachname2", m.get("NAME"));
            assertEquals("Vorname2", m.get("VORNAME"));
        });

        flux = supplier.get();
        flux.skip(1).take(1).subscribe(m -> {
            assertEquals("2", m.get("ID").toString());
            assertEquals("Nachname1", m.get("NAME"));
            assertEquals("Vorname1", m.get("VORNAME"));
        });
    }

    /**
     *
     */
    @Test
    void test055QueryAsFluxParallel()
    {
        Flux<Person> flux = jdbcTemplate.queryAsFlux("select * from PERSON order by id asc", new PersonRowMapper());

        // @formatter:off
        flux.parallel()
            .runOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
            .subscribe(p -> System.out.printf("FluxParallel: %s, %s%n", Thread.currentThread().getName(), p));
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test061QueryAsPublisherPreparedSetter() throws Exception
    {
        Publisher<Person> publisher = jdbcTemplate.queryAsPublisher("select * from PERSON where name like ? order by name desc", new PersonRowMapper(),
                ps -> ps.setString(1, "Nachname%"));

        ResultSetSubscriberForList<Person> toListSubscriber = new ResultSetSubscriberForList<>();
        publisher.subscribe(toListSubscriber);

        List<Person> result = toListSubscriber.getRows();

        assertEquals(2, result.size());

        Person p = result.get(0);
        assertEquals(3, p.getId());
        assertEquals("Nachname2", p.getNachname());
        assertEquals("Vorname2", p.getVorname());

        p = result.get(1);
        assertEquals(2, p.getId());
        assertEquals("Nachname1", p.getNachname());
        assertEquals("Vorname1", p.getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test062QueryAsPublisherPreparedParam() throws Exception
    {
        Publisher<Person> publisher =
                jdbcTemplate.queryAsPublisher("select * from PERSON where name like ? order by name desc", new PersonRowMapper(), "Nachname%");

        ResultSetSubscriberForList<Person> toListSubscriber = new ResultSetSubscriberForList<>();
        publisher.subscribe(toListSubscriber);

        List<Person> result = toListSubscriber.getRows();

        assertEquals(2, result.size());

        Person p = result.get(0);
        assertEquals(3, p.getId());
        assertEquals("Nachname2", p.getNachname());
        assertEquals("Vorname2", p.getVorname());

        p = result.get(1);
        assertEquals(2, p.getId());
        assertEquals("Nachname1", p.getNachname());
        assertEquals("Vorname1", p.getVorname());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test063QueryAsPublisherForListPreparedSetter() throws Exception
    {
        Publisher<Map<String, Object>> publisher = jdbcTemplate.queryAsPublisher("select * from PERSON where name like ? order by name desc",
                new ColumnMapRowMapper(), ps -> ps.setString(1, "Nachname%"));

        ResultSetSubscriberForList<Map<String, Object>> toListSubscriber = new ResultSetSubscriberForList<>();
        publisher.subscribe(toListSubscriber);

        List<Map<String, Object>> result = toListSubscriber.getRows();

        assertEquals(2, result.size());

        Map<String, Object> m = result.get(0);
        // ColumnNames
        Set<String> columnNames = m.keySet();
        Iterator<String> nameIterator = columnNames.iterator();

        assertEquals("ID", nameIterator.next());
        assertEquals("NAME", nameIterator.next());
        assertEquals("VORNAME", nameIterator.next());

        assertEquals("3", m.get("ID").toString());
        assertEquals("Nachname2", m.get("NAME"));
        assertEquals("Vorname2", m.get("VORNAME"));

        m = result.get(1);
        assertEquals("2", m.get("ID").toString());
        assertEquals("Nachname1", m.get("NAME"));
        assertEquals("Vorname1", m.get("VORNAME"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test064QueryAsPublisherForListPreparedParam() throws Exception
    {
        Publisher<Map<String, Object>> publisher =
                jdbcTemplate.queryAsPublisher("select * from PERSON where name like ? order by name desc", new ColumnMapRowMapper(), "Nachname%");

        ResultSetSubscriberForList<Map<String, Object>> toListSubscriber = new ResultSetSubscriberForList<>();
        publisher.subscribe(toListSubscriber);

        List<Map<String, Object>> result = toListSubscriber.getRows();

        assertEquals(2, result.size());

        Map<String, Object> m = result.get(0);
        // ColumnNames
        Set<String> columnNames = m.keySet();
        Iterator<String> nameIterator = columnNames.iterator();

        assertEquals("ID", nameIterator.next());
        assertEquals("NAME", nameIterator.next());
        assertEquals("VORNAME", nameIterator.next());

        assertEquals("3", m.get("ID").toString());
        assertEquals("Nachname2", m.get("NAME"));
        assertEquals("Vorname2", m.get("VORNAME"));

        m = result.get(1);
        assertEquals("2", m.get("ID").toString());
        assertEquals("Nachname1", m.get("NAME"));
        assertEquals("Vorname1", m.get("VORNAME"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void test070InsertPrepared() throws Exception
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

        assertEquals(4L, id);

        int affectedRows = jdbcTemplate.update(sql.toString(), ps -> {
            ps.setLong(1, id);
            ps.setString(2, "Freesee");
            ps.setString(3, "Thomass");
        });

        assertEquals(1, affectedRows);
    }
}
