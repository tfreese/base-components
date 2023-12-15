// Created: 26.01.2018
package de.freese.base.core.function;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Interface eines {@link Predicate} mit einer Exception.<br>
 *
 * @param <T> Konkreter Parameter-Typ
 * @param <E> Konkreter Exception-Typ
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ThrowingPredicate<T, E extends Exception> {
    static <T, E> ThrowingPredicate<T, Exception> isEqual(final Object targetRef) {
        final ThrowingPredicate<T, Exception> predicate;

        if (targetRef == null) {
            predicate = Objects::isNull;
        }
        else {
            predicate = targetRef::equals;
        }

        return predicate;
    }

    default ThrowingPredicate<T, E> and(final ThrowingPredicate<? super T, E> other) {
        Objects.requireNonNull(other);

        return t -> test(t) && other.test(t);
    }

    default ThrowingPredicate<T, E> negate() {
        return t -> !test(t);
    }

    default ThrowingPredicate<T, E> or(final ThrowingPredicate<? super T, E> other) {
        Objects.requireNonNull(other);

        return t -> test(t) || other.test(t);
    }

    boolean test(T t) throws E;
}
