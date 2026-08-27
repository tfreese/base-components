package de.freese.base.core.function;

/**
 * @author Thomas Freese
 * @since 16.02.2017
 */
@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {
    void run() throws E;
}
