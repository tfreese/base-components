// Created: 23 Juli 2024
package de.freese.base.persistence.jdbc.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.exception.PersistenceException;
import de.freese.base.persistence.jdbc.function.CallableStatementMapper;
import de.freese.base.persistence.jdbc.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.function.ResultSetCallbackColumnMap;
import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementSetter;
import de.freese.base.persistence.jdbc.transaction.SimpleTransaction;
import de.freese.base.persistence.jdbc.transaction.Transaction;

/**
 * @author Thomas Freese
 */
public abstract class AbstractJdbcClient {
    public static final ScopedValue<Transaction> TRANSACTION = ScopedValue.newInstance();

    public interface QuerySpec {
        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        default <T> T as(final ResultSetCallback<T> resultSetCallback) {
            return as(resultSetCallback, null);
        }

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        <T> T as(final ResultSetCallback<T> resultSetCallback, StatementSetter<PreparedStatement> statementSetter);

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        default <T, C extends Collection<T>> C asCollection(final Supplier<C> collectionFactory, final RowMapper<T> rowMapper) {
            return asCollection(collectionFactory, rowMapper, null);
        }

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        <T, C extends Collection<T>> C asCollection(final Supplier<C> collectionFactory, final RowMapper<T> rowMapper, StatementSetter<PreparedStatement> statementSetter);

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        default <T> List<T> asList(final RowMapper<T> rowMapper) {
            return asList(rowMapper, null);
        }

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        default <T> List<T> asList(final RowMapper<T> rowMapper, final StatementSetter<PreparedStatement> statementSetter) {
            return asCollection(ArrayList::new, rowMapper, statementSetter);
        }

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        default List<Map<String, Object>> asListOfMaps() {
            return as(new ResultSetCallbackColumnMap());
        }

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        default List<Map<String, Object>> asListOfMaps(final StatementSetter<PreparedStatement> statementSetter) {
            return as(new ResultSetCallbackColumnMap(), statementSetter);
        }

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        <T, K, V> Map<K, List<V>> asMap(final RowMapper<T> rowMapper, Function<T, K> keyMapper, Function<T, V> valueMapper);

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        default <T> Set<T> asSet(final RowMapper<T> rowMapper) {
            return asSet(rowMapper, null);
        }

        /**
         * Execute the SQL with {@link Statement#executeQuery(String)}.
         */
        default <T> Set<T> asSet(final RowMapper<T> rowMapper, final StatementSetter<PreparedStatement> statementSetter) {
            return asCollection(LinkedHashSet::new, rowMapper, statementSetter);
        }
    }

    public interface StatementSpec {
        /**
         * Execute the SQL with {@link CallableStatement#execute()}.
         */
        <R> R call(final StatementSetter<CallableStatement> statementSetter, final CallableStatementMapper<R> mapper);

        /**
         * Execute the SQL with {@link Statement#execute(String)}.
         */
        boolean execute();

        /**
         * Execute the SQL with {@link Statement#executeUpdate(String)}.
         */
        int executeUpdate();

        /**
         * Execute the SQL with {@link Statement#executeUpdate(String)}.
         */
        int executeUpdate(StatementSetter<PreparedStatement> statementSetter);

        /**
         * Execute the SQL with {@link Statement#executeUpdate(String)}.
         */
        int executeUpdate(StatementSetter<PreparedStatement> statementSetter, LongConsumer generatedKeysConsumer);

        QuerySpec query();

        StatementSpec statementConfigurer(StatementConfigurer statementConfigurer);
    }

    private final DataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Function<DataSource, Transaction> transactionHandler;

    protected AbstractJdbcClient(final DataSource dataSource) {
        this(dataSource, SimpleTransaction::new);
    }

    protected AbstractJdbcClient(final DataSource dataSource, final Function<DataSource, Transaction> transactionHandler) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
        this.transactionHandler = Objects.requireNonNull(transactionHandler, "transactionHandler required");
    }

    public Transaction createTransaction() {
        return transactionHandler.apply(getDataSource());
    }

    public boolean isBatchSupported() {
        final ConnectionCallback<Boolean> connectionCallback = this::isBatchSupported;

        return execute(connectionCallback, true);
    }

    public StatementSpec sql(final CharSequence sql) {
        return new DefaultStatementSpec(sql, this);
    }

    protected void close(final Connection connection) {
        final Transaction transaction = TRANSACTION.orElse(null);

        if (transaction != null) {
            // Closed by Transaction#close.
            return;
        }

        getLogger().debug("close connection");

        try {
            if (connection == null || connection.isClosed()) {
                return;
            }

            connection.close();
        }
        catch (Exception ex) {
            //            throw new UncheckedSqlException(ex);
            getLogger().error("Could not close JDBC Connection", ex);
        }
    }

    protected RuntimeException convertException(final Exception ex) {
        Throwable th = ex;

        if (th instanceof RuntimeException re) {
            throw re;
        }

        if (th.getCause() instanceof SQLException) {
            th = th.getCause();
        }

        // while (!(th instanceof SQLException)) {
        // th = th.getCause();
        // }

        return new RuntimeException(th);
    }

    protected <T> T execute(final ConnectionCallback<T> connectionCallback, final boolean closeResources) {
        Connection connection = null;

        try {
            connection = getConnection();

            return connectionCallback.doInConnection(connection);
        }
        catch (SQLException ex) {
            throw convertException(ex);
        }
        finally {
            if (closeResources) {
                close(connection);
            }
        }
    }

    protected Connection getConnection() {
        final Transaction transaction = TRANSACTION.orElse(null);

        if (transaction != null) {
            return transaction.getConnection();
        }

        try {
            return getDataSource().getConnection();
        }
        catch (SQLException ex) {
            throw new PersistenceException(ex);
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected void handleWarnings(final Statement stmt) throws SQLException {
        if (getLogger().isDebugEnabled()) {
            SQLWarning warning = stmt.getWarnings();

            while (warning != null) {
                getLogger().debug("SQLWarning ignored: SQL state '{}', error code '{}', message [{}]", warning.getSQLState(), warning.getErrorCode(), warning.getMessage());

                warning = warning.getNextWarning();
            }
        }
    }

    protected boolean isBatchSupported(final Connection connection) throws SQLException {
        final DatabaseMetaData metaData = connection.getMetaData();

        return metaData.supportsBatchUpdates();
    }

    protected void logSql(final CharSequence sql) {
        if (getLogger().isDebugEnabled()) {
            final String value = sql.toString()
                    .replaceAll("(\\r\\n|\\r|\\n)", " ")
                    .replaceAll("\\s{2,}", " ")
                    .replace("( ", "(")
                    .replace(" )", ")");

            // final Pattern pattern = Pattern.compile("(\r\n|\r|\n)");
            // pattern.matcher(value).replaceAll(" ");

            final String valueLowerCase = value.toLowerCase();

            if (valueLowerCase.startsWith("create") || valueLowerCase.startsWith("drop") || valueLowerCase.startsWith("alter")) {
                getLogger().debug("Executing DDL: {}", FormatStyle.DDL.getFormatter().format(value));
            }
            else {
                getLogger().debug("Executing SQL: {}", FormatStyle.BASIC.getFormatter().format(value));
            }
        }
    }
}
