// Created: 04.02.2017
package de.freese.base.persistence.jdbc.template.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Flow.Publisher;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;

/**
 * Erzeugt eine Reactive-Pipeline: {@link Stream}, {@link Flux} oder {@link Publisher}.
 *
 * @author Thomas Freese
 *
 * @param <R> Konkreter Reactive-Typ
 * @param <T> Konkreter Entity-Typ
 */
@FunctionalInterface
public interface ReactiveCallback<R, T>
{
    /**
     * Erzeugt eine Reactive-Pipeline: {@link Stream}, {@link Flux} oder {@link Publisher}.
     *
     * @param connection {@link Connection}
     * @param statement {@link Statement}
     * @param resultSet {@link ResultSet}
     *
     * @return Object
     */
    R doReactive(Connection connection, Statement statement, ResultSet resultSet);
}
