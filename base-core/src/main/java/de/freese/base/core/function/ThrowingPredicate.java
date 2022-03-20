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
 * @see java.util.function.Predicate
 */
@FunctionalInterface
public interface ThrowingPredicate<T, E extends Exception>
{
    /**
     * @param targetRef Object
     *
     * @return {@link ThrowingPredicate}
     */
    @SuppressWarnings("unused")
    static <T, E> ThrowingPredicate<T, Exception> isEqual(final Object targetRef)
    {
        ThrowingPredicate<T, Exception> predicate = null;

        if (targetRef == null)
        {
            predicate = Objects::isNull;
        }
        else
        {
            predicate = targetRef::equals;
        }

        return predicate;
    }

    /**
     * @param other {@link ThrowingPredicate}
     *
     * @return {@link ThrowingPredicate}
     */
    default ThrowingPredicate<T, E> and(final ThrowingPredicate<? super T, E> other)
    {
        Objects.requireNonNull(other);

        return t -> test(t) && other.test(t);
    }

    /**
     * @return {@link ThrowingPredicate}
     */
    default ThrowingPredicate<T, E> negate()
    {
        return t -> !test(t);
    }

    /**
     * @param other {@link ThrowingPredicate}
     *
     * @return {@link ThrowingPredicate}
     */
    default ThrowingPredicate<T, E> or(final ThrowingPredicate<? super T, E> other)
    {
        Objects.requireNonNull(other);

        return t -> test(t) || other.test(t);
    }

    /**
     * @param t Object
     *
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     *
     * @throws E Falls was schiefgeht.
     */
    boolean test(T t) throws E;
}
