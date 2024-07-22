// Created: 11.11.23
package de.freese.base.persistence.jdbc.client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import de.freese.base.persistence.jdbc.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.function.ResultSetCallbackColumnMap;
import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.reactive.ResultSetSpliterator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetPublisher;

/**
 * @author Thomas Freese
 */
class DefaultSelectSpec extends AbstractStatementSpec<JdbcClient.SelectSpec> implements JdbcClient.SelectSpec {
    private final JdbcClient jdbcClient;
    private final CharSequence sql;

    DefaultSelectSpec(final CharSequence sql, final JdbcClient jdbcClient) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
    }

    @Override
    public <T> T execute(final ResultSetCallback<T> resultSetCallback) {
        return jdbcClient.execute(sql, getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, true);
    }

    @Override
    public <T, C extends Collection<T>> C execute(final Supplier<C> collectionFactory, final RowMapper<T> rowMapper) {
        final ResultSetCallback<C> resultSetCallback = rs -> {
            final C results = collectionFactory.get();

            while (rs.next()) {
                results.add(rowMapper.mapRow(rs));
            }

            return results;
        };

        return execute(resultSetCallback);
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
                getLogger().debug("close jdbc flux");

                jdbcClient.close(resultSet);
                jdbcClient.close(statement);
                jdbcClient.close(connection);
            });
        };

        return jdbcClient.execute(sql, getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, false);
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
                getLogger().debug("close jdbc publisher");

                jdbcClient.close(rs);
                jdbcClient.close(statement);
                jdbcClient.close(connection);
            };

            return new ResultSetPublisher<>(resultSet, rowMapper, doOnClose);
        };

        return jdbcClient.execute(sql, getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, false);
    }

    @Override
    public <T> Stream<T> executeAsStream(final RowMapper<T> rowMapper) {
        final ResultSetCallback<Stream<T>> resultSetCallback = resultSet -> {
            final Statement statement = resultSet.getStatement();
            final Connection connection = statement.getConnection();

            final Spliterator<T> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

            return StreamSupport.stream(spliterator, false)
                    .onClose(() -> {
                        getLogger().debug("close jdbc stream");

                        jdbcClient.close(resultSet);
                        jdbcClient.close(statement);
                        jdbcClient.close(connection);
                    });
        };

        return jdbcClient.execute(sql, getStatementConfigurer(), getPreparedStatementSetter(), resultSetCallback, false);
    }

    @Override
    protected DefaultSelectSpec self() {
        return this;
    }
}
