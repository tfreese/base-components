// Created: 17.08.23
package de.freese.base.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import de.freese.base.persistence.jdbc.reactive.ResultSetIterable;

/**
 * @author Thomas Freese
 */
//@Execution(ExecutionMode.CONCURRENT)
//@ExtendWith(MockitoExtension.class)

// Otherwise the Mock must be created and configured for each Test-Method.
//@MockitoSettings(strictness = Strictness.LENIENT)
class TestMock {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMock.class);

    private final Connection connection = mock(Connection.class);

    private final DataSource dataSource = mock(DataSource.class);

    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);

    private final ResultSet resultSet = mock(ResultSet.class);

    @AfterEach
    void afterEach() throws Exception {
        verify(preparedStatement).close();
        verify(resultSet).close();
        verify(connection).close();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void testFlux() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        List<Object> result = Flux.fromIterable(new ResultSetIterable<>(resultSet, rs -> rs.getObject(1))).doFinally(signal -> {
            LOGGER.info("close flux");

            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
            JdbcUtils.closeConnection(connection);
        }).collectList().block();

        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));
    }

    @Test
    void testFluxSynchronousSink() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        List<Object> result = Flux.generate((final SynchronousSink<Object> sink) -> {
            try {
                if (resultSet.next()) {
                    sink.next(resultSet.getObject(1));
                }
                else {
                    LOGGER.info("close flux sink");

                    JdbcUtils.closeResultSet(resultSet);
                    JdbcUtils.closeStatement(preparedStatement);
                    JdbcUtils.closeConnection(connection);

                    sink.complete();
                }
            }
            catch (SQLException sex) {
                sink.error(sex);
            }
        }).collectList().block();

        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));
    }

    @Test
    void testMock() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        List<Object> result = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("some sql");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(rs.getObject(1));
            }
        }

        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));
    }

    @Test
    void testStream() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        List<Object> result = new ArrayList<>();

        try (Stream<Object> stream = StreamSupport.stream(new ResultSetIterable<>(resultSet, rs -> rs.getObject(1)).spliterator(), false).onClose(() -> {
            LOGGER.info("close stream");

            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
            JdbcUtils.closeConnection(connection);
        })) {
            result = stream.toList();
        }

        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));
    }
}
