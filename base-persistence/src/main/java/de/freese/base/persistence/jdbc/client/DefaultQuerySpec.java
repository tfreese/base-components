// Created: 23 Juli 2024
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import de.freese.base.persistence.jdbc.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementSetter;
import de.freese.base.persistence.jdbc.reactive.ResultSetSpliterator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetPublisher;

/**
 * @author Thomas Freese
 */
class DefaultQuerySpec implements QuerySpec {
    private final JdbcClient jdbcClient;
    private final CharSequence sql;
    private final StatementConfigurer statementConfigurer;
    private final StatementSetter<PreparedStatement> statementSetter;

    DefaultQuerySpec(final CharSequence sql, final JdbcClient jdbcClient, final StatementConfigurer statementConfigurer,
                     final StatementSetter<PreparedStatement> statementSetter) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
        this.statementConfigurer = statementConfigurer;
        this.statementSetter = statementSetter;
    }

    @Override
    public <T> T as(final ResultSetCallback<T> resultSetCallback) {
        jdbcClient.logSql(sql);

        final ConnectionCallback<T> connectionCallback = connection -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                if (statementConfigurer != null) {
                    statementConfigurer.configure(preparedStatement);
                }

                if (statementSetter != null) {
                    statementSetter.setParameter(preparedStatement);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    final T result = resultSetCallback.doInResultSet(resultSet);

                    jdbcClient.handleWarnings(preparedStatement);

                    return result;
                }
            }
        };

        return jdbcClient.execute(connectionCallback, true);
    }

    @Override
    public <T, C extends Collection<T>> C asCollection(final Supplier<C> collectionFactory, final RowMapper<T> rowMapper) {
        jdbcClient.logSql(sql);

        final ResultSetCallback<C> resultSetCallback = resultSet -> {
            final C results = collectionFactory.get();

            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet));
            }

            return results;
        };

        return as(resultSetCallback);
    }

    @Override
    public <T> Flux<T> asFlux(final RowMapper<T> rowMapper) {
        jdbcClient.logSql(sql);

        final ConnectionCallback<Flux<T>> connectionCallback = connection -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            if (statementConfigurer != null) {
                statementConfigurer.configure(preparedStatement);
            }

            if (statementSetter != null) {
                statementSetter.setParameter(preparedStatement);
            }

            final ResultSet resultSet = preparedStatement.executeQuery();
            jdbcClient.handleWarnings(preparedStatement);

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
                jdbcClient.getLogger().debug("close jdbc flux");

                jdbcClient.close(resultSet);
                jdbcClient.close(preparedStatement);
                jdbcClient.close(connection);
            });
        };

        return jdbcClient.execute(connectionCallback, false);
    }

    @Override
    public <T, K, V> Map<K, List<V>> asMap(final RowMapper<T> rowMapper, final Function<T, K> keyMapper, final Function<T, V> valueMapper) {
        jdbcClient.logSql(sql);

        final ResultSetCallback<Map<K, List<V>>> resultSetCallback = resultSet -> {
            final Map<K, List<V>> results = new HashMap<>();

            while (resultSet.next()) {
                final T row = rowMapper.mapRow(resultSet);

                final K key = keyMapper.apply(row);
                final V value = valueMapper.apply(row);

                results.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }

            return results;
        };

        return as(resultSetCallback);
    }

    @Override
    public <T> Flow.Publisher<T> asPublisher(final RowMapper<T> rowMapper) {
        jdbcClient.logSql(sql);

        final ConnectionCallback<Flow.Publisher<T>> connectionCallback = connection -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            if (statementConfigurer != null) {
                statementConfigurer.configure(preparedStatement);
            }

            if (statementSetter != null) {
                statementSetter.setParameter(preparedStatement);
            }

            final ResultSet resultSet = preparedStatement.executeQuery();
            jdbcClient.handleWarnings(preparedStatement);

            final Consumer<ResultSet> doOnClose = rs -> {
                jdbcClient.getLogger().debug("close jdbc publisher");

                jdbcClient.close(rs);
                jdbcClient.close(preparedStatement);
                jdbcClient.close(connection);
            };

            return new ResultSetPublisher<>(resultSet, rowMapper, doOnClose);
        };

        return jdbcClient.execute(connectionCallback, false);
    }

    @Override
    public <T> Stream<T> asStream(final RowMapper<T> rowMapper) {
        jdbcClient.logSql(sql);

        final ConnectionCallback<Stream<T>> connectionCallback = connection -> {
            final PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            if (statementConfigurer != null) {
                statementConfigurer.configure(preparedStatement);
            }

            if (statementSetter != null) {
                statementSetter.setParameter(preparedStatement);
            }

            final ResultSet resultSet = preparedStatement.executeQuery();
            jdbcClient.handleWarnings(preparedStatement);

            final Spliterator<T> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

            return StreamSupport.stream(spliterator, false)
                    .onClose(() -> {
                        jdbcClient.getLogger().debug("close jdbc stream");

                        jdbcClient.close(resultSet);
                        jdbcClient.close(preparedStatement);
                        jdbcClient.close(connection);
                    });
        };

        return jdbcClient.execute(connectionCallback, false);
    }
}
