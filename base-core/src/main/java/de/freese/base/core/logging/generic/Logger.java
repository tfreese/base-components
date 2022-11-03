// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
public interface Logger
{
    void debug(String message);

    /**
     * @see String#format
     */
    default void debug(String format, Object... args)
    {
        if (isDebugEnabled())
        {
            debug(String.format(format, args));
        }
    }

    void error(String message);

    void error(String message, Throwable error);

    /**
     * @see String#format
     */
    default void error(String format, Throwable error, Object... args)
    {
        if (isErrorEnabled())
        {
            error(String.format(format, args), error);
        }
    }

    /**
     * @see String#format
     */
    default void error(String format, Object... args)
    {
        if (isErrorEnabled())
        {
            error(String.format(format, args));
        }
    }

    void info(String message);

    /**
     * @see String#format
     */
    default void info(String format, Object... args)
    {
        if (isInfoEnabled())
        {
            info(String.format(format, args));
        }
    }

    boolean isDebugEnabled();

    boolean isErrorEnabled();

    boolean isInfoEnabled();
}
