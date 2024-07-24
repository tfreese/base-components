// Created: 23 Juli 2024
package de.freese.base.persistence.jdbc.client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import de.freese.base.persistence.jdbc.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.function.StatementConfigurer;
import de.freese.base.persistence.jdbc.function.StatementSetter;

/**
 * @author Thomas Freese
 */
class DefaultQuerySpec implements AbstractJdbcClient.QuerySpec {
    private final AbstractJdbcClient jdbcClient;
    private final CharSequence sql;
    private final StatementConfigurer statementConfigurer;

    public DefaultQuerySpec(final CharSequence sql, final AbstractJdbcClient jdbcClient, final StatementConfigurer statementConfigurer) {
        super();

        this.sql = Objects.requireNonNull(sql, "sql required");
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient required");
        this.statementConfigurer = statementConfigurer;
    }

    @Override
    public <T> T as(final ResultSetCallback<T> resultSetCallback, final StatementSetter<PreparedStatement> statementSetter) {
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
    public <T, C extends Collection<T>> C asCollection(final Supplier<C> collectionFactory, final RowMapper<T> rowMapper,
                                                       final StatementSetter<PreparedStatement> statementSetter) {
        jdbcClient.logSql(sql);

        final ResultSetCallback<C> resultSetCallback = resultSet -> {
            final C results = collectionFactory.get();

            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet));
            }

            return results;
        };

        return as(resultSetCallback, statementSetter);
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
}
