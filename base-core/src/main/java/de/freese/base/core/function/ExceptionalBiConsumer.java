// Created: 16.02.2017
package de.freese.base.core.function;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Interface eines {@link BiConsumer} mit einer Exception.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Parameter-Typ
 * @param <U> Konkreter Parameter-Typ
 * @param <E> Konkreter Exception-Typ
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface ExceptionalBiConsumer<T, U, E extends Exception>
{
    /**
     * @see java.util.function.BiConsumer#accept(Object, Object)
     * @param t Object
     * @param u Object
     * @throws Exception Falls was schief geht.
     */
    public void accept(T t, U u) throws E;

    /**
     * @see java.util.function.Consumer#andThen(Consumer)
     * @param after {@link ExceptionalBiConsumer}
     * @return {@link ExceptionalBiConsumer}
     */
    public default ExceptionalBiConsumer<T, U, E> andThen(final ExceptionalBiConsumer<? super T, ? super U, E> after)
    {
        Objects.requireNonNull(after);

        return (l, r) ->
        {
            accept(l, r);
            after.accept(l, r);
        };
    }
}
