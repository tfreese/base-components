// Created: 03.01.2018
package de.freese.base.core.function;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ExceptionHandler {
    void handle(Exception ex);
}
