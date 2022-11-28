// Created: 10.06.2019
package de.freese.base.persistence.jdbc.reactive.flow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import de.freese.base.persistence.jdbc.template.function.RowMapper;
import de.freese.base.utils.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <T> Entity-Type
 *
 * @author Thomas Freese
 */
public class ResultSetSubscription<T> implements Subscription
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSubscription.class);

    private final Connection connection;

    private final ResultSet resultSet;

    private final RowMapper<T> rowMapper;

    private final Statement statement;

    private final Subscriber<? super T> subscriber;

    ResultSetSubscription(final Connection connection, final Statement statement, final ResultSet resultSet, final RowMapper<T> rowMapper,
                          final Subscriber<? super T> subscriber)
    {
        super();

        this.connection = Objects.requireNonNull(connection, "connection required");
        this.statement = Objects.requireNonNull(statement, "statement required");
        this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
        this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
        this.subscriber = Objects.requireNonNull(subscriber, "subscriber required");
    }

    /**
     * @see java.util.concurrent.Flow.Subscription#cancel()
     */
    @Override
    public void cancel()
    {
        closeJdbcResources();
    }

    /**
     * @see java.util.concurrent.Flow.Subscription#request(long)
     */
    @Override
    public void request(final long n)
    {
        LOGGER.debug("request next {} objects", n);

        try
        {
            for (int i = 0; i < n; i++)
            {
                if (this.resultSet.next())
                {
                    T row = this.rowMapper.mapRow(this.resultSet);
                    this.subscriber.onNext(row);
                }
                else
                {
                    closeJdbcResources();
                    this.subscriber.onComplete();
                    break;
                }
            }
        }
        catch (SQLException sex)
        {
            closeJdbcResources();
            this.subscriber.onError(sex);
        }
    }

    protected void closeJdbcResources()
    {
        LOGGER.debug("close jdbc subscription");

        JdbcUtils.closeSilent(this.resultSet);
        JdbcUtils.closeSilent(this.statement);
        JdbcUtils.closeSilent(this.connection);
    }
}
