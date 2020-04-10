/**
 *
 */
package de.freese.base.utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Nuetzliches fuer Exceptions.
 *
 * @author Thomas Freese
 */
public final class ExceptionUtils
{
    /**
     * Liefert den Cause der {@link InvocationTargetException}.<br>
     * Es wird als default nur 10 Hierarchien geprueft.
     * 
     * @param ite {@link InvocationTargetException}
     * @return {@link Throwable}, kann auch eine InvocationTargetException sein
     */
    public static Throwable getCause(final InvocationTargetException ite)
    {
        Throwable th = ite;
        int counter = 0;

        while (th instanceof InvocationTargetException)
        {
            th = (th.getCause() != null) ? th.getCause() : th;

            if (counter++ == 10)
            {
                // Rekursion vermeiden
                break;
            }
        }

        return th;
    }

    /**
     * Liefert den Cause der {@link RuntimeException}.<br>
     * Es wird als default nur 10 Hierarchien geprueft.
     * 
     * @param re {@link RuntimeException}
     * @return {@link Throwable}, kann auch eine RuntimeException sein
     */
    public static Throwable getCause(final RuntimeException re)
    {
        Throwable th = re;
        int counter = 0;

        while (th instanceof RuntimeException)
        {
            th = (th.getCause() != null) ? th.getCause() : th;

            if (counter++ == 10)
            {
                // Rekursion vermeiden
                break;
            }
        }

        return th;
    }

    /**
     * Liefert die enthaltene SQL-Exception oder wieder die Parameter-Exception.<br>
     * 
     * @param throwable {@link Throwable}
     * @return {@link Throwable}, kann auch null sein
     */
    public static Throwable getSQLException(final Throwable throwable)
    {
        if (throwable == null)
        {
            return null;
        }

        if (throwable instanceof SQLException)
        {
            return throwable;
        }

        Throwable th = throwable.getCause() != null ? throwable.getCause() : throwable;
        int counter = 0;

        while (!(th instanceof SQLException))
        {
            if (th == null)
            {
                break;
            }

            th = th.getCause();

            // Rekursion verhindern
            if (counter++ == 10)
            {
                break;
            }
        }

        return th;
    }

    /**
     * Erstellt ein neues {@link ExceptionUtils} Object.
     */
    private ExceptionUtils()
    {
        super();
    }
}
