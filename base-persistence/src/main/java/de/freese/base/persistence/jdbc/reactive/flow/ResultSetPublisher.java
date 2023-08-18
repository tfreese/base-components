// Created: 10.06.2019
package de.freese.base.persistence.jdbc.reactive.flow;

import java.sql.ResultSet;
import java.util.Objects;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Consumer;

import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * @author Thomas Freese
 */
public class ResultSetPublisher<T> implements Publisher<T> {

    private final Consumer<ResultSet> doOnClose;
    
    private final ResultSet resultSet;

    private final RowMapper<T> rowMapper;

    public ResultSetPublisher(final ResultSet resultSet, final RowMapper<T> rowMapper, Consumer<ResultSet> doOnClose) {
        super();

        this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
        this.doOnClose = Objects.requireNonNull(doOnClose, "doOnClose required");
    }

    @Override
    public void subscribe(final Subscriber<? super T> subscriber) {
        ResultSetSubscription<T> subscription = new ResultSetSubscription<>(this.resultSet, this.rowMapper, this.doOnClose, subscriber);

        subscriber.onSubscribe(subscription);
    }
}
