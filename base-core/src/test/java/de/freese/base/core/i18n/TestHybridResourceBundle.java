package de.freese.base.core.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 * @since 04.08.2026
 */
class TestHybridResourceBundle {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestHybridResourceBundle.class);

    private static HikariDataSource dataSource;

    @AfterAll
    static void afterAll() {
        final HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

        LOGGER.info("Connections: idle={}, active={}, total={}",
                poolMXBean.getIdleConnections(),
                poolMXBean.getActiveConnections(),
                poolMXBean.getTotalConnections());

        dataSource.close();
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        final HikariConfig config = new HikariConfig();
        // ;DB_CLOSE_DELAY=-1 doesn't close the DB after the end of the last connection.
        // ;DB_CLOSE_ON_EXIT=FALSE doesn't close the DB after the end of the Runtime.
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=0;DB_CLOSE_ON_EXIT=true");
        config.setUsername("sa");
        config.setPassword("");
        config.setPoolName("my-db");
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(3);
        config.setAutoCommit(true);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        // config.addDataSourceProperty("cachePrepStmts", "true");
        // config.addDataSourceProperty("prepStmtCacheSize", "250");
        // config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            final String sql = """
                    CREATE TABLE RESOURCE_BUNDLE
                    (
                        BASE_NAME  VARCHAR2(100) NOT NULL,
                        LOCALE     VARCHAR2(20) NOT NULL,
                        MSG_KEY    VARCHAR2(200) NOT NULL,
                        MSG_VALUE  VARCHAR2(4000),
                        UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        CONSTRAINT PK_RESOURCE_BUNDLE PRIMARY KEY (BASE_NAME, LOCALE, MSG_KEY)
                    );
                    """;
            statement.execute(sql);

            statement.execute("INSERT INTO RESOURCE_BUNDLE (BASE_NAME, LOCALE, MSG_KEY, MSG_VALUE) VALUES ('greeting', 'en', 'greeting.hello', 'Hello')");
            statement.execute("INSERT INTO RESOURCE_BUNDLE (BASE_NAME, LOCALE, MSG_KEY, MSG_VALUE) VALUES ('greeting', 'de', 'greeting.hello', 'Hallo')");
        }
    }

    // @BeforeEach
    // void beforeEach() {
    //     ResourceBundle.clearCache(HybridResourceBundleControl.class.getClassLoader());
    // }

    @Test
    void testPropertyOverrides() {
        final HybridResourceBundleControl bundleControl = new HybridResourceBundleControl(new DatasourceResourceProvider(dataSource),
                HybridResourceBundleControl.Priority.PROPERTY_OVERRIDES, ResourceBundle.Control.TTL_DONT_CACHE);

        ResourceBundle rb = ResourceBundle.getBundle("greeting", Locale.ENGLISH, bundleControl);
        assertEquals("Hello World", rb.getString("greeting.hello"));

        rb = ResourceBundle.getBundle("greeting", Locale.GERMAN, bundleControl);
        assertEquals("Hallo Welt", rb.getString("greeting.hello"));
    }

    @Test
    void testProviderOverrides() {
        final HybridResourceBundleControl bundleControl = new HybridResourceBundleControl(new DatasourceResourceProvider(dataSource),
                HybridResourceBundleControl.Priority.PROVIDER_OVERRIDES, ResourceBundle.Control.TTL_DONT_CACHE);

        ResourceBundle rb = ResourceBundle.getBundle("greeting", Locale.ENGLISH, bundleControl);
        assertEquals("Hello", rb.getString("greeting.hello"));

        rb = ResourceBundle.getBundle("greeting", Locale.GERMAN, bundleControl);
        assertEquals("Hallo", rb.getString("greeting.hello"));
    }
}
