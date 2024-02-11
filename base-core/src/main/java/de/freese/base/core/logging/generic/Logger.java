// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
public interface Logger {
    void debug(String message);

    default void debug(final String format, final Object... args) {
        if (isDebugEnabled()) {
            debug(String.format(format, args));
        }
    }

    void error(String message);

    void error(String message, Throwable error);

    default void error(final String format, final Throwable error, final Object... args) {
        if (isErrorEnabled()) {
            error(String.format(format, args), error);
        }
    }

    default void error(final String format, final Object... args) {
        if (isErrorEnabled()) {
            error(String.format(format, args));
        }
    }

    void info(String message);

    default void info(final String format, final Object... args) {
        if (isInfoEnabled()) {
            info(String.format(format, args));
        }
    }

    boolean isDebugEnabled();

    boolean isErrorEnabled();

    boolean isInfoEnabled();
}
