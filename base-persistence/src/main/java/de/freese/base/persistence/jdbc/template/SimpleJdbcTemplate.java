// Created: 12.01.2017
package de.freese.base.persistence.jdbc.template;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.Flow.Publisher;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.persistence.jdbc.JdbcUtils;
import de.freese.base.persistence.jdbc.reactive.ResultSetSpliterator;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetPublisher;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscription;
import de.freese.base.persistence.jdbc.template.function.CallableStatementCallback;
import de.freese.base.persistence.jdbc.template.function.CallableStatementCreator;
import de.freese.base.persistence.jdbc.template.function.ConnectionCallback;
import de.freese.base.persistence.jdbc.template.function.ParameterizedPreparedStatementSetter;
import de.freese.base.persistence.jdbc.template.function.PreparedStatementCallback;
import de.freese.base.persistence.jdbc.template.function.PreparedStatementCreator;
import de.freese.base.persistence.jdbc.template.function.PreparedStatementSetter;
import de.freese.base.persistence.jdbc.template.function.ReactiveCallback;
import de.freese.base.persistence.jdbc.template.function.ResultSetExtractor;
import de.freese.base.persistence.jdbc.template.function.RowMapper;
import de.freese.base.persistence.jdbc.template.function.StatementCallback;
import de.freese.base.persistence.jdbc.template.function.StatementCreator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

/**
 * Analog-Implementierung vom org.springframework.jdbc.core.JdbcTemplate.<br>
 *
 * @author Thomas Freese
 */
public class SimpleJdbcTemplate
{
    /**
     *
     */
    private DataSource dataSource = null;

    /**
     * If this variable is set to a non-negative value, it will be used for setting the fetchSize property on statements used for query processing.
     */
    private int fetchSize = -1;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * If this variable is set to a non-negative value, it will be used for setting the maxRows property on statements used for query processing.
     */
    private int maxRows = -1;

    /**
     * If this variable is set to a non-negative value, it will be used for setting the queryTimeout property on statements used for query processing.
     */
    private int queryTimeout = -1;

    /**
     * Erzeugt eine neue Instanz von {@link SimpleJdbcTemplate}
     */
    public SimpleJdbcTemplate()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link SimpleJdbcTemplate}
     *
     * @param dataSource {@link DataSource}
     */
    public SimpleJdbcTemplate(final DataSource dataSource)
    {
        super();

        setDataSource(dataSource);
    }

    /**
     * Prepare the given JDBC Statement (or PreparedStatement or CallableStatement), applying statement settings such as fetch size, max rows, and query
     * timeout.
     *
     * @param statement {@link Statement}
     * @throws SQLException if thrown by JDBC API
     * @see #setFetchSize
     * @see #setMaxRows
     * @see #setQueryTimeout
     * @see org.springframework.jdbc.datasource.DataSourceUtils#applyTransactionTimeout
     */
    protected void applyStatementSettings(final Statement statement) throws SQLException
    {
        int fetchSize = getFetchSize();

        if (fetchSize != -1)
        {
            statement.setFetchSize(fetchSize);
        }

        int maxRows = getMaxRows();

        if (maxRows != -1)
        {
            statement.setMaxRows(maxRows);
        }

        int queryTimeout = getQueryTimeout();

        if (queryTimeout != -1)
        {
            statement.setQueryTimeout(queryTimeout);
        }
    }

    /**
     * Führt ein einfaches {@link CallableStatement#execute()} aus.<br>
     * {call my_procedure(?)};<br>
     * {? = call my_procedure(?)};
     *
     * @param sql String
     */
    public void call(final String sql)
    {
        CallableStatementCreator csc = con -> con.prepareCall(sql);
        CallableStatementCallback<?> action = stmt -> {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug(String.format("call: %s", sql));
            }

            return stmt.execute();
        };

        execute(csc, action);
    }

    /**
     * Führt ein einfaches {@link CallableStatement#execute()} aus.<br>
     * {call my_procedure(?)};<br>
     * {? = call my_procedure(?)};
     *
     * @param sql String
     * @param setter {@link PreparedStatementSetter}
     */
    public void call(final String sql, final PreparedStatementSetter setter)
    {
        CallableStatementCreator csc = con -> con.prepareCall(sql);
        CallableStatementCallback<?> action = stmt -> {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug(String.format("call: %s", sql));
            }

            stmt.clearParameters();
            setter.setValues(stmt);

            return stmt.execute();
        };

        execute(csc, action);
    }

    /**
     * Schliesst die {@link Connection}.<br>
     * Erfolgt eine {@link Exception} wird diese als WARNING geloggt.
     *
     * @param connection {@link Connection}
     */
    protected void closeConnection(final Connection connection)
    {
        try
        {
            JdbcUtils.closeConnection(connection);
        }
        catch (SQLException sex)
        {
            getLogger().debug("Could not close JDBC Connection", sex);
        }
        catch (Throwable ex)
        {
            getLogger().debug("Unexpected exception on closing JDBC Connection", ex);
        }
    }

    /**
     * Schliesst das {@link ResultSet}.<br>
     * Erfolgt eine {@link Exception} wird diese als WARNING geloggt.
     *
     * @param resultSet {@link ResultSet}
     */
    protected void closeResultSet(final ResultSet resultSet)
    {
        try
        {
            JdbcUtils.closeResultSet(resultSet);
        }
        catch (SQLException sex)
        {
            getLogger().trace("Could not close JDBC ResultSet", sex);
        }
        catch (Throwable ex)
        {
            getLogger().trace("Unexpected exception on closing JDBC ResultSet", ex);
        }
    }

    /**
     * Schliesst das {@link Statement}.<br>
     * Erfolgt eine {@link Exception} wird diese als WARNING geloggt.n.
     *
     * @param statement {@link Statement} geht.
     */
    protected void closeStatement(final Statement statement)
    {
        try
        {
            JdbcUtils.closeStatement(statement);
        }
        catch (SQLException sex)
        {
            getLogger().trace("Could not close JDBC Statement", sex);
        }
        catch (Throwable ex)
        {
            getLogger().trace("Unexpected exception on closing JDBC Statement", ex);
        }
    }

    /**
     * Konvertiert bei Bedarf eine Exception.<br>
     * Default: Bei RuntimeException und SQLException wird jeweils der Cause geliefert.
     *
     * @param ex {@link Exception}
     * @return {@link RuntimeException}
     */
    protected RuntimeException convertException(final Exception ex)
    {
        Throwable th = ex;

        if (th instanceof RuntimeException)
        {
            throw (RuntimeException) th;
        }

        if (th.getCause() instanceof SQLException)
        {
            th = th.getCause();
        }

        // while (!(th instanceof SQLException))
        // {
        // th = th.getCause();
        // }

        return new RuntimeException(th);
    }

    /**
     * callableStatement.registerOutParameter(2, java.sql.Types.VARCHAR);<br>
     * callableStatement.execute(); ODER executeUpdate();<br>
     * callableStatement.getString(2);
     *
     * @param <T> Konkreter Return-Typ.
     * @param csc {@link CallableStatementCreator}
     * @param action {@link CallableStatementCallback}
     * @return Object
     */
    public <T> T execute(final CallableStatementCreator csc, final CallableStatementCallback<T> action)
    {
        ConnectionCallback<T> cc = con -> {
            try (CallableStatement cs = csc.createCallableStatement(con))
            {
                applyStatementSettings(cs);

                T result = action.doInCallableStatement(cs);

                return result;
            }
        };

        return execute(cc);
    }

    /**
     * @param <T> Konkreter Return-Typ.
     * @param action {@link ConnectionCallback}
     * @return Object
     */
    @SuppressWarnings("resource")
    public <T> T execute(final ConnectionCallback<T> action)
    {
        Connection connection = null;

        try
        {
            connection = getConnection();

            T result = action.doInConnection(connection);

            return result;
        }
        catch (SQLException sex)
        {
            throw convertException(sex);
        }
        finally
        {
            closeConnection(connection);
        }
    }

    /**
     * @param <T> Konkreter Return-Typ.
     * @param psc {@link PreparedStatementCreator}
     * @param action {@link PreparedStatementCallback}
     * @return Object
     */
    public <T> T execute(final PreparedStatementCreator psc, final PreparedStatementCallback<T> action)
    {
        ConnectionCallback<T> cc = con -> {
            try (PreparedStatement ps = psc.createPreparedStatement(con))
            {
                applyStatementSettings(ps);

                T result = action.doInPreparedStatement(ps);

                return result;
            }
        };

        return execute(cc);
    }

    /**
     * @param sc {@link StatementCreator}
     * @param action {@link StatementCallback}
     * @param <T> Konkreter Return-Typ.
     * @return Object
     */
    public <T> T execute(final StatementCreator sc, final StatementCallback<T> action)
    {
        ConnectionCallback<T> cc = con -> {
            try (Statement stmt = sc.createStatement(con))
            {
                applyStatementSettings(stmt);

                T result = action.doInStatement(stmt);

                return result;
            }
        };

        return execute(cc);
    }

    /**
     * Führt ein einfaches {@link Statement#execute(String)} aus.
     *
     * @param sql String
     */
    public void execute(final String sql)
    {
        StatementCreator sc = Connection::createStatement;
        StatementCallback<?> action = stmt -> {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug(String.format("execute: %s", sql));
            }

            return stmt.execute(sql);
        };

        execute(sc, action);
    }

    /**
     * Liefert die {@link Connection} für die Query.
     *
     * @return {@link Connection}
     */
    protected Connection getConnection()
    {
        Connection connection = null;

        // Spring-Variante
        // connection = DataSourceUtils.getConnection(getDataSource());

        try
        {
            connection = getDataSource().getConnection();
        }
        catch (SQLException sex)
        {
            throw convertException(sex);
        }

        return connection;
    }

    /**
     * @return {@link DataSource}
     */
    public DataSource getDataSource()
    {
        return this.dataSource;
    }

    /**
     * Return the fetch size specified for this JdbcTemplate.
     *
     * @return int
     */
    public int getFetchSize()
    {
        return this.fetchSize;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Return the maximum number of rows specified for this JdbcTemplate.
     *
     * @return int
     */
    public int getMaxRows()
    {
        return this.maxRows;
    }

    /**
     * Return the query timeout for statements that this JdbcTemplate executes.
     *
     * @return int
     */
    public int getQueryTimeout()
    {
        return this.queryTimeout;
    }

    /**
     * Abfrage der {@link DatabaseMetaData}.
     *
     * @return boolean
     */
    public boolean isBatchSupported()
    {
        ConnectionCallback<Boolean> action = con -> {
            boolean result = isBatchSupported(con);

            return result;
        };

        return execute(action);
    }

    /**
     * Abfrage der {@link DatabaseMetaData}.
     *
     * @param connection {@link Connection}
     * @return boolean
     * @throws SQLException Falls was schief geht.
     */
    protected boolean isBatchSupported(final Connection connection) throws SQLException
    {
        DatabaseMetaData dbmd = connection.getMetaData();

        return dbmd.supportsBatchUpdates();
    }

    /**
     * Führt ein {@link Statement#executeQuery(String)} aus und extrahiert ein Objekt aus dem {@link ResultSet}.
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rse {@link ResultSetExtractor}
     * @return Object
     */
    public <T> T query(final String sql, final ResultSetExtractor<T> rse)
    {
        StatementCreator sc = Connection::createStatement;
        StatementCallback<T> action = stmt -> {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug(String.format("query: %s", sql));
            }

            try (ResultSet rs = stmt.executeQuery(sql))
            {
                return rse.extractData(rs);
            }
        };

        return execute(sc, action);
    }

    /**
     * Führt ein {@link Statement#executeQuery(String)} aus und extrahiert ein Objekt aus dem {@link ResultSet}.
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rse {@link ResultSetExtractor}
     * @param params Object[]; SQL-Parameter
     * @return Object
     */
    public <T> T query(final String sql, final ResultSetExtractor<T> rse, final Object...params)
    {
        return query(sql, rse, new ArgumentPreparedStatementSetter(params));
    }

    /**
     * Führt ein {@link Statement#executeQuery(String)} aus und extrahiert ein Objekt aus dem {@link ResultSet}.
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rse {@link ResultSetExtractor}
     * @param setter {@link PreparedStatementSetter}
     * @return Object
     */
    public <T> T query(final String sql, final ResultSetExtractor<T> rse, final PreparedStatementSetter setter)
    {
        PreparedStatementCreator psc = con -> con.prepareStatement(sql);
        PreparedStatementCallback<T> action = ps -> {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug(String.format("query: %s", sql));
            }

            ps.clearParameters();
            setter.setValues(ps);

            try (ResultSet rs = ps.executeQuery())
            {
                return rse.extractData(rs);
            }
        };

        return execute(psc, action);
    }

    /**
     * Führt ein {@link Statement#executeQuery(String)} aus und erzeugt über den {@link RowMapper} eine Liste aus Entities.
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @return {@link List}
     */
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper)
    {
        return query(sql, new RowMapperResultSetExtractor<>(rowMapper));
    }

    /**
     * Führt ein {@link Statement#executeQuery(String)} aus und erzeugt über den {@link RowMapper} eine Liste aus Entities.
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param params Object[]; SQL-Parameter
     * @return {@link List}
     */
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, final Object...params)
    {
        return query(sql, rowMapper, new ArgumentPreparedStatementSetter(params));
    }

    /**
     * Führt ein {@link Statement#executeQuery(String)} aus und erzeugt über den {@link RowMapper} eine Liste aus Entities.
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param setter {@link PreparedStatementSetter}
     * @return {@link List}
     */
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, final PreparedStatementSetter setter)
    {
        return query(sql, new RowMapperResultSetExtractor<>(rowMapper), setter);
    }

    /**
     * Erzeugt über den {@link RowMapper} einen {@link Flux} aus Entities.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Flux#doFinally}-Methode.<br>
     * <b>Der JDBC-Treiber muss ResultSet-Streaming unterstützen (setFetchSize(int)) !</b><br>
     * Eine Wiederverwendung des Fluxes ist ebenfalls nicht möglich, da nach dem ersten mal bereits alle DB-Resourcen geschlossen sind.<br>
     * Beispiel: <code>
     * <pre>
     * Flux&lt;Entity&gt; flux = jdbcTemplate.queryAsFlux(Sql, RowMapper));
     * flux.subscribe(System.out::println);
     * </pre>
     * </code>
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @return {@link Flux}
     */
    public <T> Flux<T> queryAsFlux(final String sql, final RowMapper<T> rowMapper)
    {
        return queryAsFlux(sql, rowMapper, (PreparedStatementSetter) null);
    }

    /**
     * Erzeugt über den {@link RowMapper} einen {@link Flux} aus Entities.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Flux#doFinally}-Methode.<br>
     * <b>Der JDBC-Treiber muss ResultSet-Streaming unterstützen (setFetchSize(int)) !</b><br>
     * Eine Wiederverwendung des Fluxes ist ebenfalls nicht möglich, da nach dem ersten mal bereits alle DB-Resourcen geschlossen sind.<br>
     * Beispiel: <code>
     * <pre>
     * Flux&lt;Entity&gt; flux = jdbcTemplate.queryAsFlux(Sql, RowMapper, PreparedStatementSetter));
     * flux.subscribe(System.out::println);
     * </pre>
     * </code>
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param params Object[]; SQL-Parameter
     * @return {@link Flux}
     */
    public <T> Flux<T> queryAsFlux(final String sql, final RowMapper<T> rowMapper, final Object...params)
    {
        return queryAsFlux(sql, rowMapper, new ArgumentPreparedStatementSetter(params));
    }

    /**
     * Erzeugt über den {@link RowMapper} einen {@link Flux} aus Entities.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Flux#doFinally}-Methode.<br>
     * <b>Der JDBC-Treiber muss ResultSet-Streaming unterstützen (setFetchSize(int)) !</b><br>
     * Eine Wiederverwendung des Fluxes ist ebenfalls nicht möglich, da nach dem ersten mal bereits alle DB-Resourcen geschlossen sind.<br>
     * Beispiel: <code>
     * <pre>
     * Flux&lt;Entity&gt; flux = jdbcTemplate.queryAsFlux(Sql, RowMapper, PreparedStatementSetter));
     * flux.subscribe(System.out::println);
     * </pre>
     * </code>
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param setter {@link PreparedStatementSetter}
     * @return {@link Flux}
     */
    public <T> Flux<T> queryAsFlux(final String sql, final RowMapper<T> rowMapper, final PreparedStatementSetter setter)
    {
        // LambdaExceptions.checkedFunction(rs -> rm.mapRow(rs, rowCount.getAndIncrement())

        ReactiveCallback<Flux<T>, T> action = (connection, statement, resultSet) -> {

            // @formatter:off
//            return Flux.fromIterable(new ResultSetIterable<>(resultSet, rowMapper))
//                    .doFinally(state -> {
//                        getLogger().debug("close jdbc flux");
//
//                        closeResultSet(resultSet);
//                        closeStatement(statement);
//                        closeConnection(connection);
//                    })
//                    ;
            // @formatter:on

            // @formatter:off
            return Flux.generate((final SynchronousSink<T> sink) ->
                    {
                        try
                        {
                            if (resultSet.next())
                            {
                                sink.next(rowMapper.mapRow(resultSet));
                            }
                            else
                            {
                                getLogger().debug("close jdbc flux");

                                closeResultSet(resultSet);
                                closeStatement(statement);
                                closeConnection(connection);

                                sink.complete();
                            }
                        }
                        catch (SQLException sex)
                        {
                            sink.error(sex);
                        }
                     })
                    //.map(rowMapper::mapRow)
                    //.onErrorMap(SQLException.class, ExceptionFactory::create)
//                  .doFinally(state -> {
//                      getLogger().debug("close jdbc flux");
//
//                      closeResultSet(resultSet);
//                      closeStatement(statement);
//                      closeConnection(connection);
//                  })
                    ;
            // @formatter:on
        };

        Flux<T> flux = queryAsReactive(sql, setter, action);

        return flux;
    }

    /**
     * Erzeugt einen {@link Publisher} aus dem {@link ResultSet}.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der
     * {@link ResultSetSubscription#closeJdbcResources}-Methode.<br>
     * <b>Der JDBC-Treiber muss ResultSet-Streaming unterstützen (setFetchSize(int)) !</b><br>
     * Eine Wiederverwendung des Publisher ist ebenfalls nicht möglich, da nach dem ersten mal bereits alle DB-Resourcen geschlossen sind.<br>
     * Beispiel: <code>
     * <pre>
     * Publisher&lt;Entity&gt; publisher = jdbcTemplate.queryAsPublisher(Sql, RowMapper, PreparedStatementSetter));
     * publisher.subscribe(new java.util.concurrent.Flow.Subscriber);
     * </pre>
     * </code>
     *
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @return {@link Publisher}
     */
    public <T> Publisher<T> queryAsPublisher(final String sql, final RowMapper<T> rowMapper)
    {
        return queryAsPublisher(sql, rowMapper, (PreparedStatementSetter) null);
    }

    /**
     * Erzeugt einen {@link Publisher} aus dem {@link ResultSet}.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der
     * {@link ResultSetSubscription#closeJdbcResources}-Methode.<br>
     * <b>Der JDBC-Treiber muss ResultSet-Streaming unterstützen (setFetchSize(int)) !</b><br>
     * Eine Wiederverwendung des Publisher ist ebenfalls nicht möglich, da nach dem ersten mal bereits alle DB-Resourcen geschlossen sind.<br>
     * Beispiel: <code>
     * <pre>
     * Publisher&lt;Entity&gt; publisher = jdbcTemplate.queryAsPublisher(Sql, RowMapper, PreparedStatementSetter));
     * publisher.subscribe(new java.util.concurrent.Flow.Subscriber);
     * </pre>
     * </code>
     *
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param params Object[]; SQL-Parameter
     * @return {@link Publisher}
     */
    public <T> Publisher<T> queryAsPublisher(final String sql, final RowMapper<T> rowMapper, final Object...params)
    {
        return queryAsPublisher(sql, rowMapper, new ArgumentPreparedStatementSetter(params));
    }

    /**
     * Erzeugt einen {@link Publisher} aus dem {@link ResultSet}.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der
     * {@link ResultSetSubscription#closeJdbcResources}-Methode.<br>
     * <b>Der JDBC-Treiber muss ResultSet-Streaming unterstützen (setFetchSize(int)) !</b><br>
     * Eine Wiederverwendung des Publisher ist ebenfalls nicht möglich, da nach dem ersten mal bereits alle DB-Resourcen geschlossen sind.<br>
     * Beispiel: <code>
     * <pre>
     * Publisher&lt;Entity&gt; publisher = jdbcTemplate.queryAsPublisher(Sql, RowMapper, PreparedStatementSetter));
     * publisher.subscribe(new java.util.concurrent.Flow.Subscriber);
     * </pre>
     * </code>
     *
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param setter {@link PreparedStatementSetter}
     * @return {@link Publisher}
     */
    public <T> Publisher<T> queryAsPublisher(final String sql, final RowMapper<T> rowMapper, final PreparedStatementSetter setter)
    {
        ReactiveCallback<Publisher<T>, T> action = (connection, statement, resultSet) -> new ResultSetPublisher<>(connection, statement, resultSet, rowMapper);

        Publisher<T> publisher = queryAsReactive(sql, setter, action);

        return publisher;
    }

    /**
     * Ausführung der Query und zusammenbauen der Reactive-Implementierungen ({@link Stream} oder {@link Flux}) über Factory-Interface.
     *
     * @param sql String *
     * @param pss {@link PreparedStatementSetter}; optional
     * @param action {@link ReactiveCallback}
     * @return {@link Stream} oder {@link Flux}
     */
    @SuppressWarnings("resource")
    protected <R, T> R queryAsReactive(final String sql, final PreparedStatementSetter pss, final ReactiveCallback<R, T> action)
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(String.format("queryReactive: %s", sql));
        }

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try
        {
            connection = getConnection();

            if (pss == null)
            {
                statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                // statement = connection.createStatement();
            }
            else
            {
                statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                // statement = connection.prepareStatement(sql);

                pss.setValues((PreparedStatement) statement);
            }

            // Für Streaming muss die #setFetchSize gesetzt sein !
            applyStatementSettings(statement);

            if (pss == null)
            {
                resultSet = statement.executeQuery(sql);
            }
            else
            {
                resultSet = ((PreparedStatement) statement).executeQuery();
            }
        }
        catch (SQLException sex)
        {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(connection);

            throw convertException(sex);
        }

        R reactive = action.doReactive(connection, statement, resultSet);

        return reactive;
    }

    /**
     * Erzeugt über den {@link RowMapper} einen {@link Stream} aus Entities.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Stream#onClose}-Methode.<br>
     * Daher MUSS die {@link Stream#close}-Methode zwingend aufgerufen werden (try-resource).<br>
     * <b>Der JDBC-Treiber muss ResultSet-Streaming unterstützen (setFetchSize(int)) !</b><br>
     * Beispiel: <code>
     * <pre>
     * try (Stream&lt;Entity&gt; stream = jdbcTemplate.queryAsStream(Sql, RowMapper))
     * {
     *     stream.forEach(System.out::println);
     * }
     * </pre>
     * </code>
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @return {@link Stream}
     */
    public <T> Stream<T> queryAsStream(final String sql, final RowMapper<T> rowMapper)
    {
        return queryAsStream(sql, rowMapper, (PreparedStatementSetter) null);
    }

    /**
     * Erzeugt über den {@link RowMapper} einen {@link Stream} aus Entities.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Stream#onClose}-Methode.<br>
     * Daher MUSS die {@link Stream#close}-Methode zwingend aufgerufen werden (try-resource).<br>
     * <b>Der JDBC-Treiber muss ResultSet-Streaming unterstützen (setFetchSize(int)) !</b><br>
     * Beispiel: <code>
     * <pre>
     * try (Stream&lt;Entity&gt; stream = jdbcTemplate.queryAsStream(Sql, RowMapper, PreparedStatementSetter))
     * {
     *     stream.forEach(System.out::println);
     * }
     * </pre>
     * </code>
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param params Object[]; SQL-Parameter
     * @return {@link Stream}
     */
    public <T> Stream<T> queryAsStream(final String sql, final RowMapper<T> rowMapper, final Object...params)
    {
        return queryAsStream(sql, rowMapper, new ArgumentPreparedStatementSetter(params));
    }

    /**
     * Erzeugt über den {@link RowMapper} einen {@link Stream} aus Entities.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Stream#onClose}-Methode.<br>
     * Daher MUSS die {@link Stream#close}-Methode zwingend aufgerufen werden (try-resource).<br>
     * <b>Der JDBC-Treiber muss ResultSet-Streaming unterstützen (setFetchSize(int)) !</b><br>
     * Beispiel: <code>
     * <pre>
     * try (Stream&lt;Entity&gt; stream = jdbcTemplate.queryAsStream(Sql, RowMapper, PreparedStatementSetter))
     * {
     *     stream.forEach(System.out::println);
     * }
     * </pre>
     * </code>
     *
     * @param <T> Konkreter Return-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param setter {@link PreparedStatementSetter}
     * @return {@link Stream}
     */
    public <T> Stream<T> queryAsStream(final String sql, final RowMapper<T> rowMapper, final PreparedStatementSetter setter)
    {
        // LambdaExceptions.checkedFunction(rs -> rm.mapRow(rs, rowCount.getAndIncrement())

        ReactiveCallback<Stream<T>, T> action = (connection, statement, resultSet) -> {

            // @formatter:off
            // int characteristics = Spliterator.CONCURRENT | Spliterator.ORDERED | Spliterator.NONNULL;
            // Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize( new ResultSetIterator<>(resultSet, rowMapper), characteristics);
            Spliterator<T> spliterator = new ResultSetSpliterator<>(resultSet, rowMapper);

            return StreamSupport.stream(spliterator, false)
                    .onClose(() -> {
                        getLogger().debug("close jdbc stream");

                        closeResultSet(resultSet);
                        closeStatement(statement);
                        closeConnection(connection);
                    });
            // @formatter:on
        };

        Stream<T> stream = queryAsReactive(sql, setter, action);

        return stream;
    }

    /**
     * @param dataSource {@link DataSource}
     */
    public void setDataSource(final DataSource dataSource)
    {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    /**
     * Set the fetch size for this JdbcTemplate. This is important for processing large result sets: Setting this higher than the default value will increase
     * processing speed at the cost of memory consumption; setting this lower can avoid transferring row data that will never be read by the application.
     * <p>
     * Default is -1, indicating to use the JDBC driver's default configuration (i.e. to not pass a specific fetch size setting on to the driver).
     * <p>
     * Note: As of 4.3, negative values other than -1 will get passed on to the driver, since e.g. MySQL supports special behavior for
     * {@code Integer.MIN_VALUE}.
     *
     * @param fetchSize int
     * @see java.sql.Statement#setFetchSize
     */
    public void setFetchSize(final int fetchSize)
    {
        this.fetchSize = fetchSize;
    }

    /**
     * Set the maximum number of rows for this JdbcTemplate. This is important for processing subsets of large result sets, avoiding to read and hold the entire
     * result set in the database or in the JDBC driver if we're never interested in the entire result in the first place (for example, when performing searches
     * that might return a large number of matches).
     * <p>
     * Default is -1, indicating to use the JDBC driver's default configuration (i.e. to not pass a specific max rows setting on to the driver).
     * <p>
     * Note: As of 4.3, negative values other than -1 will get passed on to the driver, in sync with {@link #setFetchSize}'s support for special MySQL values.
     *
     * @param maxRows int
     * @see java.sql.Statement#setMaxRows
     */
    public void setMaxRows(final int maxRows)
    {
        this.maxRows = maxRows;
    }

    /**
     * Set the query timeout for statements that this JdbcTemplate executes.
     * <p>
     * Default is -1, indicating to use the JDBC driver's default (i.e. to not pass a specific query timeout setting on the driver).
     * <p>
     * Note: Any timeout specified here will be overridden by the remaining transaction timeout when executing within a transaction that has a timeout specified
     * at the transaction level.
     *
     * @param queryTimeout int
     * @see java.sql.Statement#setQueryTimeout
     */
    public void setQueryTimeout(final int queryTimeout)
    {
        this.queryTimeout = queryTimeout;
    }

    /**
     * Führt ein {@link Statement#executeUpdate(String)} aus (INSERT, UPDATE, DELETE).
     *
     * @param sql String
     * @return int; affectedRows
     */
    public int update(final String sql)
    {
        StatementCreator sc = Connection::createStatement;
        StatementCallback<Integer> action = stmt -> {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug(String.format("update: %s", sql));
            }

            return stmt.executeUpdate(sql);
        };

        return execute(sc, action);
    }

    /**
     * Führt ein {@link Statement#executeUpdate(String)} aus (INSERT, UPDATE, DELETE).
     *
     * @param sql String
     * @param params Object[]; SQL-Parameter
     * @return int; affectedRows
     */
    public int update(final String sql, final Object...params)
    {
        return update(sql, new ArgumentPreparedStatementSetter(params));
    }

    /**
     * Führt ein {@link Statement#executeUpdate(String)} aus (INSERT, UPDATE, DELETE).
     *
     * @param sql String
     * @param setter {@link PreparedStatementSetter}
     * @return int; affectedRows
     */
    public int update(final String sql, final PreparedStatementSetter setter)
    {
        PreparedStatementCreator psc = con -> con.prepareStatement(sql);
        PreparedStatementCallback<Integer> action = ps -> {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug(String.format("update: %s", sql));
            }

            ps.clearParameters();
            setter.setValues(ps);

            return ps.executeUpdate();
        };

        return execute(psc, action);
    }

    /**
     * Führt ein {@link Statement#executeBatch()} aus (INSERT, UPDATE, DELETE).<br>
     * Die Default Batch-Size beträgt 20.
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param setter {@link ParameterizedPreparedStatementSetter}
     * @param batchArgs {@link Collection}
     * @return int[]; affectedRows
     */
    public <T> int[] updateBatch(final String sql, final ParameterizedPreparedStatementSetter<T> setter, final Collection<T> batchArgs)
    {
        return updateBatch(sql, setter, batchArgs, 20);
    }

    /**
     * Führt ein {@link Statement#executeBatch()} aus (INSERT, UPDATE, DELETE).
     *
     * @param <T> Konkreter Row-Typ
     * @param sql String
     * @param setter {@link ParameterizedPreparedStatementSetter}
     * @param batchArgs {@link Collection}
     * @param batchSize int
     * @return int[]; affectedRows
     */
    public <T> int[] updateBatch(final String sql, final ParameterizedPreparedStatementSetter<T> setter, final Collection<T> batchArgs, final int batchSize)
    {
        PreparedStatementCreator psc = con -> con.prepareStatement(sql);
        PreparedStatementCallback<int[]> action = ps -> {
            if (getLogger().isDebugEnabled())
            {
                getLogger().debug(String.format("updateBatch: size=%d; %s", batchArgs.size(), sql));
            }

            boolean supportsBatch = isBatchSupported(ps.getConnection());

            List<int[]> affectedRows = new ArrayList<>();
            int n = 0;

            for (T arg : batchArgs)
            {
                ps.clearParameters();
                setter.setValues(ps, arg);
                n++;

                if (supportsBatch)
                {
                    ps.addBatch();

                    if (((n % batchSize) == 0) || (n == batchArgs.size()))
                    {
                        if (getLogger().isDebugEnabled())
                        {
                            int batchIndex = ((n % batchSize) == 0) ? n / batchSize : (n / batchSize) + 1;
                            int items = n - ((((n % batchSize) == 0) ? (n / batchSize) - 1 : (n / batchSize)) * batchSize);
                            getLogger().debug("Sending SQL batch update #{} with {} items", batchIndex, items);
                        }

                        affectedRows.add(ps.executeBatch());
                        ps.clearBatch();
                    }
                }
                else
                {
                    // Batch nicht möglich -> direkt ausführen.
                    int affectedRow = ps.executeUpdate();

                    affectedRows.add(new int[]
                    {
                            affectedRow
                    });
                }
            }

            return affectedRows.stream().flatMapToInt(IntStream::of).toArray();
        };

        return execute(psc, action);
    }
}
