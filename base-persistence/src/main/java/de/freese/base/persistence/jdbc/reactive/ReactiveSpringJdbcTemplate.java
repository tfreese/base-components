// Created: 16.06.2016
package de.freese.base.persistence.jdbc.reactive;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

/**
 * Unterschiede zum Spring-{@link JdbcTemplate}:<br>
 * <ul>
 * <li>negative FETCH_SIZE möglich
 * <li>Statements werden mit ResultSet.TYPE_FORWARD_ONLY und ResultSet.CONCUR_READ_ONLY erzeugt
 * <li>queryAsFlux
 * <li>queryAsStream
 * </ul>
 * Durch diese Unterschiede wird das {@link ResultSet} nicht komplett in den RAM geladen, sondern die Daten werden Zeilenweise vom DB-Server geholt.<br>
 * Dadurch ergibt sich eine enorme Einsparung im Speicherverbrauch gerade bei größeren Datenmengen.<br>
 * <br>
 * Beipiele:<br>
 * Es kann der org.springframework.jdbc.core.RowMapper oder java.util.function.Function zum Mappen der Entities verwendet werden.
 *
 * <pre>
 * java.util.stream.Stream: Streams MÜSSEN geschlossen werdenn um die DB-Resourcen freizugeben !
 * try (Stream&lt;Entity&gt; stream = jdbcTemplate.queryAsStream("select * from entity", new EntityRowMapper()))
 * {
 *     stream.forEach(System.out::println);
 * }
 * reactor.core.publisher.Flux:
 * jdbcTemplate.queryAsFlux("select * from entity", new EntityRowMapper()).subscribe(System.out::println);
 * </pre>
 *
 * @author Thomas Freese
 */
public class ReactiveSpringJdbcTemplate extends JdbcTemplate
{
    /**
     * Erzeugt die Reactive-Implementierung {@link Stream} oder {@link Flux}.
     *
     * @param <R> Konkreter Reactive-Typ
     * @param <T> Konkreter Entity-Typ
     * @author Thomas Freese
     */
    @FunctionalInterface
    protected interface ReactiveFactory<R, T>
    {
        /**
         * @param connection {@link Connection}
         * @param preparedStatement {@link PreparedStatement}
         * @param resultSet {@link ResultSet}
         * @param rowMapper {@link RowMapper}
         * @return Object
         */
        R createReactive(final Connection connection, final PreparedStatement preparedStatement, final ResultSet resultSet, final RowMapper<T> rowMapper);
    }

    /**
     * {@link Iterable} für ein {@link ResultSet}.<br>
     *
     * @param <T> Type of Entity
     * @author Thomas Freese
     */
    protected static class SpringResultSetIterable<T> implements Iterable<T>
    {
        /**
         *
         */
        private final ResultSet resultSet;

        /**
         *
         */
        private final RowMapper<T> rowMapper;

        /**
         * Erstellt ein neues {@link SpringResultSetIterable} Object.
         *
         * @param resultSet {@link ResultSet}
         * @param rowMapper {@link RowMapper}
         */
        public SpringResultSetIterable(final ResultSet resultSet, final RowMapper<T> rowMapper)
        {
            super();

            this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
            this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
        }

        /**
         * @see java.lang.Iterable#iterator()
         */
        @Override
        public Iterator<T> iterator()
        {
            return new SpringResultSetIterator<>(this.resultSet, this.rowMapper);
        }
    }

    /**
     * {@link Iterator} für ein {@link ResultSet}.<br>
     *
     * @param <T> Type of Entity
     * @author Thomas Freese
     */
    protected static class SpringResultSetIterator<T> implements Iterator<T>
    {
        /**
         *
         */
        private final ResultSet resultSet;

        /**
         *
         */
        private final RowMapper<T> rowMapper;

        /**
         * Erstellt ein neues {@link SpringResultSetIterator} Object.
         *
         * @param resultSet {@link ResultSet}
         * @param rowMapper {@link RowMapper}
         */
        public SpringResultSetIterator(final ResultSet resultSet, final RowMapper<T> rowMapper)
        {
            super();

            this.resultSet = Objects.requireNonNull(resultSet, "resultSet required");
            this.rowMapper = Objects.requireNonNull(rowMapper, "rowMapper required");
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext()
        {
            try
            {
                boolean hasMore = !this.resultSet.isClosed() && !this.resultSet.isAfterLast() && this.resultSet.next();

                // if (!hasMore)
                // {
                // close();
                // }

                return hasMore;
            }
            catch (SQLException ex)
            {
                // close();
                throw new RuntimeException(ex);
            }
        }

        /**
         * @see java.util.Iterator#next()
         */
        @Override
        public T next()
        {
            try
            {
                T entity = this.rowMapper.mapRow(this.resultSet, 0);

                return entity;
            }
            catch (SQLException sex)
            {
                // close();
                throw new RuntimeException(sex);
            }
        }
    }

    /**
     * Simple adapter for CallableStatementCreator, allowing to use a plain SQL statement.
     */
    protected static class StreamingCallableStatementCreator implements CallableStatementCreator, SqlProvider
    {
        /**
         *
         */
        private final String callString;

        /**
         * Erzeugt eine neue Instanz von {@link StreamingCallableStatementCreator}
         *
         * @param callString String
         */
        public StreamingCallableStatementCreator(final String callString)
        {
            super();

            Assert.notNull(callString, "Call string must not be null");
            this.callString = callString;
        }

        /**
         * @see org.springframework.jdbc.core.CallableStatementCreator#createCallableStatement(java.sql.Connection)
         */
        @Override
        public CallableStatement createCallableStatement(final Connection con) throws SQLException
        {
            return con.prepareCall(this.callString, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }

        /**
         * @see org.springframework.jdbc.core.SqlProvider#getSql()
         */
        @Override
        public String getSql()
        {
            return this.callString;
        }
    }

    /**
     * Simple adapter for PreparedStatementCreator, allowing to use a plain SQL statement.
     */
    protected static class StreamingPreparedStatementCreator implements PreparedStatementCreator, SqlProvider
    {
        /**
         *
         */
        private final String sql;

        /**
         * Erzeugt eine neue Instanz von {@link StreamingPreparedStatementCreator}
         *
         * @param sql String
         */
        public StreamingPreparedStatementCreator(final String sql)
        {
            super();

            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
        }

        /**
         * @see org.springframework.jdbc.core.PreparedStatementCreator#createPreparedStatement(java.sql.Connection)
         */
        @Override
        public PreparedStatement createPreparedStatement(final Connection con) throws SQLException
        {
            return con.prepareStatement(this.sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }

        /**
         * @see org.springframework.jdbc.core.SqlProvider#getSql()
         */
        @Override
        public String getSql()
        {
            return this.sql;
        }
    }

    /**
     * Erzeugt eine neue Instanz von {@link ReactiveSpringJdbcTemplate}
     */
    public ReactiveSpringJdbcTemplate()
    {
        super();

        setFetchSize(Integer.getInteger("reactive.jdbc.fetchsize", 500));
    }

    /**
     * Erzeugt eine neue Instanz von {@link ReactiveSpringJdbcTemplate}
     *
     * @param dataSource {@link DataSource}
     */
    public ReactiveSpringJdbcTemplate(final DataSource dataSource)
    {
        super(dataSource);

        setFetchSize(Integer.getInteger("reactive.jdbc.fetchsize", 500));
    }

    /**
     * Erzeugt eine neue Instanz von {@link ReactiveSpringJdbcTemplate}
     *
     * @param dataSource {@link DataSource}
     * @param lazyInit boolean
     */
    public ReactiveSpringJdbcTemplate(final DataSource dataSource, final boolean lazyInit)
    {
        super(dataSource, lazyInit);

        setFetchSize(Integer.getInteger("reactive.jdbc.fetchsize", 500));
    }

    /**
     * @see org.springframework.jdbc.core.JdbcTemplate#applyStatementSettings(java.sql.Statement)
     */
    @Override
    protected void applyStatementSettings(final Statement stmt) throws SQLException
    {
        int fetchSize = getFetchSize();

        if (fetchSize != 0)
        {
            stmt.setFetchSize(fetchSize);
        }

        int maxRows = getMaxRows();

        if (maxRows > 0)
        {
            stmt.setMaxRows(maxRows);
        }

        DataSourceUtils.applyTimeout(stmt, getDataSource(), getQueryTimeout());
    }

    /**
     * Erzeugt einen {@link Flux} aus dem {@link ResultSet}.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Flux#doFinally}-Methode.
     *
     * @param connection {@link Connection}
     * @param preparedStatement {@link PreparedStatement}
     * @param resultSet {@link ResultSet}
     * @param rowMapper {@link RowMapper}
     * @return {@link Flux}
     */
    protected <T> Flux<T> createFlux(final Connection connection, final PreparedStatement preparedStatement, final ResultSet resultSet,
                                     final RowMapper<T> rowMapper)
    {
        Flux<T> flux = Flux.fromIterable(new SpringResultSetIterable<>(resultSet, rowMapper)).doFinally(state -> {
            this.logger.debug("close jdbc flux");

            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
            DataSourceUtils.releaseConnection(connection, getDataSource());
        });

        return flux;
    }

    /**
     * Erzeugt einen {@link Stream} aus dem {@link ResultSet}.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Stream#onClose}-Methode.<br>
     * Daher MUSS die {@link Stream#close}-Methode zwingend aufgerufen werden (try-resource).
     *
     * @param connection {@link Connection}
     * @param preparedStatement {@link PreparedStatement}
     * @param resultSet {@link ResultSet}
     * @param rowMapper {@link RowMapper}
     * @return {@link Stream}
     */
    protected <T> Stream<T> createStream(final Connection connection, final PreparedStatement preparedStatement, final ResultSet resultSet,
                                         final RowMapper<T> rowMapper)
    {
        Stream<T> stream = StreamSupport.stream(new SpringResultSetIterable<>(resultSet, rowMapper).spliterator(), true).onClose(() -> {
            this.logger.debug("close jdbc stream");

            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
            DataSourceUtils.releaseConnection(connection, getDataSource());
        });

        return stream;
    }

    /**
     * Verwendet das {@link ResultSet} mit {@link ResultSet#TYPE_FORWARD_ONLY} und {@link ResultSet#CONCUR_READ_ONLY} (SQL-Streaming).
     *
     * @see org.springframework.jdbc.core.JdbcTemplate#execute(java.lang.String, org.springframework.jdbc.core.CallableStatementCallback)
     */
    @Override
    public <T> T execute(final String callString, final CallableStatementCallback<T> action) throws DataAccessException
    {
        return execute(new StreamingCallableStatementCreator(callString), action);
    }

    /**
     * Verwendet das {@link ResultSet} mit {@link ResultSet#TYPE_FORWARD_ONLY} und {@link ResultSet#CONCUR_READ_ONLY} (SQL-Streaming).
     *
     * @see org.springframework.jdbc.core.JdbcTemplate#execute(java.lang.String, org.springframework.jdbc.core.PreparedStatementCallback)
     */
    @Override
    public <T> T execute(final String sql, final PreparedStatementCallback<T> action) throws DataAccessException
    {
        return execute(new StreamingPreparedStatementCreator(sql), action);
    }

    /**
     * Verwendet das {@link ResultSet} mit {@link ResultSet#TYPE_FORWARD_ONLY} und {@link ResultSet#CONCUR_READ_ONLY} (SQL-Streaming).
     *
     * @see org.springframework.jdbc.core.JdbcTemplate#query(java.lang.String, org.springframework.jdbc.core.PreparedStatementSetter,
     *      org.springframework.jdbc.core.ResultSetExtractor)
     */
    @Override
    public <T> T query(final String sql, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws DataAccessException
    {
        return query(new StreamingPreparedStatementCreator(sql), pss, rse);
    }

    /**
     * Erzeugt einen {@link Flux} aus dem {@link ResultSet}.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Flux#doFinally}-Methode.
     *
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param args Object[]
     * @return {@link Flux}
     * @throws DataAccessException Falls was schief geht.
     */
    public <T> Flux<T> queryAsFlux(final String sql, final RowMapper<T> rowMapper, final Object...args) throws DataAccessException
    {
        Flux<T> flux = queryAsReactive(sql, rowMapper, this::createFlux, args);

        return flux;
    }

    /**
     * Ausführung der Query und zusammenbauen der Reactive-Implementierungen (Stream, Flux) über Factory-Interface.
     *
     * @param <T> Konkreter Entity-Typ
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param reactiveFactory {@link ReactiveFactory}
     * @param args Object
     * @return {@link Flux}
     */
    @SuppressWarnings("resource")
    protected <R, T> R queryAsReactive(final String sql, final RowMapper<T> rowMapper, final ReactiveFactory<R, T> reactiveFactory, final Object...args)
    {
        PreparedStatementCreator psc = new StreamingPreparedStatementCreator(sql);
        PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(args);

        final Connection con = DataSourceUtils.getConnection(getDataSource());
        PreparedStatement ps = null;
        ResultSet rs = null;

        try
        {
            ps = psc.createPreparedStatement(con);
            applyStatementSettings(ps);

            pss.setValues(ps);
            rs = ps.executeQuery();

            handleWarnings(ps);
        }
        catch (SQLException ex)
        {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(con, getDataSource());

            // throw translateException("queryAsReactive", sql, ex);
            throw getExceptionTranslator().translate("queryAsReactive", sql, ex);
        }

        R react = reactiveFactory.createReactive(con, ps, rs, rowMapper);

        return react;
    }

    /**
     * Erzeugt einen {@link Stream} aus dem {@link ResultSet}.<br>
     * Das Schliessen der DB-Resourcen ({@link ResultSet}, {@link Statement}, {@link Connection}) erfolgt in der {@link Stream#onClose}-Methode.<br>
     * Daher MUSS die {@link Stream#close}-Methode zwingend aufgerufen werden (try-resource).e.
     *
     * @param sql String
     * @param rowMapper {@link RowMapper}
     * @param args Object[]
     * @return {@link Stream}
     * @throws DataAccessException Falls was schief geht.
     */
    public <T> Stream<T> queryAsStream(final String sql, final RowMapper<T> rowMapper, final Object...args) throws DataAccessException
    {
        Stream<T> stream = queryAsReactive(sql, rowMapper, this::createStream, args);

        return stream;
    }

    /**
     * Verwendet das {@link ResultSet} mit {@link ResultSet#TYPE_FORWARD_ONLY} und {@link ResultSet#CONCUR_READ_ONLY} (SQL-Streaming).
     *
     * @see org.springframework.jdbc.core.JdbcTemplate#update(java.lang.String, org.springframework.jdbc.core.PreparedStatementSetter)
     */
    @Override
    public int update(final String sql, final PreparedStatementSetter pss) throws DataAccessException
    {
        return update(new StreamingPreparedStatementCreator(sql), pss);
    }
}
