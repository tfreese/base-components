// Created: 08.06.2020
package de.freese.base.resourcemap.cache;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import de.freese.base.resourcemap.ResourceMap;

/**
 * Statischer {@link ResourceMapCache} für alle {@link ResourceMap}s ohne Funktion.
 *
 * @author Thomas Freese
 */
public final class NoOpResourceMapCache implements ResourceMapCache
{
    /**
     * @author Thomas Freese
     */
    private static class InstanceHolder
    {
        /**
         *
         */
        private static final ResourceMapCache INSTANCE = new NoOpResourceMapCache();
    }

    /**
     * @return {@link ResourceMapCache}
     */
    public static ResourceMapCache getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Erstellt ein neues {@link NoOpResourceMapCache} Object.
     */
    private NoOpResourceMapCache()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#clear(java.lang.String, java.util.Locale)
     */
    @Override
    public void clear(final String bundleName, final Locale locale)
    {
        // Empty
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#clearAll()
     */
    @Override
    public void clearAll()
    {
        // Empty
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#getValue(java.lang.String, java.util.Locale, java.lang.Class, java.lang.String)
     */
    @Override
    public <T> T getValue(final String bundleName, final Locale locale, final Class<T> type, final String key)
    {
        return null;
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#getValues(java.lang.String, java.util.Locale, java.lang.Class)
     */
    @Override
    public <T> Map<String, T> getValues(final String bundleName, final Locale locale, final Class<T> type)
    {
        return Collections.emptyMap();
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#putValue(java.lang.String, java.util.Locale, java.lang.Class, java.lang.String, java.lang.Object)
     */
    @Override
    public <T> void putValue(final String bundleName, final Locale locale, final Class<T> type, final String key, final T value)
    {
        // Empty
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#putValues(java.lang.String, java.util.Locale, java.lang.Class, java.util.Map)
     */
    @Override
    public <T> void putValues(final String bundleName, final Locale locale, final Class<T> type, final Map<String, T> values)
    {
        // Empty
    }
}
