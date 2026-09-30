package de.freese.base.core.i18n;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 * @since 30.09.26
 */
public final class DatasourceResourceProvider implements ResourceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceResourceProvider.class);

    private final DataSource dataSource;

    public DatasourceResourceProvider(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    @Override
    public Map<String, String> loadProperties(final String baseName, final Locale locale) {
        final String localeTag = locale.toString();

        final String sql = """
                SELECT
                    MSG_KEY,
                    MSG_VALUE
                FROM
                    RESOURCE_BUNDLE
                WHERE
                    BASE_NAME = ?
                    AND LOCALE = ?
                """;

        final Map<String, String> map = new HashMap<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, baseName);
            statement.setString(2, localeTag);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    final String key = resultSet.getString("MSG_KEY");
                    final String value = resultSet.getString("MSG_VALUE");

                    if (key != null) {
                        map.put(key, value);
                    }
                }
            }
        }
        catch (final SQLException ex) {
            throw new IllegalStateException("Query failed for: " + baseName + " / " + localeTag, ex);
        }

        return map;
    }

    @Override
    public boolean needsReload(final String baseName, final Locale locale, final long loadTime) {
        final String localeTag = locale.toString();

        final String sql = """
                SELECT
                    MAX(UPDATED_AT)
                FROM
                    RESOURCE_BUNDLE
                WHERE
                    BASE_NAME = ?
                    AND LOCALE = ?
                """;

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, baseName);
            ps.setString(2, localeTag);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final Timestamp ts = rs.getTimestamp(1);

                    if (ts != null && ts.getTime() > loadTime) {
                        return true;
                    }
                }
            }
        }
        catch (final SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Keep the old (working) State.
            return false;
        }

        return false;
    }
}
