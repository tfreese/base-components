package de.freese.base.core.i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Thomas Freese
 * @since 04.08.26
 */
class TestHybridResourceBundle {
    private final DataSource dataSource = mock();
    private final Connection connection = mock();


    @AfterEach
    void afterEach() throws Exception {
        verify(connection, atLeast(3)).close();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    void testHybridResourceBundle() throws SQLException {
        final PreparedStatement preparedStatementLoad = mock();
        final ResultSet resultSetLoad = mock();

        when(connection.prepareStatement("""
                SELECT
                    MSG_KEY,
                    MSG_VALUE
                FROM
                    RESOURCE_BUNDLE
                WHERE
                    BASE_NAME = ?
                    AND LOCALE = ?
                """)).thenReturn(preparedStatementLoad);
        when(preparedStatementLoad.executeQuery()).thenReturn(resultSetLoad);

        when(resultSetLoad.next()).thenReturn(true, false);
        when(resultSetLoad.getString("MSG_KEY")).thenReturn("greeting.hello");
        when(resultSetLoad.getString("MSG_VALUE")).thenReturn("Hello, World!");

        final PreparedStatement preparedStatementReLoad = mock();
        final ResultSet resultSetReLoad = mock();

        when(connection.prepareStatement("""
                SELECT
                    MAX(UPDATED_AT)
                FROM
                    RESOURCE_BUNDLE
                WHERE
                    BASE_NAME = ?
                    AND LOCALE = ?
                """)).thenReturn(preparedStatementReLoad);
        when(preparedStatementReLoad.executeQuery()).thenReturn(resultSetReLoad);

        when(resultSetReLoad.next()).thenReturn(true, false);
        when(resultSetReLoad.getTimestamp(1)).thenReturn(Timestamp.from(java.time.Instant.now()));

        final ResourceBundle.Control control = new HybridResourceBundleControl(dataSource, HybridResourceBundleControl.Priority.DB_OVERRIDES_PROPERTIES, Duration.ofMinutes(5L));
        final ResourceBundle rb = ResourceBundle.getBundle("demo", Locale.GERMANY, control);

        final String value = rb.getString("greeting.hello");

        assertEquals("Hello, World!", value);

        // 3x Query: '' (ROOT), de, de_DE
        verify(connection, times(3)).close();

        verify(preparedStatementLoad, times(3)).executeQuery();
        verify(preparedStatementLoad, times(3)).close();
        verify(resultSetLoad, times(1)).getString("MSG_KEY");
        verify(resultSetLoad, times(1)).getString("MSG_VALUE");
        verify(resultSetLoad, times(3)).close();

        // HybridResourceBundleControl#needsReload is never Called in this Test.
        verify(preparedStatementReLoad, times(0)).executeQuery();
        verify(preparedStatementReLoad, times(0)).close();
        verify(resultSetReLoad, times(0)).getTimestamp(1);
        verify(resultSetReLoad, times(0)).close();
    }
}
