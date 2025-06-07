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

    public DbServerExtension(final EmbeddedDatabaseType databaseType, final boolean autoCommit) {
        super();

        this.databaseType = Objects.requireNonNull(databaseType, "databaseType required");
        this.autoCommit = autoCommit;
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} - afterAll", databaseType);

            final HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

            LOGGER.debug("{} - Connections: idle={}, active={}, total={}, waitingThreads={}",
                    databaseType,
                    poolMXBean.getIdleConnections(),
                    poolMXBean.getActiveConnections(),
                    poolMXBean.getTotalConnections(),
                    poolMXBean.getThreadsAwaitingConnection());
        }

        LOGGER.debug("{} - close datasource", databaseType);

        switch (getDatabaseType()) {
            case HSQL, H2:
                // Handled already by hsql with ';shutdown=true'.
                // Handled already by h2 with ';DB_CLOSE_ON_EXIT=FALSE'.
                //                try (Connection connection = dataSource.getConnection();
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
                throw new IllegalArgumentException("unsupported databaseType: " + databaseType);
        }

        dataSource.close();

        await().pollDelay(Duration.ofMillis(100)).until(() -> true);

        if (!dataSource.isClosed()) {
            dataSource.close();
        }

        if (LOGGER.isDebugEnabled()) {
            final long startTime = getStoreForGlobal(context).get("start-time", long.class);
            final long duration = System.currentTimeMillis() - startTime;

            LOGGER.debug("{} - All Tests took {} ms.", databaseType, duration);
        }

        dataSource = null;

        // System.gc();
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) {
        // final Method testMethod = context.getRequiredTestMethod();
        // final long startTime = getStoreForMethod(context).get("start-time", long.class);
        // final long duration = System.currentTimeMillis() - startTime;
        //
        // LOGGER.debug("{} - Method [{}] took {} ms.", databaseType, testMethod.getName(), duration);
        // LOGGER.debug("{} - Idle Connections = {}", databaseType, dataSource.getHikariPoolMXBean().getIdleConnections());
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        LOGGER.debug("{} - beforeAll", databaseType);

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
                // jdbc:h2:file:~/test;MODE=Oracle;DEFAULT_NULL_ORDERING=HIGH
                config.setDriverClassName("org.h2.Driver");
                config.setJdbcUrl("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=0;DB_CLOSE_ON_EXIT=true");
            }

            case DERBY -> {
                System.setProperty("derby.stream.error.file", "build/derby.log");
                // System.setProperty("derby.stream.error.file", "/dev/null");

                config.setDriverClassName("org.apache.derby.iapi.jdbc.AutoloadedDriver");
                config.setJdbcUrl("jdbc:derby:memory:" + UUID.randomUUID() + ";create=true");
            }
            default -> throw new IllegalArgumentException("unsupported databaseType: " + databaseType);
        }

        config.setUsername("sa");
        config.setPassword("");
        config.setPoolName(getDatabaseType().name());
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(8);
        config.setAutoCommit(autoCommit);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        // config.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");
        config.setConnectionTimeout(getSqlTimeout().toMillis());

        dataSource = new HikariDataSource(config);

        // Trigger init.
        // dataSource.getConnection().close();

        // Class.forName(getDriver(), true, getClass().getClassLoader());

        // final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // populator.addScript(new ClassPathResource("hsqldb-schema.sql"));
        // populator.addScript(new ClassPathResource("hsqldb-data.sql"));
        // populator.execute(dataSource);
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        getStoreForMethod(context).put("start-time", System.currentTimeMillis());
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public EmbeddedDatabaseType getDatabaseType() {
        return databaseType;
    }

    public String getDriver() {
        return dataSource.getDriverClassName();
    }

    public String getPassword() {
        return dataSource.getPassword();
    }

    public String getUrl() {
        return dataSource.getJdbcUrl();
    }

    public String getUsername() {
        return dataSource.getUsername();
    }

    @Override
    public String toString() {
        return databaseType.toString();
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
