// Created: 25 Juli 2024
package de.freese.base.persistence.jdbc.client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;

import de.freese.base.persistence.jdbc.function.ResultSetCallback;
import de.freese.base.persistence.jdbc.function.ResultSetCallbackColumnMap;
import de.freese.base.persistence.jdbc.function.RowMapper;
import de.freese.base.persistence.jdbc.reactive.flow.ResultSetSubscription;

/**
 * @author Thomas Freese
 */
public interface QuerySpec {
    /**
     * Execute the SQL with {@link Statement#executeQuery(String)}.
     */
    <T> T as(ResultSetCallback<T> resultSetCallback);

    /**
     * Execute the SQL with {@link Statement#executeQuery(String)}.
     */
    <T, C extends Collection<T>> C asCollection(Supplier<C> collectionFactory, RowMapper<T> rowMapper);

    /**
     * Closing of the Resources ({@link ResultSet}, {@link Statement}, {@link Connection}) happens in {@link Flux#doFinally}-Method.<br>
     * <b>The JDBC-Treiber must support ResultSet-Streaming(setFetchSize(int)) !</b><br>
     * Reuse is not possible, because the Resources are closed after first usage.<br>
     * Example:<br>
     * <pre>{@code
     * Flux<Entity> flux = jdbcClient.asFlux(RowMapper));
     * flux.subscribe(System.out::println);
     * }</pre>
     */
    <T> Flux<T> asFlux(RowMapper<T> rowMapper);

    /**
     * Execute the SQL with {@link Statement#executeQuery(String)}.
     */
    default <T> List<T> asList(final RowMapper<T> rowMapper) {
        return asCollection(ArrayList::new, rowMapper);
    }

    /**
     * Execute the SQL with {@link Statement#executeQuery(String)}.
     */
    default List<Map<String, Object>> asListOfMaps() {
        return as(new ResultSetCallbackColumnMap());
    }

    /**
     * Execute the SQL with {@link Statement#executeQuery(String)}.
     */
    <T, K, V> Map<K, List<V>> asMap(RowMapper<T> rowMapper, Function<T, K> keyMapper, Function<T, V> valueMapper);

    /**
     * Closing of the Resources ({@link ResultSet}, {@link Statement}, {@link Connection}) happens in {@link ResultSetSubscription}<br>
     * <b>The JDBC-Treiber must support ResultSet-Streaming(setFetchSize(int)) !</b><br>
     * Reuse is not possible, because the Resources are closed after first usage.<br>
     * Example:<br>
     * <pre>{@code
     * Publisher<Entity> publisher = jdbcClient.asPublisher(RowMapper));
     * publisher.subscribe(new java.util.concurrent.Flow.Subscriber);
     * }</pre>
     */
    <T> Flow.Publisher<T> asPublisher(RowMapper<T> rowMapper);

    /**
     * Execute the SQL with {@link Statement#executeQuery(String)}.
     */
    default <T> Set<T> asSet(final RowMapper<T> rowMapper) {
        return asCollection(LinkedHashSet::new, rowMapper);
    }

    /**
     * Execute the SQL with {@link Statement#executeQuery(String)}.<br>
     * <br>
     * Closing of the Resources ({@link ResultSet}, {@link Statement}, {@link Connection}) happens in {@link Stream#onClose}-Method.<br>
     * {@link Stream#close}-Method MUST be called (try-resource).<br>
     * <b>The JDBC-Treiber must support ResultSet-Streaming(setFetchSize(int)) !</b><br>
     * Example:<br>
     * <pre>{@code
     * try (Stream<Entity> stream = jdbcClient.asStream(RowMapper)) {
     *     stream.forEach(System.out::println);
     * }
     * }</pre>
     */
    <T> Stream<T> asStream(RowMapper<T> rowMapper);
}
