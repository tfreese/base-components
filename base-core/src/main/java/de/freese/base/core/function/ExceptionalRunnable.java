// Created: 16.02.2017
package de.freese.base.core.function;

/**
 * Interface eines {@link Runnable} mit einer Exception.<br>
 *
 * @author Thomas Freese
 * @param <E> Konkreter Exception-Typ
 */
@FunctionalInterface
public interface ExceptionalRunnable<E extends Exception>
{
    /**
     * Performs this operation.
     *
     * @throws E Falls was schief geht.
     */
    public void run() throws E;
}
