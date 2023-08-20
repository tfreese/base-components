// Created: 18.08.23
package de.freese.base.persistence.jdbc.template;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
class TestSimpleJdbcTemplateMock {

    private final Connection connection = mock(Connection.class);

    private final DataSource dataSource = mock(DataSource.class);

    private final DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);

    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);

    private final ResultSet resultSet = mock(ResultSet.class);

    private final ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);

    private SimpleJdbcTemplate jdbcTemplate;

    @AfterEach
    void afterEach() throws Exception {
        verify(connection, atLeast(1)).close();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(connection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(preparedStatement);
        when(databaseMetaData.getDriverName()).thenReturn("mock");
        when(databaseMetaData.getDatabaseProductName()).thenReturn("mock");
        when(databaseMetaData.supportsBatchUpdates()).thenReturn(true);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.getConnection()).thenReturn(connection);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

        jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    @Test
    void testQueryFlux() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        List<Object> result = jdbcTemplate.select("some sql").flux(rs -> rs.getObject(1)).collectList().block();

        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testQueryList() throws Exception {
        when(resultSetMetaData.getColumnCount()).thenReturn(1);
        when(resultSetMetaData.getColumnName(anyInt())).thenReturn("col");
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        List<Map<String, Object>> result = jdbcTemplate.select("some sql").list();

        assertEquals(2, result.size());
        assertTrue(result.get(0).containsKey("COL"));
        assertEquals(11, result.get(0).values().iterator().next());
        assertTrue(result.get(1).containsKey("COL"));
        assertEquals(22, result.get(1).values().iterator().next());

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testQueryPublisher() throws Exception {
        List<Object> result = new ArrayList<>();

        List<Flow.Subscriber<Object>> subscribers = List.of(new ResultSetSubscriberForAll<>(result::add), new ResultSetSubscriberForEachObject<>(result::add), new ResultSetSubscriberForFetchSize<>(result::add, 2));

        for (Flow.Subscriber<Object> subscriber : subscribers) {
            result.clear();

            when(resultSet.next()).thenReturn(true, true, false);
            when(resultSet.getObject(1)).thenReturn(11, 22);

            Flow.Publisher<Object> publisher = jdbcTemplate.select("some sql").publisher(rs -> rs.getObject(1));

            publisher.subscribe(subscriber);

            assertEquals(2, result.size());
            assertEquals(11, result.get(0));
            assertEquals(22, result.get(1));
        }

        verify(connection, times(3)).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection, times(3)).close();
        verify(preparedStatement, atLeast(3)).clearParameters();
        verify(preparedStatement, times(3)).executeQuery();
        verify(preparedStatement, times(3)).close();
        verify(resultSet, times(6)).getObject(1);
        verify(resultSet, times(3)).close();
    }

    @Test
    void testQueryStream() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        List<Object> result = null;

        try (Stream<Object> stream = jdbcTemplate.select("some sql").stream(rs -> rs.getObject(1))) {
            result = stream.toList();
        }

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testUpdate() throws Exception {
        when(preparedStatement.executeUpdate()).thenReturn(2);

        int affectedRows = jdbcTemplate.update("some sql").execute();

        assertEquals(2, affectedRows);

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
    }

    @Test
    void testUpdateBatch() throws Exception {
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1});

        int[] affectedRows = jdbcTemplate.update("some sql").executeBatch(List.of(11, 22), (ps, data) -> ps.setInt(1, data), 1);

        assertArrayEquals(new int[]{1, 1}, affectedRows);

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement).setInt(1, 11);
        verify(preparedStatement).setInt(1, 22);
        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement, times(2)).executeBatch();
        verify(preparedStatement, atLeast(2)).clearBatch();
        verify(preparedStatement).close();
    }

    @Test
    void testUpdateParameter() throws Exception {
        when(preparedStatement.executeUpdate()).thenReturn(2);

        int affectedRows = jdbcTemplate.update("some sql").param(1).execute();

        assertEquals(2, affectedRows);

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
    }

    @Test
    void testUpdateParameterNull() throws Exception {
        when(preparedStatement.executeUpdate()).thenReturn(2);

        int affectedRows = jdbcTemplate.update("some sql").param(null).execute();

        assertEquals(2, affectedRows);

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement, atLeast(1)).clearParameters();
        verify(preparedStatement).setNull(1, 0);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).close();
    }
}
