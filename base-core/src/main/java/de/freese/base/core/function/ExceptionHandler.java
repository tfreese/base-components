package de.freese.base.core.function;

/**
 * @author Thomas Freese
 * @since 03.01.2018
 */
@FunctionalInterface
public interface ExceptionHandler {
    void handle(Exception ex);
}
