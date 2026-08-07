package de.freese.base.core.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author Thomas Freese
 * @since 04.08.26
 */
public final class HybridResourceBundleControl extends ResourceBundle.Control {
    private static final Logger LOGGER = LoggerFactory.getLogger(HybridResourceBundleControl.class);

    public enum Priority {
        /**
         * DB überschreibt Property-Datei (typisch: DB = Overrides/Customizing).
         */
        DB_OVERRIDES_PROPERTIES,

        /**
         * Property-Datei überschreibt DB.
         */
        PROPERTIES_OVERRIDE_DB
    }

    private final DataSource dataSource;
    private final Priority priority;
    private final Duration ttl;

    public HybridResourceBundleControl(final DataSource dataSource, final Priority priority, final Duration ttl) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
        this.priority = Objects.requireNonNull(priority, "priority required");
        this.ttl = Objects.requireNonNull(ttl, "ttl required");
    }

    /**
     * Ein einziges, selbst definiertes Format -> wir übernehmen das Laden komplett.
     */
    @Override
    public List<String> getFormats(final String baseName) {
        return List.of("hybrid");
    }

    @Override
    public long getTimeToLive(final String baseName, final Locale locale) {
        return ttl.toMillis();
    }

    /**
     * DB-Frische prüfen: Wenn seit dem letzten Laden ein Datensatz aktualisiert wurde, wird neu geladen.
     * Property-Änderungen werden über reload (URLConnection) mit erfasst.
     */
    @Override
    public boolean needsReload(final String baseName, final Locale locale, final String format,
                               final ClassLoader loader, final ResourceBundle bundle, final long loadTime) {
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
            ps.setString(2, locale.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final Timestamp ts = rs.getTimestamp(1);

                    if (ts != null && ts.getTime() > loadTime) {
                        return true;
                    }
                }
            }
        } catch (final SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);

            // Im Zweifel nicht neu laden -> alten (funktionierenden) Stand behalten.
            return false;
        }

        return false;
    }

    @Override
    public ResourceBundle newBundle(final String baseName, final Locale locale, final String format,
                                    final ClassLoader loader, final boolean reload) throws IOException {
        if (!"hybrid".equals(format)) {
            return null;
        }

        final Map<String, Object> fromProps = loadProperties(baseName, locale, loader, reload);
        final Map<String, Object> fromDb = loadFromDatabase(baseName, locale);

        // Für diese Locale gibt es keine Daten -> null zurück, damit die Fallback-Kette greift.
        if (fromProps.isEmpty() && fromDb.isEmpty()) {
            return null;
        }

        final Map<String, Object> merged = new HashMap<>();

        if (priority == Priority.DB_OVERRIDES_PROPERTIES) {
            merged.putAll(fromProps);

            // DB gewinnt.
            merged.putAll(fromDb);
        } else {
            merged.putAll(fromDb);

            // Properties gewinnen.
            merged.putAll(fromProps);
        }

        return new MapResourceBundle(merged);
    }

    private Map<String, Object> loadFromDatabase(final String baseName, final Locale locale) {
        // ROOT -> ""
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

        final Map<String, Object> map = new HashMap<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, baseName);
            ps.setString(2, localeTag);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final String value = rs.getString("MSG_VALUE");

                    if (value != null) {
                        map.put(rs.getString("MSG_KEY"), value);
                    }
                }
            }
        } catch (final SQLException ex) {
            // Bewusst hart: fehlerhafte I18n soll nicht stumm zu falschen Texten führen.
            throw new IllegalStateException("I18n-DB-Zugriff fehlgeschlagen: " + baseName + " / '" + localeTag + "'", ex);
        }

        return map;
    }

    private Map<String, Object> loadProperties(final String baseName, final Locale locale, final ClassLoader loader, final boolean reload) throws IOException {
        final String resourceName = toResourceName(toBundleName(baseName, locale), "properties");
        final Map<String, Object> map = new HashMap<>();

        try (InputStream inputStream = openStream(resourceName, loader, reload)) {
            if (inputStream == null) {
                return map;
            }

            final Properties props = new Properties();

            // Seit Java 9 lädt PropertyResourceBundle default UTF-8; hier explizit, um sicherzugehen.
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                props.load(reader);
            }

            for (final String name : props.stringPropertyNames()) {
                map.put(name, props.getProperty(name));
            }
        }

        return map;
    }

    /**
     * Cache-Umgehung bei reload=true (analog zur Default-Control).
     */
    private InputStream openStream(final String resourceName, final ClassLoader loader, final boolean reload) throws IOException {
        if (!reload) {
            return loader.getResourceAsStream(resourceName);
        }

        final URL url = loader.getResource(resourceName);

        if (url == null) {
            return null;
        }

        final URLConnection urlConnection = url.openConnection();
        urlConnection.setUseCaches(false);

        return urlConnection.getInputStream();
    }
}
