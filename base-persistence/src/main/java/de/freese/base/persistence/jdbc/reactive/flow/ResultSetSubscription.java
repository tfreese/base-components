// Created: 10.06.2019
package de.freese.base.persistence.jdbc.reactive.flow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.persistence.jdbc.function.RowMapper;

/**
 * @author Thomas Freese
 */
public class ResultSetSubscription<T> implements Subscription {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscription.class);

    private final Consumer<ResultSet> doOnClose;

    private final RowMapper<T> rowMapper;

    private final Subscriber<? super T> subscriber;

    private ResultSet resultSet;

    ResultSetSubscription(final ResultSet resultSet, final RowMapper<T> rowMapper, final Consumer<ResultSet> doOnClose, final Subscriber<? super T> subscriber) {
        super();

        this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
        this.doOnClose = Objects.requireNonNull(doOnClose, "doOnClose required");
        this.subscriber = Objects.requireNonNull(subscriber, "subscriber required");
    }

    @Override
    public void cancel() {
        closeJdbcResources();
    }

    @Override
    public void request(final long n) {
        LOGGER.debug("request next {} object(s)", n);

        try {
            for (int i = 0; i < n; i++) {
                if (this.resultSet.next()) {
                    T row = this.rowMapper.mapRow(this.resultSet);
                    this.subscriber.onNext(row);
                }
                else {
                    closeJdbcResources();
                    this.subscriber.onComplete();
                    break;
                }
            }
        }
        catch (SQLException sex) {
            closeJdbcResources();
            this.subscriber.onError(sex);
        }
    }

    protected void closeJdbcResources() {
        LOGGER.debug("close jdbc subscription");

        doOnClose.accept(this.resultSet);

        this.resultSet = null;
    }
}
