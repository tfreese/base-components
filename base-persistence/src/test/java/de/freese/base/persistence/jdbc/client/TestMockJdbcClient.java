// Created: 10.11.23
package de.freese.base.persistence.jdbc.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Flow;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForAll;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForEachObject;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscriberForFetchSize;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@SuppressWarnings("try")
class TestMockJdbcClient {
    private final CallableStatement callableStatement = mock();
    private final Connection connection = mock();
    private final DataSource dataSource = mock();
    private final DatabaseMetaData databaseMetaData = mock();
    private final PreparedStatement preparedStatement = mock();
    private final ResultSet resultSet = mock();
    private final ResultSetMetaData resultSetMetaData = mock();
    private final Statement statement = mock();

    private JdbcClient jdbcClient;

    @AfterEach
    void afterEach() throws Exception {
        verify(connection, atLeast(1)).close();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);

        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(connection.prepareStatement(anyString(), eq(ResultSet.TYPE_FORWARD_ONLY), eq(ResultSet.CONCUR_READ_ONLY))).thenReturn(preparedStatement);
        when(connection.prepareCall(anyString(), eq(ResultSet.TYPE_FORWARD_ONLY), eq(ResultSet.CONCUR_READ_ONLY))).thenReturn(callableStatement);
        when(connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(statement);

        when(databaseMetaData.getDriverName()).thenReturn("mock");
        when(databaseMetaData.getDatabaseProductName()).thenReturn("mock");
        when(databaseMetaData.supportsBatchUpdates()).thenReturn(true);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.getConnection()).thenReturn(connection);

        when(callableStatement.execute()).thenReturn(true);
        when(callableStatement.executeQuery()).thenReturn(resultSet);

        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(statement.getConnection()).thenReturn(connection);

        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

        jdbcClient = new JdbcClient(dataSource);
    }

    @Test
    void testSelectConfigurer() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        final List<Object> result = jdbcClient.select("select configurer").statementConfigurer(st -> st.setFetchSize(4)).executeAsList(rs -> rs.getObject(1));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));

        verify(connection).prepareStatement(anyString(), eq(ResultSet.TYPE_FORWARD_ONLY), eq(ResultSet.CONCUR_READ_ONLY));
        verify(connection).close();
        verify(preparedStatement).setFetchSize(4);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).getWarnings();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testSelectFlux() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);
        when(resultSet.getStatement()).thenReturn(preparedStatement);

        final List<Object> result = jdbcClient.select("select flux").executeAsFlux(rs -> rs.getObject(1)).collectList().block();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));

        verify(connection).prepareStatement(anyString(), eq(ResultSet.TYPE_FORWARD_ONLY), eq(ResultSet.CONCUR_READ_ONLY));
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).getWarnings();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testSelectList() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        final List<Object> result = jdbcClient.select("select list").executeAsList(rs -> rs.getObject(1));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));

        verify(connection).prepareStatement(anyString(), eq(ResultSet.TYPE_FORWARD_ONLY), eq(ResultSet.CONCUR_READ_ONLY));
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).getWarnings();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testSelectParameter() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        final List<Object> result = jdbcClient.select("select parameter").statementSetter(ps -> ps.setInt(1, 1)).executeAsList(rs -> rs.getObject(1));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));

        verify(connection).prepareStatement(anyString(), eq(ResultSet.TYPE_FORWARD_ONLY), eq(ResultSet.CONCUR_READ_ONLY));
        verify(connection).close();
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).getWarnings();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testSelectPublisher() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);
        when(resultSet.getStatement()).thenReturn(preparedStatement);

        final List<Object> result = new ArrayList<>();

        final List<Flow.Subscriber<Object>> subscribers = List.of(new ResultSetSubscriberForAll<>(result::add), new ResultSetSubscriberForEachObject<>(result::add), new ResultSetSubscriberForFetchSize<>(result::add, 2));

        for (Flow.Subscriber<Object> subscriber : subscribers) {
            result.clear();

            when(resultSet.next()).thenReturn(true, true, false);
            when(resultSet.getObject(1)).thenReturn(11, 22);

            final Flow.Publisher<Object> publisher = jdbcClient.select("some sql").executeAsPublisher(rs -> rs.getObject(1));

            publisher.subscribe(subscriber);

            assertEquals(2, result.size());
            assertEquals(11, result.get(0));
            assertEquals(22, result.get(1));
        }

        verify(connection, times(3)).prepareStatement(anyString(), eq(ResultSet.TYPE_FORWARD_ONLY), eq(ResultSet.CONCUR_READ_ONLY));
        verify(connection, times(3)).close();
        verify(preparedStatement, times(3)).executeQuery();
        verify(preparedStatement, times(3)).getWarnings();
        verify(preparedStatement, times(3)).close();
        verify(resultSet, times(6)).getObject(1);
        verify(resultSet, times(3)).close();
    }

    @Test
    void testSelectSet() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        final Set<Object> result = jdbcClient.select("select set").executeAsSet(rs -> rs.getObject(1));

        final List<Object> resultList = new ArrayList<>(result);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, resultList.get(0));
        assertEquals(22, resultList.get(1));

        verify(connection).prepareStatement(anyString(), eq(ResultSet.TYPE_FORWARD_ONLY), eq(ResultSet.CONCUR_READ_ONLY));
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).getWarnings();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testSelectStream() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);
        when(resultSet.getStatement()).thenReturn(preparedStatement);

        List<Object> result = null;

        try (Stream<Object> stream = jdbcClient.select("select stream").executeAsStream(rs -> rs.getObject(1))) {
            result = stream.toList();
        }

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));

        verify(connection).prepareStatement(anyString(), eq(ResultSet.TYPE_FORWARD_ONLY), eq(ResultSet.CONCUR_READ_ONLY));
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).getWarnings();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }
}
