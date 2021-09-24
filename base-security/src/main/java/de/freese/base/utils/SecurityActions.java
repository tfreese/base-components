// Created: 09.04.2020
package de.freese.base.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

/**
 * @author Thomas Freese
 */
public final class SecurityActions
{
    /**
     * @param action {@link PrivilegedAction}
     *
     * @return Object
     */
    public static <T> T doPrivileged(final PrivilegedAction<T> action)
    {
        if (System.getSecurityManager() == null)
        {
            return action.run();
        }

        return AccessController.doPrivileged(action);
    }

    /**
     * @param action {@link PrivilegedExceptionAction}
     *
     * @return Object
     *
     * @throws Exception Falls was schief geht.
     */
    public static <T> T doPrivileged(final PrivilegedExceptionAction<T> action) throws Exception
    {
        if (System.getSecurityManager() == null)
        {
            return action.run();
        }

        return AccessController.doPrivileged(action);
    }

    /**
     * @param key String
     *
     * @return String
     */
    public static String getSystemProperty(final String key)
    {
        return doPrivileged((PrivilegedAction<String>) () -> System.getProperty(key));
    }

    /**
     * @param key String
     * @param defaultValue String
     *
     * @return String
     */
    public static String getSystemProperty(final String key, final String defaultValue)
    {
        return doPrivileged((PrivilegedAction<String>) () -> System.getProperty(key, defaultValue));
    }

    /**
     * Erstellt ein neues {@link SecurityActions} Object.
     */
    private SecurityActions()
    {
        super();
    }
}
