// Created: 26.01.2018
package de.freese.base.core.function;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Interface eines {@link Predicate} mit einer Exception.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Parameter-Typ
 * @param <E> Konkreter Exception-Typ
 * @see java.util.function.Predicate
 */
@FunctionalInterface
public interface ExceptionalPredicate<T, E extends Exception>
{
    /**
     * @see java.util.function.Predicate#isEqual(Object)
     * @param targetRef Object
     * @return {@link ExceptionalPredicate}
     * @see java.util.function.Predicate#isEqual(Object)
     */
    @SuppressWarnings("unused")
    public static <T, E> ExceptionalPredicate<T, Exception> isEqual(final Object targetRef)
    {
        ExceptionalPredicate<T, Exception> predicate = null;

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
     * @see java.util.function.Predicate#and(Predicate)
     * @param other {@link ExceptionalPredicate}
     * @return {@link ExceptionalPredicate}
     */
    public default ExceptionalPredicate<T, E> and(final ExceptionalPredicate<? super T, E> other)
    {
        Objects.requireNonNull(other);

        return t -> test(t) && other.test(t);
    }

    /**
     * @see java.util.function.Predicate#negate()
     * @return {@link ExceptionalPredicate}
     */
    public default ExceptionalPredicate<T, E> negate()
    {
        return t -> !test(t);
    }

    /**
     * @see java.util.function.Predicate#or(Predicate)
     * @param other {@link ExceptionalPredicate}
     * @return {@link ExceptionalPredicate}
     */
    public default ExceptionalPredicate<T, E> or(final ExceptionalPredicate<? super T, E> other)
    {
        Objects.requireNonNull(other);

        return t -> test(t) || other.test(t);
    }

    /**
     * @see java.util.function.Predicate#test(Object)
     * @param t Object
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     * @throws E Falls was schief geht.
     */
    public boolean test(T t) throws E;
}
