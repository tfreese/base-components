// Created: 23 Juli 2024
package de.freese.base.persistence.jdbc.client;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.exception.PersistenceException;
import de.freese.base.persistence.jdbc.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.transaction.SimpleTransaction;
import de.freese.base.persistence.jdbc.transaction.Transaction;

/**
 * @author Thomas Freese
 */
public class JdbcClient {
    public static final ScopedValue<Transaction> TRANSACTION = ScopedValue.newInstance();
    private static final Pattern PATTERN_LINE_BREAKS = Pattern.compile("(\\r\\n|\\r|\\n)");
    private static final Pattern PATTERN_SPACES = Pattern.compile("\\s{2,}");
    private final DataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Function<DataSource, Transaction> transactionHandler;

    protected JdbcClient(final DataSource dataSource) {
        this(dataSource, SimpleTransaction::new);
    }

    protected JdbcClient(final DataSource dataSource, final Function<DataSource, Transaction> transactionHandler) {
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

    protected void close(final ResultSet resultSet) {
        getLogger().debug("close resultSet");

        try {
            if (resultSet == null || resultSet.isClosed()) {
                return;
            }

            resultSet.close();
        }
        catch (Exception ex) {
            getLogger().error("Could not close JDBC ResultSet", ex);
        }
    }

    protected void close(final Statement statement) {
        getLogger().debug("close statement");

        try {
            if (statement == null || statement.isClosed()) {
                return;
            }

            statement.close();
        }
        catch (Exception ex) {
            getLogger().error("Could not close JDBC Statement", ex);
        }
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
            // connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

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
            String value = sql.toString();
            value = PATTERN_LINE_BREAKS.matcher(value).replaceAll(" ");
            value = PATTERN_SPACES.matcher(value).replaceAll(" ");
            value = value
                    .replace("( ", "(")
                    .replace(" )", ")");

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
