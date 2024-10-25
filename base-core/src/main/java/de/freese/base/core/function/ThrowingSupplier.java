// Created: 16.02.2017
package de.freese.base.core.function;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ThrowingSupplier<R, E extends Exception> {
    R get() throws E;
}
