// Created: 16.02.2017
package de.freese.base.core.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * Interface einer {@link Function} mit einer Exception.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Parameter-Typ
 * @param <R> Konkreter Ergebnis-Typ
 * @param <E> Konkreter Exception-Typ
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception>
{
    /**
     * @return {@link Function}
     */
    static <T> Function<T, T> identity()
    {
        return t -> t;
    }

    /**
     * @param after {@link ThrowingFunction}
     * @return {@link ThrowingFunction}
     */
    public default <V> ThrowingFunction<T, V, E> andThen(final ThrowingFunction<? super R, V, E> after)
    {
        Objects.requireNonNull(after);

        return t -> after.apply(apply(t));
    }

    /**
     * @param t Object
     * @return Object
     * @throws Exception Falls was schief geht.
     */
    public R apply(T t) throws E;

    /**
     * @param before {@link ThrowingFunction}
     * @return {@link ThrowingFunction}
     */
    public default <V> ThrowingFunction<V, R, E> compose(final ThrowingFunction<? super V, T, E> before)
    {
        Objects.requireNonNull(before);

        return v -> apply(before.apply(v));
    }
}
