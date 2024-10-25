// Created: 16.02.2017
package de.freese.base.core.function;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {
    void run() throws E;
}
