// Created: 16.02.2017
package de.freese.base.core.function;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Interface eines {@link BiConsumer} mit einer Exception.<br>
 *
 * @author Thomas Freese
 *
 * @param <T> Konkreter Parameter-Typ
 * @param <U> Konkreter Parameter-Typ
 * @param <E> Konkreter Exception-Typ
 *
 * @see java.util.function.BiConsumer
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Exception>
{
    /**
     * @param t Object
     * @param u Object
     *
     * @throws Exception Falls was schief geht.
     */
    void accept(T t, U u) throws E;

    /**
     * @param after {@link ThrowingBiConsumer}
     *
     * @return {@link ThrowingBiConsumer}
     */
    default ThrowingBiConsumer<T, U, E> andThen(final ThrowingBiConsumer<? super T, ? super U, E> after)
    {
        Objects.requireNonNull(after);

        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
