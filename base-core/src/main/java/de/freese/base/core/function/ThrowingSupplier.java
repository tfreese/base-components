package de.freese.base.core.function;

/**
 * @author Thomas Freese
 * @since 16.02.2017
 */
@FunctionalInterface
public interface ThrowingSupplier<R, E extends Exception> {
    R get() throws E;
}
