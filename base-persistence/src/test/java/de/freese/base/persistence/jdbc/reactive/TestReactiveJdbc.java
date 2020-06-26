/**
 * Created: 09.04.2019
 */

package de.freese.base.persistence.jdbc.reactive;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
import de.freese.base.persistence.jdbc.template.function.RowMapper;
import de.freese.base.utils.JdbcUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestReactiveJdbc
{
    /**
     *
     */
    private static DataSource dataSource;

    /**
     *
     */
    @AfterAll
    static void afterClass()
    {
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
        dataSource = database;

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("hsqldb-schema.sql"));
        populator.addScript(new ClassPathResource("hsqldb-data.sql"));
        populator.execute(dataSource);
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
     * @return {@link DataSource}
     */
    static DataSource getDataSource()
    {
        return dataSource;
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    @Test
    void flux() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveParallel.flux()");

        Connection connection = getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        RowMapper<Person> rowMapper = new PersonRowMapper();

        Flux<Person> flux = Flux.fromIterable(new ResultSetIterable<>(resultSet, rowMapper)).doFinally(state -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        });

        flux.subscribe(p -> System.out.printf("%s: %s%n", Thread.currentThread().getName(), p));

        assertTrue(true);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    @Test
    void fluxParallel() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveParallel.fluxParallel()");

        Connection connection = getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        RowMapper<Person> rowMapper = new PersonRowMapper();

        Flux<Person> flux = Flux.fromIterable(new ResultSetIterable<>(resultSet, rowMapper)).doFinally(state -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        });

        // @formatter:off
        flux.parallel()
            .runOn(Schedulers.fromExecutor(Executors.newCachedThreadPool()))
            .subscribe(p -> System.out.printf("%s: %s%n", Thread.currentThread().getName(), p));
        // @formatter:on

        assertTrue(true);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    @Test
    void streamIterable() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveParallel.streamIterable()");

        Connection connection = getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        RowMapper<Person> rowMapper = new PersonRowMapper();

        try (Stream<Person> stream = StreamSupport.stream(new ResultSetIterable<>(resultSet, rowMapper).spliterator(), false).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.forEach(p -> System.out.printf("%s: %s%n", Thread.currentThread().getName(), p));
        }

        assertTrue(true);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    @Test
    void streamIterableParallel() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveParallel.streamIterableParallel()");

        Connection connection = getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        RowMapper<Person> rowMapper = new PersonRowMapper();

        try (Stream<Person> stream = StreamSupport.stream(new ResultSetIterable<>(resultSet, rowMapper).spliterator(), true).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.parallel().forEach(p -> System.out.printf("%s: %s%n", Thread.currentThread().getName(), p));
        }

        assertTrue(true);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    @Test
    void streamIterator() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveParallel.streamIterator()");

        Connection connection = getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        RowMapper<Person> rowMapper = new PersonRowMapper();

        int characteristics = Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL;
        Spliterator<Person> spliterator = Spliterators.spliteratorUnknownSize(new ResultSetIterator<>(resultSet, rowMapper), characteristics);

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.forEach(p -> System.out.printf("%s: %s%n", Thread.currentThread().getName(), p));
        }

        assertTrue(true);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    @Test
    void streamIteratorParallel() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveParallel.streamIteratorParallel()");

        Connection connection = getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        RowMapper<Person> rowMapper = new PersonRowMapper();

        int characteristics = Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL;
        Spliterator<Person> spliterator = Spliterators.spliteratorUnknownSize(new ResultSetIterator<>(resultSet, rowMapper), characteristics);

        try (Stream<Person> stream = StreamSupport.stream(spliterator, true).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.parallel().forEach(p -> System.out.printf("%s: %s%n", Thread.currentThread().getName(), p));
        }

        assertTrue(true);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    @Test
    void streamSpliterator() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveParallel.streamSpliterator()");

        Connection connection = getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        RowMapper<Person> rowMapper = new PersonRowMapper();

        Spliterator<Person> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.forEach(p -> System.out.printf("%s: %s%n", Thread.currentThread().getName(), p));
        }

        assertTrue(true);
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    @Test
    void streamSpliteratorParallel() throws SQLException
    {
        System.out.println();
        System.out.println("TestReactiveParallel.streamResultSetSpliteratorParallel()");

        Connection connection = getDataSource().getConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        ResultSet resultSet = statement.executeQuery("select * from PERSON order by id asc");

        RowMapper<Person> rowMapper = new PersonRowMapper();

        Spliterator<Person> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

        try (Stream<Person> stream = StreamSupport.stream(spliterator, true).onClose(() -> {
            System.out.println("close jdbc stream");
            close(connection, statement, resultSet);
        }))
        {
            stream.parallel().forEach(p -> System.out.printf("%s: %s%n", Thread.currentThread().getName(), p));
        }

        assertTrue(true);
    }
}
