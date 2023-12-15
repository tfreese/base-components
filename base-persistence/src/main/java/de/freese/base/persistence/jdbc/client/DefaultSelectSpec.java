// Created: 11.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import de.freese.base.persistence.jdbc.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.function.ResultSetCallbackColumnMap;
import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.reactive.ResultSetSpliterator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetPublisher;

/**
 * @author Thomas Freese
 */
class DefaultSelectSpec implements JdbcClient.SelectSpec {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSelectSpec.class);

    private final JdbcClient jdbcClient;
    private final CharSequence sql;

    private PreparedStatementSetter preparedStatementSetter;
    private StatementConfigurer statementConfigurer;

    DefaultSelectSpec(final CharSequence sql, final JdbcClient jdbcClient) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
    }

    @Override
    public <T> T execute(final ResultSetCallback<T> resultSetCallback) {
        return jdbcClient.execute(sql, statementConfigurer, preparedStatementSetter, resultSetCallback, true);
    }

    @Override
    public <T> Flux<T> executeAsFlux(final RowMapper<T> rowMapper) {
        final ResultSetCallback<Flux<T>> resultSetCallback = resultSet -> {
            final Statement statement = resultSet.getStatement();
            final Connection connection = statement.getConnection();

            return Flux.generate((final SynchronousSink<T> sink) -> {
                try {
                    if (resultSet.next()) {
                        sink.next(rowMapper.mapRow(resultSet));
                    }
                    else {
                        sink.complete();
                    }
                }
                catch (SQLException ex) {
                    sink.error(ex);
                }
            }).doFinally(state -> {
                LOGGER.debug("close jdbc flux");

                jdbcClient.close(resultSet);
                jdbcClient.close(statement);
                jdbcClient.close(connection);
            });
        };

        return jdbcClient.execute(sql, statementConfigurer, preparedStatementSetter, resultSetCallback, false);
    }

    @Override
    public <T> List<T> executeAsList(final RowMapper<T> rowMapper) {
        final ResultSetCallback<List<T>> resultSetCallback = rs -> {
            final List<T> results = new ArrayList<>();

            while (rs.next()) {
                results.add(rowMapper.mapRow(rs));
            }

            return results;
        };

        return execute(resultSetCallback);
    }

    @Override
    public List<Map<String, Object>> executeAsMap() {
        return execute(new ResultSetCallbackColumnMap());
    }

    @Override
    public <T> Flow.Publisher<T> executeAsPublisher(final RowMapper<T> rowMapper) {
        final ResultSetCallback<Flow.Publisher<T>> resultSetCallback = resultSet -> {
            final Statement statement = resultSet.getStatement();
            final Connection connection = statement.getConnection();

            final Consumer<ResultSet> doOnClose = rs -> {
                LOGGER.debug("close jdbc publisher");

                jdbcClient.close(rs);
                jdbcClient.close(statement);
                jdbcClient.close(connection);
            };

            return new ResultSetPublisher<>(resultSet, rowMapper, doOnClose);
        };

        return jdbcClient.execute(sql, statementConfigurer, preparedStatementSetter, resultSetCallback, false);
    }

    @Override
    public <T> Set<T> executeAsSet(final RowMapper<T> rowMapper) {
        final ResultSetCallback<Set<T>> resultSetCallback = rs -> {
            final Set<T> results = new LinkedHashSet<>();

            while (rs.next()) {
                results.add(rowMapper.mapRow(rs));
            }

            return results;
        };

        return execute(resultSetCallback);
    }

    @Override
    public <T> Stream<T> executeAsStream(final RowMapper<T> rowMapper) {
        final ResultSetCallback<Stream<T>> resultSetCallback = resultSet -> {
            final Statement statement = resultSet.getStatement();
            final Connection connection = statement.getConnection();

            // @formatter:off
            final Spliterator<T> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

            return StreamSupport.stream(spliterator, false)
                    .onClose(() -> {
                        LOGGER.debug("close jdbc stream");

                        jdbcClient.close(resultSet);
                        jdbcClient.close(statement);
                        jdbcClient.close(connection);
                    });
            // @formatter:on
        };

        return jdbcClient.execute(sql, statementConfigurer, preparedStatementSetter, resultSetCallback, false);
    }

    @Override
    public JdbcClient.SelectSpec statementConfigurer(final StatementConfigurer statementConfigurer) {
        this.statementConfigurer = statementConfigurer;

        return this;
    }

    @Override
    public JdbcClient.SelectSpec statementSetter(final PreparedStatementSetter preparedStatementSetter) {
        this.preparedStatementSetter = preparedStatementSetter;

        return this;
    }
}
