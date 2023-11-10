// Created: 10.11.23
package de.freese.base.persistence.jdbc.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import java.util.List;
import java.util.SequencedSet;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestJdbcClient {
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
        when(connection.prepareStatement(anyString(), anyInt(), anyInt())).thenReturn(preparedStatement);
        when(connection.prepareCall(anyString(), anyInt(), anyInt())).thenReturn(callableStatement);
        when(connection.createStatement(anyInt(), anyInt())).thenReturn(statement);

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
    void testSelectList() throws Exception {
        when(resultSetMetaData.getColumnCount()).thenReturn(1);
        when(resultSetMetaData.getColumnName(anyInt())).thenReturn("col");
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        List<Object> result = jdbcClient.sql("select list").selectList(rs -> rs.getObject(1));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).getWarnings();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testSelectSet() throws Exception {
        when(resultSetMetaData.getColumnCount()).thenReturn(1);
        when(resultSetMetaData.getColumnName(anyInt())).thenReturn("col");
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        SequencedSet<Object> result = jdbcClient.sql("select set").selectSet(rs -> rs.getObject(1));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, result.getFirst());
        assertEquals(22, result.getLast());

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).getWarnings();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }

    @Test
    void testSelectStream() throws Exception {
        when(resultSetMetaData.getColumnCount()).thenReturn(1);
        when(resultSetMetaData.getColumnName(anyInt())).thenReturn("col");
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(11, 22);

        List<Object> result = null;

        try (Stream<Object> stream = jdbcClient.sql("select stream").selectStream(rs -> rs.getObject(1))) {
            result = stream.toList();
        }

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(11, result.get(0));
        assertEquals(22, result.get(1));

        verify(connection).prepareStatement(anyString(), anyInt(), anyInt());
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).getWarnings();
        verify(preparedStatement).close();
        verify(resultSet, times(2)).getObject(1);
        verify(resultSet).close();
    }
}
