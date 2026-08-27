package de.freese.base.core.function;

import java.util.Objects;

/**
 * @author Thomas Freese
 * @since 16.02.2017
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;

    default ThrowingConsumer<T, E> andThen(final ThrowingConsumer<? super T, E> after) {
        Objects.requireNonNull(after);

        return t -> {
            accept(t);
            after.accept(t);
        };
    }
}
