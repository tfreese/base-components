// Created: 19.08.23
package de.freese.base.persistence.jdbc.newtemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import de.freese.base.persistence.jdbc.reactive.ResultSetSpliterator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetPublisher;
import de.freese.base.persistence.jdbc.template.CachedRowSetResultSetExtractor;
import de.freese.base.persistence.jdbc.template.ColumnMapRowMapper;
import de.freese.base.persistence.jdbc.template.RowMapperResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;
import de.freese.base.persistence.jdbc.template.function.StatementCallback;
import de.freese.base.persistence.jdbc.template.function.StatementCreator;

/**
 * @author Thomas Freese
 */
public class DefaultResultQuerySpec implements ResultQuerySpec {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResultQuerySpec.class);

    private final Supplier<Connection> connectionSupplier;

    private final JdbcOperations jdbcOperations = new JdbcOperations();

    private final StatementCreator<PreparedStatement> statementCreator;

    public DefaultResultQuerySpec(Supplier<Connection> connectionSupplier, StatementCreator<PreparedStatement> statementCreator) {
        super();

        this.connectionSupplier = Objects.requireNonNull(connectionSupplier, "connectionSupplier required");
        this.statementCreator = Objects.requireNonNull(statementCreator, "statementCreator required");
    }

    @Override
    public <T> T extract(final ResultSetExtractor<T> resultSetExtractor) {
        StatementCallback<PreparedStatement, T> action = stmt -> {
            ResultSet resultSet = null;

            try {

                resultSet = stmt.executeQuery();

                return resultSetExtractor.extractData(resultSet);
            }
            finally {
                jdbcOperations.close(resultSet);
            }
        };

        return jdbcOperations.execute(connectionSupplier, statementCreator, action, true);
    }

    @Override
    public <T> Flux<T> flux(final RowMapper<T> rowMapper) {
        StatementCallback<PreparedStatement, Flux<T>> action = stmt -> {
            final ResultSet resultSet = stmt.executeQuery();
            final Connection connection = stmt.getConnection();

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

                jdbcOperations.close(resultSet);
                jdbcOperations.close(stmt);
                jdbcOperations.close(connection);
            });
        };

        return jdbcOperations.execute(connectionSupplier, statementCreator, action, false);
    }

    @Override
    public List<Map<String, Object>> list() {
        RowMapper<Map<String, Object>> rowMapper = new ColumnMapRowMapper();

        return list(rowMapper);
    }

    @Override
    public <T> List<T> list(final RowMapper<T> rowMapper) {
        ResultSetExtractor<List<T>> resultSetExtractor = new RowMapperResultSetExtractor<>(rowMapper);

        return extract(resultSetExtractor);
    }

    @Override
    public <T> Flow.Publisher<T> publisher(final RowMapper<T> rowMapper) {
        StatementCallback<PreparedStatement, Flow.Publisher<T>> action = stmt -> {
            final ResultSet resultSet = stmt.executeQuery();
            final Connection connection = stmt.getConnection();

            Consumer<ResultSet> doOnClose = rs -> {
                LOGGER.debug("close jdbc publisher");

                jdbcOperations.close(rs);
                jdbcOperations.close(stmt);
                jdbcOperations.close(connection);
            };

            return new ResultSetPublisher<>(resultSet, rowMapper, doOnClose);
        };

        return jdbcOperations.execute(connectionSupplier, statementCreator, action, false);
    }

    @Override
    public RowSet rowSet() {
        ResultSetExtractor<CachedRowSet> resultSetExtractor = null;

        try {
            resultSetExtractor = new CachedRowSetResultSetExtractor();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return extract(resultSetExtractor);
    }

    @Override
    public <T> Stream<T> stream(final RowMapper<T> rowMapper) {
        StatementCallback<PreparedStatement, Stream<T>> action = stmt -> {
            final ResultSet resultSet = stmt.executeQuery();
            final Connection connection = stmt.getConnection();

            // @formatter:off
            Spliterator<T> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

            return StreamSupport.stream(spliterator, false)
                    .onClose(() -> {
                        LOGGER.debug("close jdbc stream");

                        jdbcOperations.close(resultSet);
                        jdbcOperations.close(stmt);
                        jdbcOperations.close(connection);
                    });
            // @formatter:on
        };

        return jdbcOperations.execute(connectionSupplier, statementCreator, action, false);
    }
}
