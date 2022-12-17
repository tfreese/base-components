// Created: 03.01.2016
package de.freese.base.persistence.jdbc.reactive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(MockitoExtension.class)

// Otherwise the Mock must be created and configured for each Test-Method.
@MockitoSettings(strictness = Strictness.LENIENT)
class TestMockReactiveJdbc
{
    static final Function<ResultSet, City> MAPPING_FUNCTION = resultSet ->
    {
        try
        {
            // return rowMapper.mapRow(resultSet, 0);
            return new City(resultSet.getString("country"), resultSet.getString("city"));
        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMockReactiveJdbc.class);

    // private static final ExceptionalFunction<ResultSet, City, SQLException> MAPPING_FUNCTION = resultSet -> {
    // // return rowMapper.mapRow(resultSet, 0);
    // return new City(resultSet.getString("country"), resultSet.getString("city"));
    // };

    /**
     * @author Thomas Freese
     */
    private static class City
    {
        private final String city;

        private final String country;

        private City(final String country, final String city)
        {
            super();

            this.country = country;
            this.city = city;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("City [country=");
            builder.append(this.country);
            builder.append(", city=");
            builder.append(this.city);
            builder.append("]");

            return builder.toString();
        }
    }

    private final String[][] data =
            {
                    {
                            "Pakistan", "Karachi"
                    },
                    {
                            "Turkey", "Istanbul"
                    },
                    {
                            "China", "Hong Kong"
                    },
                    {
                            "Russia", "Saint Petersburg"
                    },
                    {
                            "Australia", "Sydney"
                    },
                    {
                            "Germany", "Berlin"
                    },
                    {
                            "Spain", "Madrid"
                    }
            };

    private Connection connection;

    private DataSource datasource;

    private ResultSet resultSet;

    private int resultSetIndex;

    private PreparedStatement statement;

    @BeforeEach
    void setup() throws SQLException
    {
        this.resultSetIndex = -1;

        this.datasource = mock(DataSource.class);
        this.connection = mock(Connection.class);
        this.statement = mock(PreparedStatement.class);
        this.resultSet = mock(ResultSet.class);

        when(this.datasource.getConnection()).thenReturn(this.connection);
        when(this.connection.createStatement()).thenReturn(this.statement);
        when(this.statement.executeQuery()).thenReturn(this.resultSet);

        when(this.resultSet.next()).then(invocation ->
        {
            this.resultSetIndex++;
            return this.resultSetIndex < this.data.length;
        });

        when(this.resultSet.getString("country")).then(invocation -> this.data[this.resultSetIndex][0]);

        when(this.resultSet.getString("city")).then(invocation -> this.data[this.resultSetIndex][1]);
    }

    @Test
    void testResultSetFlux() throws SQLException
    {
        // @formatter:off
        Flux<City> flux = Flux.fromIterable(new ResultSetIterable<>(this.resultSet, MAPPING_FUNCTION::apply))
                .doFinally(signal -> {
                    LOGGER.debug("close flux");

                    JdbcUtils.closeResultSet(this.resultSet);
                    JdbcUtils.closeStatement(this.statement);
                    DataSourceUtils.releaseConnection(this.connection, this.datasource);
                });
        // @formatter:on

        // @formatter:off
        Iterator<City> cities = flux.filter(city -> !"China".equalsIgnoreCase(city.country))
                .take(3)
                //.doOnNext(System.out::println) // Zwischenergebnisse
                .toIterable().iterator();
        // @formatter:on

        validateIterator(cities);
    }

    @Test
    void testResultSetFluxSynchronousSink() throws SQLException
    {
        // @formatter:off
        Flux<City> flux = Flux.generate((final SynchronousSink<ResultSet> sink) ->
                {
                    try
                    {
                        if (this.resultSet.next())
                        {
                            sink.next(this.resultSet);
                        }
                        else
                        {
                            LOGGER.debug("close flux sink");

                            JdbcUtils.closeResultSet(this.resultSet);
                            JdbcUtils.closeStatement(this.statement);
                            DataSourceUtils.releaseConnection(this.connection, this.datasource);

                            sink.complete();
                        }
                    }
                    catch (SQLException sex)
                    {
                        sink.error(sex);
                    }
                 })
                .map(MAPPING_FUNCTION)
                //.onErrorMap(SQLException.class, ExceptionFactory::create)
//                .doFinally(signal -> {
//                    System.out.println("close flux sink");
//
//                    JdbcUtils.closeResultSet(this.resultSet);
//                    JdbcUtils.closeStatement(this.statement);
//                    DataSourceUtils.releaseConnection(this.connection, this.datasource);
//                })
                ;
        // @formatter:on

        // @formatter:off
        Iterator<City> cities = flux.filter(city -> !"China".equalsIgnoreCase(city.country))
                .take(3)
                //.doOnNext(System.out::println) // Zwischenergebnisse
                .toIterable().iterator();
        // @formatter:on

        validateIterator(cities);
    }

    @Test
    void testResultSetStream() throws SQLException
    {
        try (Stream<City> stream = StreamSupport.stream(new ResultSetIterable<>(this.resultSet, MAPPING_FUNCTION::apply).spliterator(), false).onClose(() ->
        {
            LOGGER.debug("close stream");

            JdbcUtils.closeResultSet(this.resultSet);
            JdbcUtils.closeStatement(this.statement);
            DataSourceUtils.releaseConnection(this.connection, this.datasource);
        }))
        {
            // @formatter:off
            Iterator<City> cities = stream.filter(city -> !"China".equalsIgnoreCase(city.country))
                    .limit(3)
                    //.peek(System.out::println) // Zwischenergebnisse
                    .iterator();
            // @formatter:on

            validateIterator(cities);
        }
    }

    private void validateIterator(final Iterator<City> cities)
    {
        assertThat(cities.hasNext()).isTrue();
        assertThat(cities.next().country).isEqualTo("Pakistan");

        assertThat(cities.hasNext()).isTrue();
        assertThat(cities.next().country).isEqualTo("Turkey");

        assertThat(cities.hasNext()).isTrue();
        assertThat(cities.next().country).isEqualTo("Russia");

        assertThat(cities.hasNext()).isFalse();
    }
}
