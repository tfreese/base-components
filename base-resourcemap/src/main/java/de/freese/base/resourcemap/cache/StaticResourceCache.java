// Created: 08.06.2020
package de.freese.base.resourcemap.cache;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.freese.base.resourcemap.ResourceMap;

/**
 * Static {@link ResourceCache} for all {@link ResourceMap}s.
 *
 * @author Thomas Freese
 */
public final class StaticResourceCache implements ResourceCache
{
    /**
     * @author Thomas Freese
     */
    private static class InstanceHolder
    {
        private static final ResourceCache INSTANCE = new StaticResourceCache();
    }

    public static ResourceCache getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    private final Map<String, Map<Locale, Map<Class<?>, Map<String, ?>>>> cache = new HashMap<>();

    private StaticResourceCache()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#clear(java.lang.String, java.util.Locale)
     */
    @Override
    public void clear(final String bundleName, final Locale locale)
    {
        Map<Locale, Map<Class<?>, Map<String, ?>>> byLocale = this.cache.computeIfAbsent(bundleName, k -> new HashMap<>());

        byLocale.remove(locale);
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#clearAll()
     */
    @Override
    public void clearAll()
    {
        this.cache.clear();
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#getValue(java.lang.String, java.util.Locale, java.lang.Class, java.lang.String)
     */
    @Override
    public <T> T getValue(final String bundleName, final Locale locale, final Class<T> type, final String key)
    {
        Map<String, T> byKey = getValues(bundleName, locale, type);

        return byKey.get(key);
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#getValues(java.lang.String, java.util.Locale, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getValues(final String bundleName, final Locale locale, final Class<T> type)
    {
        Map<Locale, Map<Class<?>, Map<String, ?>>> byLocale = this.cache.computeIfAbsent(bundleName, k -> new HashMap<>());
        Map<Class<?>, Map<String, ?>> byType = byLocale.computeIfAbsent(locale, k -> new HashMap<>());
        Map<String, ?> byKey = byType.computeIfAbsent(type, k -> new HashMap<>());

        return (Map<String, T>) byKey;
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#putValue(java.lang.String, java.util.Locale, java.lang.Class, java.lang.String, java.lang.Object)
     */
    @Override
    public <T> void putValue(final String bundleName, final Locale locale, final Class<T> type, final String key, final T value)
    {
        Map<String, T> byKey = getValues(bundleName, locale, type);

        byKey.put(key, value);
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#putValues(java.lang.String, java.util.Locale, java.lang.Class, java.util.Map)
     */
    @Override
    public <T> void putValues(final String bundleName, final Locale locale, final Class<T> type, final Map<String, T> values)
    {
        Map<Locale, Map<Class<?>, Map<String, ?>>> byLocale = this.cache.computeIfAbsent(bundleName, k -> new HashMap<>());
        Map<Class<?>, Map<String, ?>> byType = byLocale.computeIfAbsent(locale, k -> new HashMap<>());

        byType.put(type, values);
    }
}
