// Created: 16.02.2017
package de.freese.base.core.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Interface eines {@link Consumer} mit einer Exception.<br>
 *
 * @param <T> Konkreter Parameter-Typ
 * @param <E> Konkreter Exception-Typ
 *
 * @author Thomas Freese
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception>
{
    /**
     * @param t Object
     *
     * @throws Exception Falls was schiefgeht.
     */
    void accept(T t) throws E;

    /**
     * @param after {@link ThrowingConsumer}
     *
     * @return {@link ThrowingConsumer}
     */
    default ThrowingConsumer<T, E> andThen(final ThrowingConsumer<? super T, E> after)
    {
        Objects.requireNonNull(after);

        return t ->
        {
            accept(t);
            after.accept(t);
        };
    }
}
