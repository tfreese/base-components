// Created: 14.06.2019
package de.freese.base.persistence.jdbc;

import static org.awaitility.Awaitility.await;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * @author Thomas Freese
 */
public final class DbServerExtension implements BeforeAllCallback, BeforeTestExecutionCallback, AfterAllCallback, AfterTestExecutionCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbServerExtension.class);
    private static final Duration SQL_TIMEOUT = Duration.ofSeconds(5);

    public static Duration getSqlTimeout() {
        return SQL_TIMEOUT;
    }

    public static void showMemory() {
        if (!LOGGER.isDebugEnabled()) {
            return;
        }

        final Runtime runtime = Runtime.getRuntime();
        final long maxMemory = runtime.maxMemory();
        final long allocatedMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();

        final long divider = 1024L * 1024L;
        final String unit = "MB";

        final NumberFormat format = NumberFormat.getInstance();

        LOGGER.debug("Free memory: {}", format.format(freeMemory / divider) + unit);
        LOGGER.debug("Allocated memory: {}", format.format(allocatedMemory / divider) + unit);
        LOGGER.debug("Max memory: {}", format.format(maxMemory / divider) + unit);
        LOGGER.debug("Total free memory: {}", format.format((freeMemory + (maxMemory - allocatedMemory)) / divider) + unit);
    }

    private final boolean autoCommit;
    private final EmbeddedDatabaseType databaseType;

    private HikariDataSource dataSource;
    private JdbcOperations jdbcOperations;

    public DbServerExtension(final EmbeddedDatabaseType databaseType, final boolean autoCommit) {
        super();

        this.databaseType = Objects.requireNonNull(databaseType, "databaseType required");
        this.autoCommit = Objects.requireNonNull(autoCommit, "autoCommit required");
    }

    @Override
    public void afterAll(final ExtensionContext context) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} - afterAll", this.databaseType);

            final HikariPoolMXBean poolMXBean = this.dataSource.getHikariPoolMXBean();

            LOGGER.debug("{} - Connections: idle={}, active={}, total={}",
                    this.databaseType,
                    poolMXBean.getIdleConnections(),
                    poolMXBean.getActiveConnections(),
                    poolMXBean.getTotalConnections());
        }

        LOGGER.debug("{} - close datasource", this.databaseType);

        switch (getDatabaseType()) {
            case HSQL:
            case H2:
                // Handled already by hsql with 'shutdown=true'.
                //                try (Connection connection = this.dataSource.getConnection();
                //                     Statement statement = connection.createStatement()) {
                //                    statement.execute("SHUTDOWN COMPACT");
                //                }
                //                catch (Exception ex) {
                //                    LOGGER.error(ex.getMessage());
                //                }

                break;

            case DERBY:
                break;

            default:
                throw new IllegalArgumentException("unsupported databaseType: " + this.databaseType);
        }

        this.dataSource.close();

        await().pollDelay(Duration.ofMillis(100)).until(() -> true);

        if (!this.dataSource.isClosed()) {
            this.dataSource.close();
        }

        if (LOGGER.isDebugEnabled()) {
            final long startTime = getStoreForGlobal(context).get("start-time", long.class);
            final long duration = System.currentTimeMillis() - startTime;

            LOGGER.debug("{} - All Tests took {} ms.", this.databaseType, duration);
        }

        this.dataSource = null;

        System.gc();
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {
        // final Method testMethod = context.getRequiredTestMethod();
        // final long startTime = getStoreForMethod(context).get("start-time", long.class);
        // final long duration = System.currentTimeMillis() - startTime;
        //
        // LOGGER.debug("{} - Method [{}] took {} ms.", this.databaseType, testMethod.getName(), duration);
        // LOGGER.debug("{} - Idle Connections = {}", this.databaseType, this.dataSource.getHikariPoolMXBean().getIdleConnections());
    }

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        LOGGER.debug("{} - beforeAll", this.databaseType);

        getStoreForGlobal(context).put("start-time", System.currentTimeMillis());

        final HikariConfig config = new HikariConfig();

        switch (getDatabaseType()) {
            case HSQL -> {
                // final JDBCPool pool = new JDBCPool(3);
                // pool.setUrl("jdbc:hsqldb:mem:" + createDbName() + ";shutdown=true");
                // pool.setUser("sa");
                // pool.setPassword(null);

                // ;shutdown=true closes the DB after the end of the last connection.
                // ;MVCC=true;LOCK_MODE=0
                config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
                config.setJdbcUrl("jdbc:hsqldb:mem:" + UUID.randomUUID() + ";shutdown=true");
            }

            case H2 -> {
                // final JdbcConnectionPool pool = JdbcConnectionPool.create("jdbc:h2:mem:" + createDbName() + ";DB_CLOSE_DELAY=0;DB_CLOSE_ON_EXIT=true", "sa", null);
                // pool.setMaxConnections(3);

                // ;DB_CLOSE_DELAY=-1 doesn't close the DB after the end of the last connection.
                // ;DB_CLOSE_ON_EXIT=FALSE doesn't close the DB after the end of the Runtime.
                config.setDriverClassName("org.h2.Driver");
                config.setJdbcUrl("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=0;DB_CLOSE_ON_EXIT=true");
            }

            case DERBY -> {
                config.setDriverClassName("org.apache.derby.iapi.jdbc.AutoloadedDriver");
                config.setJdbcUrl("jdbc:derby:memory:" + UUID.randomUUID() + ";create=true");
            }
            default -> throw new IllegalArgumentException("unsupported databaseType: " + this.databaseType);
        }

        config.setUsername("sa");
        config.setPassword("");
        config.setPoolName(getDatabaseType().name());
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(8);
        config.setAutoCommit(this.autoCommit);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        config.setConnectionTimeout(getSqlTimeout().toMillis());

        this.dataSource = new HikariDataSource(config);

        // Trigger init.
        // this.dataSource.getConnection().close();

        this.jdbcOperations = new JdbcTemplate(this.dataSource);

        // Class.forName(getDriver(), true, getClass().getClassLoader());

        // final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // populator.addScript(new ClassPathResource("hsqldb-schema.sql"));
        // populator.addScript(new ClassPathResource("hsqldb-data.sql"));
        // populator.execute(dataSource);
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) throws Exception {
        getStoreForMethod(context).put("start-time", System.currentTimeMillis());
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public EmbeddedDatabaseType getDatabaseType() {
        return this.databaseType;
    }

    public String getDriver() {
        return this.dataSource.getDriverClassName();
    }

    public JdbcOperations getJdbcOperations() {
        return this.jdbcOperations;
    }

    public String getPassword() {
        return this.dataSource.getPassword();
    }

    public String getUrl() {
        return this.dataSource.getJdbcUrl();
    }

    public String getUsername() {
        return this.dataSource.getUsername();
    }

    /**
     * Object-Store for Test-Class.
     */
    Store getStoreForClass(final ExtensionContext context) {
        return context.getStore(Namespace.create(getClass()));
    }

    /**
     * Object-Store for the hole Test.
     */
    Store getStoreForGlobal(final ExtensionContext context) {
        return context.getStore(Namespace.create("global"));
    }

    /**
     * Object-Store for Test-Method.
     */
    Store getStoreForMethod(final ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}
