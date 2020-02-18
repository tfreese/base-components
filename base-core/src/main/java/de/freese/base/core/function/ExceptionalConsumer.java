// Created: 16.02.2017
package de.freese.base.core.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Interface eines {@link Consumer} mit einer Exception.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Parameter-Typ
 * @param <E> Konkreter Exception-Typ
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface ExceptionalConsumer<T, E extends Exception>
{
    /**
     * @see java.util.function.Consumer#accept(Object)
     * @param t Object
     * @throws Exception Falls was schief geht.
     */
    public void accept(T t) throws E;

    /**
     * @see java.util.function.Consumer#andThen(Consumer)
     * @param after {@link ExceptionalConsumer}
     * @return {@link ExceptionalConsumer}
     */
    public default ExceptionalConsumer<T, E> andThen(final ExceptionalConsumer<? super T, E> after)
    {
        Objects.requireNonNull(after);

        return t ->
        {
            accept(t);
            after.accept(t);
        };
    }
}
