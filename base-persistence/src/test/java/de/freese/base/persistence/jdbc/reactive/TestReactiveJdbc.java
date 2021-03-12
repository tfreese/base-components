/**
 * Created: 09.04.2019
 */

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetPublisher;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForAll;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForFetchSize;
import de.freese.base.utils.JdbcUtils;
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
    static final DbServerExtension SERVER = new DbServerExtension();

    /**
     *
     */
    @BeforeAll
    static void beforeClass()
    {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("hsqldb-schema.sql"));
        populator.addScript(new ClassPathResource("hsqldb-data.sql"));
        populator.execute(SERVER.getDataSource());
    }

    /**
     * @param connection {@link Connection}
     * @param statement {@link Statement}
     * @param resultSet {@link ResultSet}
     */
    static void close(final Connection connection, final Statement statement, final ResultSet resultSet)
    {
        System.out.println("TestReactiveParallel.close()");

        try
        {
            JdbcUtils.closeResultSet(resultSet);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            JdbcUtils.closeStatement(statement);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            JdbcUtils.closeConnection(connection);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * @param result {@link List}
     */
    private void assertData(final List<Person> result)
    {
        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(1, result.get(0).getId());
        assertEquals("Freese", result.get(0).getNachname());
        assertEquals("Thomas", result.get(0).getVorname());

        assertEquals(2, result.get(1).getId());
        assertEquals("Nachname1", result.get(1).getNachname());
        assertEquals("Vorname1", result.get(1).getVorname());

        assertEquals(3, result.get(2).getId());
        assertEquals("Nachname2", result.get(2).getNachname());
        assertEquals("Vorname2", result.get(2).getVorname());
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testFlowResultSetPublisher() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.subscriberForAll()");

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Publisher<Person> publisher = new ResultSetPublisher<>(connection, statement, resultSet, new PersonRowMapper());

        ResultSetSubscriberForAll<Person> subscriber = new ResultSetSubscriberForAll<>();
        publisher.subscribe(subscriber);

        assertData(subscriber.getData());
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testFlowResultSetPublisherForEachObject() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.subscriberForEachObject()");

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Publisher<Person> publisher = new ResultSetPublisher<>(connection, statement, resultSet, new PersonRowMapper());

        ResultSetSubscriberForEachObject<Person> subscriber = new ResultSetSubscriberForEachObject<>();
        publisher.subscribe(subscriber);

        assertData(subscriber.getData());
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testFlowResultSetPublisherForFetchSize() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.subscriberForFetchSize()");

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Publisher<Person> publisher = new ResultSetPublisher<>(connection, statement, resultSet, new PersonRowMapper());

        ResultSetSubscriberForFetchSize<Person> subscriber = new ResultSetSubscriberForFetchSize<>(ArrayList::new, 2);
        publisher.subscribe(subscriber);

        assertData(subscriber.getData());
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testFluxResultSetIterable() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.flux()");

        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Iterable<Person> iterable = new ResultSetIterable<>(resultSet, new PersonRowMapper());

        Flux<Person> flux = Flux.fromIterable(iterable).doFinally(state -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        });

        flux.subscribe(p -> {
            result.add(p);
            System.out.printf("%s: %s%n", Thread.currentThread().getName(), p);
        });

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testFluxResultSetIterableParallel() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.fluxParallel()");

        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Iterable<Person> iterable = new ResultSetIterable<>(resultSet, new PersonRowMapper());

        Flux<Person> flux = Flux.fromIterable(iterable).doFinally(state -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        });

        // @formatter:off
        flux.parallel()
            .runOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
            .subscribe(p -> {
                result.add(p);
                System.out.printf("%s: %s%n", Thread.currentThread().getName(), p);
             })
            ;
        // @formatter:on

        assertEquals(3, result.size());
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testStreamResultSetIterable() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.streamIterable()");

        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Spliterator<Person> spliterator = new ResultSetIterable<>(resultSet, new PersonRowMapper()).spliterator();

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.forEach(p -> {
                result.add(p);
                System.out.printf("%s: %s%n", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testStreamResultSetIterableParallel() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.streamIterableParallel()");

        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Spliterator<Person> spliterator = new ResultSetIterable<>(resultSet, new PersonRowMapper()).spliterator();

        try (Stream<Person> stream = StreamSupport.stream(spliterator, true).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.parallel().forEach(p -> {
                result.add(p);
                System.out.printf("%s: %s%n", Thread.currentThread().getName(), p);
            });
        }

        assertEquals(3, result.size());
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testStreamResultSetIterator() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.streamIterator()");

        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Iterator<Person> iterator = new ResultSetIterator<>(resultSet, new PersonRowMapper());

        int characteristics = Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL;
        Spliterator<Person> spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.forEach(p -> {
                result.add(p);
                System.out.printf("%s: %s%n", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testStreamResultSetIteratorParallel() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.streamIteratorParallel()");

        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Iterator<Person> iterator = new ResultSetIterator<>(resultSet, new PersonRowMapper());

        int characteristics = Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL;
        Spliterator<Person> spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);

        try (Stream<Person> stream = StreamSupport.stream(spliterator, true).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.parallel().forEach(p -> {
                result.add(p);
                System.out.printf("%s: %s%n", Thread.currentThread().getName(), p);
            });
        }

        assertEquals(3, result.size());
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testStreamResultSetSpliterator() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.streamSpliterator()");

        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Spliterator<Person> spliterator = new ResultSetSpliterator<>(resultSet, new PersonRowMapper());

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.forEach(p -> {
                result.add(p);
                System.out.printf("%s: %s%n", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @Test
    void testStreamResultSetSpliteratorParallel() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveJdbc.streamSpliteratorParallel()");

        List<Person> result = new ArrayList<>();

        Connection connection = SERVER.getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        Spliterator<Person> spliterator = new ResultSetSpliterator<>(resultSet, new PersonRowMapper());

        // Die Methode ResultSetSpliterator.trySplit liefert null.
        // Daher daher wird der Stream trotz StreamSupport.stream(spliterator, true) NICHT parallel sein.
        try (Stream<Person> stream = StreamSupport.stream(spliterator, true).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.parallel().forEach(p -> {
                result.add(p);
                System.out.printf("%s: %s%n", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }
}
