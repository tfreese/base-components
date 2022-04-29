// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
public interface Logger
{
    /**
     * @param message String
     */
    void debug(String message);

    /**
     * @param format String
     * @param args Object[]
     *
     * @see String#format
     */
    default void debug(String format, Object... args)
    {
        if (isDebugEnabled())
        {
            debug(String.format(format, args));
        }
    }

    /**
     * @param message String
     */
    void error(String message);

    /**
     * @param message String
     * @param error {@link java.lang.Throwable}
     */
    void error(String message, Throwable error);

    /**
     * @param format String
     * @param error {@link java.lang.Throwable}
     * @param args Object[]
     *
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
     * @param format String
     * @param args Object[]
     *
     * @see String#format
     */
    default void error(String format, Object... args)
    {
        if (isErrorEnabled())
        {
            error(String.format(format, args));
        }
    }

    /**
     * @param message String
     */
    void info(String message);

    /**
     * @param format String
     * @param args Object[]
     *
     * @see String#format
     */
    default void info(String format, Object... args)
    {
        if (isInfoEnabled())
        {
            info(String.format(format, args));
        }
    }

    /**
     * @return boolean
     */
    boolean isDebugEnabled();

    /**
     * @return boolean
     */
    boolean isErrorEnabled();

    /**
     * @return boolean
     */
    boolean isInfoEnabled();
}
