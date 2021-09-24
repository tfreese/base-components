// Created: 16.02.2017
package de.freese.base.core.function;

import java.util.function.Supplier;

/**
 * Interface eines {@link Supplier} mit einer Exception.<br>
 *
 * @author Thomas Freese
 *
 * @param <R> Konkreter Ergebnis-Typ
 * @param <E> Konkreter Exception-Typ
 *
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowingSupplier<R, E extends Exception>
{
    /**
     * @return Object
     *
     * @throws Exception Falls was schief geht.
     */
    R get() throws E;
}
