/**
 * Created: 10.06.2019
 */

package de.freese.base.persistence.jdbc.reactive.flow;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * @author Thomas Freese
 * @param <T> Entity-Type
 */
public class ResultSetSubscription<T> implements Subscription
{
    /**
    *
    */
    private final Connection connection;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private final ResultSet resultSet;

    /**
    *
    */
    private final RowMapper<T> rowMapper;

    /**
    *
    */
    private final Statement statement;

    /**
    *
    */
    private final Subscriber<? super T> subscriber;

    /**
     * Erstellt ein neues {@link de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscription} Object.
     *
     * @param connection {@link Connection}
     * @param statement {@link Statement}
     * @param resultSet {@link ResultSet}
     * @param rowMapper {@link RowMapper}
     * @param subscriber {@link Subscriber}
     */
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
        try
        {
            closeJdbcResources();
        }
        catch (SQLException sex)
        {
            getSubscriber().onError(sex);
        }
    }

    /**
     * @throws SQLException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    protected void closeJdbcResources() throws SQLException
    {
        getLogger().debug("close jdbc publisher");

        JdbcUtils.closeResultSet(getResultSet());
        JdbcUtils.closeStatement(getStatement());
        DataSourceUtils.releaseConnection(getConnection(), null);
    }

    /**
     * @return {@link Connection}
     */
    protected Connection getConnection()
    {
        return this.connection;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link ResultSet}
     */
    protected ResultSet getResultSet()
    {
        return this.resultSet;
    }

    /**
     * @return {@link RowMapper}<T>
     */
    protected RowMapper<T> getRowMapper()
    {
        return this.rowMapper;
    }

    /**
     * @return {@link Statement}
     */
    protected Statement getStatement()
    {
        return this.statement;
    }

    /**
     * @return {@link Subscriber}<? super T>
     */
    protected Subscriber<? super T> getSubscriber()
    {
        return this.subscriber;
    }

    /**
     * @see java.util.concurrent.Flow.Subscription#request(long)
     */
    @SuppressWarnings("resource")
    @Override
    public void request(final long n)
    {
        try
        {
            if (getResultSet().isClosed())
            {
                return;
            }

            for (int i = 0; i < n; i++)
            {
                if (getResultSet().next())
                {
                    T row = getRowMapper().mapRow(getResultSet());
                    getSubscriber().onNext(row);
                }
                else
                {
                    getSubscriber().onComplete();
                    closeJdbcResources();
                    break;
                }
            }
        }
        catch (SQLException sex)
        {
            getSubscriber().onError(sex);
        }
    }
}