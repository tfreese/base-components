// Created: 10.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.SequencedSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.Flow;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import de.freese.base.persistence.jdbc.template.UncheckedSqlException;

/**
 * <a href="https://github.com/spring-projects/spring-framework/blob/main/spring-jdbc/src/main/java/org/springframework/jdbc/core/simple/JdbcClient.java">Spring's JdbcClient</a>
 *
 * @author Thomas Freese
 */
public class JdbcClient {
    class StatementSpec {
        private final CharSequence sql;

        private StatementSetter<?> preparedStatementSetter;
        private StatementConfigurer statementConfigurer;

        public StatementSpec(final CharSequence sql) {
            super();

            this.sql = Objects.requireNonNull(sql, "sql required");
        }

        public StatementSpec setStatementConfigurer(StatementConfigurer statementConfigurer) {
            this.statementConfigurer = statementConfigurer;

            return this;
        }

        int delete() {
            // TODO
            return 0;
        }

        int insert() {
            // TODO
            return 0;
        }

        int insert(final Set<Long> keyHolder) {
            // TODO
            return 0;
        }

        StatementSpec preparedStatementSetter(StatementSetter<?> preparedStatementSetter) {
            this.preparedStatementSetter = preparedStatementSetter;

            return this;
        }

        <T> Flux<T> selectFlux(final RowMapper<T> rowMapper) {
            // TODO
            return null;
        }

        <T> List<T> selectList(RowMapper<T> rowMapper) {
            List<T> results = new ArrayList<>();

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = createPreparedStatement(connection, sql, statementConfigurer)) {

                if (preparedStatementSetter != null) {
                    preparedStatementSetter.setValues(preparedStatement, null);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        results.add(rowMapper.mapRow(resultSet));
                    }
                }

                handleWarnings(preparedStatement);
            }
            catch (SQLException ex) {
                throw convertException(ex);
            }
            //            finally {
            //                if (closeResources) {
            //                    close(resultSet);
            //                }
            //            }

            return results;
        }

        <T> Flow.Publisher<T> selectPublisher(RowMapper<T> rowMapper) {
            // TODO
            return null;
        }

        <T> SequencedSet<T> selectSet(RowMapper<T> rowMapper) {
            return new LinkedHashSet<>(selectList(rowMapper));
        }

        <T> Stream<T> selectStream(RowMapper<T> rowMapper) {
            try {
                final Connection connection = getConnection();
                final PreparedStatement preparedStatement = createPreparedStatement(connection, sql, statementConfigurer);

                if (preparedStatementSetter != null) {
                    preparedStatementSetter.setValues(preparedStatement, null);
                }

                ResultSet resultSet = preparedStatement.executeQuery();

                handleWarnings(preparedStatement);

                Spliterator<T> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

                return StreamSupport.stream(spliterator, false).onClose(() -> {
                    getLogger().debug("close jdbc stream");

                    close(resultSet);
                    close(preparedStatement);
                    close(connection);
                });
            }
            catch (SQLException ex) {
                throw convertException(ex);
            }
        }

        int update() {
            // TODO
            return 0;
        }
    }

    private final DataSource dataSource;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public JdbcClient(final DataSource dataSource) {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    public StatementSpec sql(CharSequence sql) {
        return new StatementSpec(sql);
    }

    protected void close(final Connection connection) {
        //        Transaction transaction = TRANSACTION.orElse(null);
        //
        //        if (transaction != null) {
        //            // Closed by Transaction#close.
        //            return;
        //        }

        getLogger().debug("close connection");

        try {
            if ((connection == null) || connection.isClosed()) {
                return;
            }

            connection.close();
        }
        catch (Exception ex) {
            //            throw new UncheckedSqlException(ex);
            getLogger().error("Could not close JDBC Connection", ex);
        }
    }

    protected void close(final ResultSet resultSet) {
        getLogger().debug("close resultSet");

        try {
            if ((resultSet == null) || resultSet.isClosed()) {
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
            if ((statement == null) || statement.isClosed()) {
                return;
            }

            statement.close();
        }
        catch (Exception ex) {
            getLogger().error("Could not close JDBC Statement", ex);
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

        // while (!(th instanceof SQLException))
        // {
        // th = th.getCause();
        // }

        return new RuntimeException(th);
    }

    protected PreparedStatement createPreparedStatement(final Connection connection, final CharSequence sql, final StatementConfigurer configurer) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        if (configurer != null) {
            configurer.configure(preparedStatement);
        }

        return preparedStatement;
    }

    protected Connection getConnection() {
        try {
            return getDataSource().getConnection();
        }
        catch (SQLException ex) {
            throw new UncheckedSqlException(ex);
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

}
