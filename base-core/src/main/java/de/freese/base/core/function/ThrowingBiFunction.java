// Created: 16.02.2017
package de.freese.base.core.function;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Interface einer {@link BiFunction} mit einer Exception.<br>
 *
 * @param <T> Konkreter Parameter-Typ
 * @param <U> Konkreter Parameter-Typ
 * @param <R> Konkreter Ergebnis-Typ
 * @param <E> Konkreter Exception-Typ
 *
 * @author Thomas Freese
 * @see java.util.function.BiFunction
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Exception>
{
    /**
     * @param after {@link ThrowingBiFunction}
     *
     * @return {@link ThrowingBiFunction}
     */
    default <V> ThrowingBiFunction<T, U, V, E> andThen(final ThrowingFunction<? super R, V, E> after)
    {
        Objects.requireNonNull(after);

        return (t, u) -> after.apply(apply(t, u));
    }

    /**
     * @param t Object
     * @param u Object
     *
     * @return Object
     *
     * @throws Exception Falls was schiefgeht.
     */
    R apply(T t, U u) throws E;
}
