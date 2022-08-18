// Created: 09.04.2019
package de.freese.base.persistence.jdbc.reactive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetPublisher;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForAll;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForFetchSize;
import de.freese.base.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestReactiveJdbc
{
    /**
     *
     */
    @RegisterExtension
    static final DbServerExtension SERVER = new DbServerExtension(EmbeddedDatabaseType.H2);
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestReactiveJdbc.class);

    /**
     *
     */
    @AfterAll
    static void afterAll()
    {
        Schedulers.shutdownNow();
    }

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db-schema.sql"));
        populator.addScript(new ClassPathResource("db-data.sql"));
        populator.execute(SERVER.getDataSource());
    }

    /**
     * @param connection {@link Connection}
     * @param statement {@link Statement}
     * @param resultSet {@link ResultSet}
     */
    static void close(final Connection connection, final Statement statement, final ResultSet resultSet)
    {
        LOGGER.debug("close");

        try
        {
            JdbcUtils.close(resultSet);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            JdbcUtils.close(statement);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            JdbcUtils.close(connection);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testFlowResultSetPublisher() throws SQLException
    {
        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Publisher<Person> publisher = new ResultSetPublisher<>(connection, statement, resultSet, new PersonRowMapper());

        List<Person> result = new ArrayList<>();
        publisher.subscribe(new ResultSetSubscriberForAll<>(result::add));

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testFlowResultSetPublisherForEachObject() throws SQLException
    {
        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Publisher<Person> publisher = new ResultSetPublisher<>(connection, statement, resultSet, new PersonRowMapper());

        List<Person> result = new ArrayList<>();
        publisher.subscribe(new ResultSetSubscriberForEachObject<>(result::add));

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testFlowResultSetPublisherForFetchSize() throws SQLException
    {
        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Publisher<Person> publisher = new ResultSetPublisher<>(connection, statement, resultSet, new PersonRowMapper());

        List<Person> result = new ArrayList<>();
        publisher.subscribe(new ResultSetSubscriberForFetchSize<>(result::add, 2));

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testFluxResultSetIterable() throws SQLException
    {
        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Iterable<Person> iterable = new ResultSetIterable<>(resultSet, new PersonRowMapper());

        Flux<Person> flux = Flux.fromIterable(iterable).doFinally(state ->
        {
            LOGGER.debug("close jdbc flux");
            close(connection, statement, resultSet);
        });

        flux.subscribe(p ->
        {
            result.add(p);
            LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
        });

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testFluxResultSetIterableParallel() throws SQLException
    {
        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Iterable<Person> iterable = new ResultSetIterable<>(resultSet, new PersonRowMapper());

        Flux<Person> flux = Flux.fromIterable(iterable).doFinally(state ->
        {
            LOGGER.debug("close jdbc flux");
            close(connection, statement, resultSet);
        });

        // @formatter:off
        flux.parallel()
                .runOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
                .doOnTerminate(() -> assertData(result))
                .subscribe(p -> {
                    result.add(p);
                    LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
                 })
            ;
        // @formatter:on
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testStreamResultSetIterable() throws SQLException
    {
        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Spliterator<Person> spliterator = new ResultSetIterable<>(resultSet, new PersonRowMapper()).spliterator();

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() ->
        {
            LOGGER.debug("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.forEach(p ->
            {
                result.add(p);
                LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testStreamResultSetIterableParallel() throws SQLException
    {
        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Spliterator<Person> spliterator = new ResultSetIterable<>(resultSet, new PersonRowMapper()).spliterator();

        try (Stream<Person> stream = StreamSupport.stream(spliterator, true).onClose(() ->
        {
            LOGGER.debug("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.parallel().forEach(p ->
            {
                result.add(p);
                LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testStreamResultSetIterator() throws SQLException
    {
        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Iterator<Person> iterator = new ResultSetIterator<>(resultSet, new PersonRowMapper());

        int characteristics = Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL;
        Spliterator<Person> spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() ->
        {
            LOGGER.debug("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.forEach(p ->
            {
                result.add(p);
                LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testStreamResultSetIteratorParallel() throws SQLException
    {
        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Iterator<Person> iterator = new ResultSetIterator<>(resultSet, new PersonRowMapper());

        int characteristics = Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL;
        Spliterator<Person> spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);

        try (Stream<Person> stream = StreamSupport.stream(spliterator, true).onClose(() ->
        {
            LOGGER.debug("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.parallel().forEach(p ->
            {
                result.add(p);
                LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testStreamResultSetSpliterator() throws SQLException
    {
        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Spliterator<Person> spliterator = new ResultSetSpliterator<>(resultSet, new PersonRowMapper());

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() ->
        {
            LOGGER.debug("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.forEach(p ->
            {
                result.add(p);
                LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schiefgeht.
     */
    @Test
    void testStreamResultSetSpliteratorParallel() throws SQLException
    {
        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Spliterator<Person> spliterator = new ResultSetSpliterator<>(resultSet, new PersonRowMapper());

        // Die Methode ResultSetSpliterator.trySplit liefert null.
        // Daher daher wird der Stream trotz StreamSupport.stream(spliterator, true) NICHT parallel sein.
        try (Stream<Person> stream = StreamSupport.stream(spliterator, true).onClose(() ->
        {
            LOGGER.debug("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.parallel().forEach(p ->
            {
                result.add(p);
                LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    /**
     * @param result {@link List}
     */
    private void assertData(final List<Person> result)
    {
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1, result.get(0).getId());
        assertEquals("Nachname1", result.get(0).getNachname());
        assertEquals("Vorname1", result.get(0).getVorname());

        assertEquals(2, result.get(1).getId());
        assertEquals("Nachname2", result.get(1).getNachname());
        assertEquals("Vorname2", result.get(1).getVorname());
    }
}
