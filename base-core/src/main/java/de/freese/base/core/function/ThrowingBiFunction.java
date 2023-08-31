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
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Exception> {
    default <V> ThrowingBiFunction<T, U, V, E> andThen(final ThrowingFunction<? super R, V, E> after) {
        Objects.requireNonNull(after);

        return (t, u) -> after.apply(apply(t, u));
    }

    R apply(T t, U u) throws E;
}
