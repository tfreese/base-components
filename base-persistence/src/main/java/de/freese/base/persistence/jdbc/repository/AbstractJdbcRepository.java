// Created: 17 Mai 2025
package de.freese.base.persistence.jdbc.repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.LongConsumer;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.exception.PersistenceException;
import de.freese.base.persistence.formatter.SqlFormatter;
import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.function.StatementCallback;
import de.freese.base.persistence.jdbc.function.StatementSetter;

/**
 * @author Thomas Freese
 */
public abstract class AbstractJdbcRepository {
    // implements Wrapper

    private final DataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractJdbcRepository(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    // @Override
    // public boolean isWrapperFor(final Class<?> iface) {
    //     if (iface.isInstance(this)) {
    //         return true;
    //     }
    //
    //     if (DataSource.class.equals(iface)) {
    //         return true;
    //     }
    //
    //     return Connection.class.equals(iface);
    // }
    //
    // @Override
    // public <T> T unwrap(final Class<T> iface) throws SQLException {
    //     if (iface.isInstance(this)) {
    //         return (T) this;
    //     }
    //
    //     if (DataSource.class.equals(iface)) {
    //         return (T) getDataSource();
    //     }
    //
    //     if (Connection.class.equals(iface)) {
    //         return (T) getDataSource().getConnection();
    //     }
    //
    //     throw new SQLException(getClass().getName() + " can not be unwrapped as [" + iface.getName() + "]");
    // }

    protected <R> R call(final CharSequence sql, final StatementSetter<CallableStatement> statementSetter, final StatementCallback<CallableStatement, R> mapper) {
        Objects.requireNonNull(sql, "sql required");

        logSql(sql);

        try (Connection connection = getDataSource().getConnection();
             CallableStatement callableStatement = connection.prepareCall(sql.toString())) {

            if (statementSetter != null) {
                statementSetter.setParameter(callableStatement);
            }

            callableStatement.execute();

            return mapper.doInStatement(callableStatement);
        }
        catch (SQLException ex) {
            throw convertException(ex);
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

        return new PersistenceException(th);
    }

    protected boolean execute(final CharSequence sql) {
        Objects.requireNonNull(sql, "sql required");

        logSql(sql);

        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            return statement.execute(sql.toString());
        }
        catch (SQLException ex) {
            throw convertException(ex);
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected void logSql(final CharSequence sql) {
        SqlFormatter.log(sql, getLogger());
    }

    protected <T> List<T> query(final CharSequence sql, final RowMapper<T> rowMapper) {
        return query(sql, rowMapper, null);
    }

    protected <T> List<T> query(final CharSequence sql, final RowMapper<T> rowMapper, final StatementSetter<PreparedStatement> statementSetter) {
        Objects.requireNonNull(sql, "sql required");
        Objects.requireNonNull(rowMapper, "rowMapper required");

        logSql(sql);

        final Instant start = Instant.now();

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            if (statementSetter != null) {
                statementSetter.setParameter(preparedStatement);
            }

            final List<T> result = new ArrayList<>();
            int counter = 0;

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(rowMapper.mapRow(resultSet));
                    counter++;
                }
            }

            if (getLogger().isDebugEnabled()) {
                final Duration needed = Duration.between(start, Instant.now());
                final String message = "processed %d entries in %d.%06ds".formatted(counter, needed.toSecondsPart(), needed.toMillisPart());
                getLogger().debug(message);
            }

            return result;
        }
        catch (SQLException ex) {
            throw convertException(ex);
        }
    }

    protected int update(final CharSequence sql) {
        return update(sql, null);
    }

    protected int update(final CharSequence sql, final StatementSetter<PreparedStatement> statementSetter) {
        return update(sql, statementSetter, null);
    }

    protected int update(final CharSequence sql, final StatementSetter<PreparedStatement> statementSetter, final LongConsumer generatedKeysConsumer) {
        Objects.requireNonNull(sql, "sql required");

        logSql(sql);

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            if (statementSetter != null) {
                statementSetter.setParameter(preparedStatement);
            }

            final int affectedRows = preparedStatement.executeUpdate();

            if (generatedKeysConsumer != null) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        generatedKeysConsumer.accept(generatedKeys.getLong(1));
                    }
                }
            }

            return affectedRows;
        }
        catch (SQLException ex) {
            throw convertException(ex);
        }
    }
}
