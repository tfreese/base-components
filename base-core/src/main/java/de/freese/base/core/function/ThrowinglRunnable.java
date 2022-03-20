// Created: 16.02.2017
package de.freese.base.core.function;

/**
 * Interface eines {@link Runnable} mit einer Exception.<br>
 *
 * @param <E> Konkreter Exception-Typ
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ThrowinglRunnable<E extends Exception>
{
    /**
     * Performs this operation.
     *
     * @throws E Falls was schiefgeht.
     */
    void run() throws E;
}
