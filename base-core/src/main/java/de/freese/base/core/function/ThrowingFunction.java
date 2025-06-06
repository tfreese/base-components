// Created: 16.02.2017
package de.freese.base.core.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {
    static <T> Function<T, T> identity() {
        return t -> t;
    }

    default <V> ThrowingFunction<T, V, E> andThen(final ThrowingFunction<? super R, V, E> after) {
        Objects.requireNonNull(after);

        return t -> after.apply(apply(t));
    }

    R apply(T t) throws E;

    default <V> ThrowingFunction<V, R, E> compose(final ThrowingFunction<? super V, T, E> before) {
        Objects.requireNonNull(before);

        return v -> apply(before.apply(v));
    }
}
