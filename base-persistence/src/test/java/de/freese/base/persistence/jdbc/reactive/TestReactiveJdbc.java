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
import java.util.concurrent.Flow.Publisher;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

import de.freese.base.persistence.jdbc.DbServerExtension;
import de.freese.base.persistence.jdbc.Person;
import de.freese.base.persistence.jdbc.PersonRowMapper;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetPublisher;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForAll;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForFetchSize;
import de.freese.base.utils.JdbcUtils;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestReactiveJdbc {
    @RegisterExtension
    static final DbServerExtension SERVER = new DbServerExtension(EmbeddedDatabaseType.H2, true);

    private static final Logger LOGGER = LoggerFactory.getLogger(TestReactiveJdbc.class);

    @AfterAll
    static void afterAll() {
        Schedulers.shutdownNow();
    }

    @BeforeAll
    static void beforeAll() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db-schema.sql"));
        populator.addScript(new ClassPathResource("db-data.sql"));
        populator.execute(SERVER.getDataSource());
    }

    @Test
    void testFlowResultSetPublisher() throws SQLException {
        final Connection connection = SERVER.getDataSource().getConnection();
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        final ResultSet resultSet = statement.executeQuery("select * from PERSON order by ID asc");

        final Consumer<ResultSet> doOnClose = rs -> {
            JdbcUtils.closeSilent(rs);
            JdbcUtils.closeSilent(statement);
            JdbcUtils.closeSilent(connection);
        };

        final Publisher<Person> publisher = new ResultSetPublisher<>(resultSet, new PersonRowMapper(), doOnClose);

        final List<Person> result = new ArrayList<>();
        publisher.subscribe(new ResultSetSubscriberForAll<>(result::add));

        assertData(result);
    }

    @Test
    void testFlowResultSetPublisherForEachObject() throws SQLException {
        final Connection connection = SERVER.getDataSource().getConnection();
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        final ResultSet resultSet = statement.executeQuery("select * from PERSON order by ID asc");

        final Consumer<ResultSet> doOnClose = rs -> {
            JdbcUtils.closeSilent(rs);
            JdbcUtils.closeSilent(statement);
            JdbcUtils.closeSilent(connection);
        };

        final Publisher<Person> publisher = new ResultSetPublisher<>(resultSet, new PersonRowMapper(), doOnClose);

        final List<Person> result = new ArrayList<>();
        publisher.subscribe(new ResultSetSubscriberForEachObject<>(result::add));

        assertData(result);
    }

    @Test
    void testFlowResultSetPublisherForFetchSize() throws SQLException {
        final Connection connection = SERVER.getDataSource().getConnection();
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10);
        final ResultSet resultSet = statement.executeQuery("select * from PERSON order by ID asc");

        final Consumer<ResultSet> doOnClose = rs -> {
            JdbcUtils.closeSilent(rs);
            JdbcUtils.closeSilent(statement);
            JdbcUtils.closeSilent(connection);
        };

        final Publisher<Person> publisher = new ResultSetPublisher<>(resultSet, new PersonRowMapper(), doOnClose);

        final List<Person> result = new ArrayList<>();
        publisher.subscribe(new ResultSetSubscriberForFetchSize<>(result::add, 2));

        assertData(result);
    }

    @Test
    void testFluxResultSetIterable() throws SQLException {
        final List<Person> result = new ArrayList<>();

        final Connection connection = SERVER.getDataSource().getConnection();
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        final ResultSet resultSet = statement.executeQuery("select * from PERSON order by ID asc");

        final Iterable<Person> iterable = new ResultSetIterable<>(resultSet, new PersonRowMapper());

        final Flux<Person> flux = Flux.fromIterable(iterable).doFinally(state -> {
            LOGGER.debug("close jdbc flux");
            JdbcUtils.closeSilent(resultSet);
            JdbcUtils.closeSilent(statement);
            JdbcUtils.closeSilent(connection);
        });

        flux.subscribe(p -> {
            result.add(p);
            LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
        });

        assertData(result);
    }

    @Test
    void testStreamResultSetIterable() throws SQLException {
        final List<Person> result = new ArrayList<>();

        final Connection connection = SERVER.getDataSource().getConnection();
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        final ResultSet resultSet = statement.executeQuery("select * from PERSON order by ID asc");

        final Spliterator<Person> spliterator = new ResultSetIterable<>(resultSet, new PersonRowMapper()).spliterator();

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() -> {
            LOGGER.debug("close jdbc stream");
            JdbcUtils.closeSilent(resultSet);
            JdbcUtils.closeSilent(statement);
            JdbcUtils.closeSilent(connection);
        })) {
            stream.forEach(p -> {
                result.add(p);
                LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    @Test
    void testStreamResultSetIterator() throws SQLException {
        final List<Person> result = new ArrayList<>();

        final Connection connection = SERVER.getDataSource().getConnection();
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        final ResultSet resultSet = statement.executeQuery("select * from PERSON order by ID asc");

        final Iterator<Person> iterator = new ResultSetIterator<>(resultSet, new PersonRowMapper());

        final int characteristics = Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL;
        final Spliterator<Person> spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() -> {
            LOGGER.debug("close jdbc stream");
            JdbcUtils.closeSilent(resultSet);
            JdbcUtils.closeSilent(statement);
            JdbcUtils.closeSilent(connection);
        })) {
            stream.forEach(p -> {
                result.add(p);
                LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    @Test
    void testStreamResultSetSpliterator() throws SQLException {
        final List<Person> result = new ArrayList<>();

        final Connection connection = SERVER.getDataSource().getConnection();
        final Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(1);
        final ResultSet resultSet = statement.executeQuery("select * from PERSON order by ID asc");

        final Spliterator<Person> spliterator = new ResultSetSpliterator<>(resultSet, new PersonRowMapper());

        try (Stream<Person> stream = StreamSupport.stream(spliterator, false).onClose(() -> {
            LOGGER.debug("close jdbc stream");
            JdbcUtils.closeSilent(resultSet);
            JdbcUtils.closeSilent(statement);
            JdbcUtils.closeSilent(connection);
        })) {
            stream.forEach(p -> {
                result.add(p);
                LOGGER.debug("{}: {}", Thread.currentThread().getName(), p);
            });
        }

        assertData(result);
    }

    private void assertData(final List<Person> result) {
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1, result.get(0).id());
        assertEquals("Name1", result.get(0).name());

        assertEquals(2, result.get(1).id());
        assertEquals("Name2", result.get(1).name());
    }
}
