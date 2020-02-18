/**
 * Created: 04.02.2017
 */
package de.freese.base.persistence.jdbc.template.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Stream;
import reactor.core.publisher.Flux;

/**
 * Erzeugt die Reactive-Implementierung {@link Stream} oder {@link Flux}.
 *
 * @author Thomas Freese
 * @param <R> Konkreter Reactive-Typ
 * @param <T> Konkreter Entity-Typ
 */
@FunctionalInterface
public interface ReactiveCallback<R, T>
{
    /**
     * Erzeugt die Reactive-Implementierung {@link Stream} oder {@link Flux}.
     *
     * @param connection {@link Connection}
     * @param statement {@link Statement}
     * @param resultSet {@link ResultSet}
     * @return Object
     */
    public R doReactive(Connection connection, Statement statement, ResultSet resultSet);
}
