// Created: 11.01.2018
package de.freese.base.persistence.jdbc.reactive;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Anpassung der Spring-Implementierung für "normales" JDBC.<br>
 * Async-JDBC: https://dzone.com/articles/spring-5-webflux-and-jdbc-to-block-or-not-to-block
 *
 * @author Thomas Freese
 * @param <T> Typ der Entity
 * @param <ID> Typ des PrimaryKeys
 */
public interface ReactiveSpringJdbcCrudRepository<T, ID> extends ReactiveCrudRepository<T, ID>
{
    /**
     * @author Thomas Freese
     * @param <T> Typ der Entity
     */
    @FunctionalInterface
    static interface JdbcSubscriber<T> extends Subscriber<T>
    {
        /**
         * @see org.reactivestreams.Subscriber#onComplete()
         */
        @Override
        public default void onComplete()
        {
            // NOOP
        }

        /**
         * @see org.reactivestreams.Subscriber#onError(java.lang.Throwable)
         */
        @Override
        public default void onError(final Throwable t)
        {
            if (t instanceof RuntimeException)
            {
                throw (RuntimeException) t;
            }
            else if (t instanceof IOException)
            {
                throw new UncheckedIOException((IOException) t);
            }

            throw new RuntimeException(t);
        }

        /**
         * @see org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription)
         */
        @Override
        public default void onSubscribe(final Subscription s)
        {
            // Immer alle Objekte anfordern.
            // s.request(1);
            s.request(Long.MAX_VALUE);
        }
    }

    /**
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#deleteAll(java.lang.Iterable)
     */
    @Override
    public default Mono<Void> deleteAll(final Iterable<? extends T> entities)
    {
        entities.forEach(e -> delete(e));

        return Mono.empty();
    }

    /**
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#deleteAll(org.reactivestreams.Publisher)
     */
    @Override
    public default Mono<Void> deleteAll(final Publisher<? extends T> entityStream)
    {
        // throw new NotImplementedException("Publisher is not implemented");

        entityStream.subscribe((JdbcSubscriber<T>) e -> delete(e));

        return Mono.empty();
    }

    /**
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#deleteById(org.reactivestreams.Publisher)
     */
    @Override
    public default Mono<Void> deleteById(final Publisher<ID> id)
    {
        // throw new NotImplementedException("Publisher is not implemented");

        id.subscribe((JdbcSubscriber<ID>) i -> deleteById(i));

        return Mono.empty();
    }

    /**
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#existsById(org.reactivestreams.Publisher)
     */
    @Override
    public default Mono<Boolean> existsById(final Publisher<ID> id)
    {
        // throw new NotImplementedException("Publisher is not implemented");

        id.subscribe((JdbcSubscriber<ID>) i -> existsById(i));

        return Mono.empty();
    }

    /**
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#findAllById(java.lang.Iterable)
     */
    @Override
    public default Flux<T> findAllById(final Iterable<ID> ids)
    {
        List<T> list = new ArrayList<>();

        ids.forEach(id -> list.add(findById(id).block()));

        // Flux = Flux.concat(Mono);
        return Flux.fromIterable(list);
    }

    /**
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#findAllById(org.reactivestreams.Publisher)
     */
    @Override
    public default Flux<T> findAllById(final Publisher<ID> idStream)
    {
        // throw new NotImplementedException("Publisher is not implemented");

        List<T> list = new ArrayList<>();

        idStream.subscribe((JdbcSubscriber<ID>) id -> list.add(findById(id).block()));

        // Flux = Flux.concat(Mono);
        return Flux.fromIterable(list);
    }

    /**
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#findById(org.reactivestreams.Publisher)
     */
    @Override
    public default Mono<T> findById(final Publisher<ID> id)
    {
        // throw new NotImplementedException("Publisher is not implemented");

        id.subscribe((JdbcSubscriber<ID>) i -> findById(i));

        return Mono.empty();
    }

    /**
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#saveAll(java.lang.Iterable)
     */
    @Override
    public default <S extends T> Flux<S> saveAll(final Iterable<S> entities)
    {
        List<S> list = new ArrayList<>();

        entities.forEach(e -> list.add(save(e).block()));

        // Flux = Flux.concat(Mono);
        return Flux.fromIterable(list);
    }

    /**
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#saveAll(org.reactivestreams.Publisher)
     */
    @Override
    public default <S extends T> Flux<S> saveAll(final Publisher<S> entityStream)
    {
        // throw new NotImplementedException("Publisher is not implemented");

        List<S> list = new ArrayList<>();

        entityStream.subscribe((JdbcSubscriber<S>) e -> list.add(save(e).block()));

        // Flux = Flux.concat(Mono);
        return Flux.fromIterable(list);
    }
}
