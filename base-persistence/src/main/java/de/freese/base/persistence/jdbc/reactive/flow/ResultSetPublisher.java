// Created: 10.06.2019
package de.freese.base.persistence.jdbc.reactive.flow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;

import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * @param <T> Entity-Type
 *
 * @author Thomas Freese
 */
public class ResultSetPublisher<T> implements Publisher<T>
{
    private final Connection connection;

    private final ResultSet resultSet;

    private final RowMapper<T> rowMapper;

    private final Statement statement;

    public ResultSetPublisher(final Connection connection, final Statement statement, final ResultSet resultSet, final RowMapper<T> rowMapper)
    {
        super();

        this.connection = Objects.requireNonNull(connection, "connection required");
        this.statement = Objects.requireNonNull(statement, "statement required");
        this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
    }

    /**
     * @see java.util.concurrent.Flow.Publisher#subscribe(java.util.concurrent.Flow.Subscriber)
     */
    @Override
    public void subscribe(final Subscriber<? super T> subscriber)
    {
        ResultSetSubscription<T> subscription = new ResultSetSubscription<>(this.connection, this.statement, this.resultSet, this.rowMapper, subscriber);

        subscriber.onSubscribe(subscription);
    }
}
