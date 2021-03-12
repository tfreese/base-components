/**
 * Created: 08.06.2020
 */

package de.freese.base.resourcemap.cache;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import de.freese.base.resourcemap.ResourceMap;

/**
 * Statischer {@link ResourceMapCache} für alle {@link ResourceMap}s.
 *
 * @author Thomas Freese
 */
public final class StaticResourceMapCache implements ResourceMapCache
{
    /**
     * @author Thomas Freese
     */
    private static class InstanceHolder
    {
        /**
         *
         */
        private static final ResourceMapCache INSTANCE = new StaticResourceMapCache();
    }

    /**
     * @return {@link ResourceMapCache}
     */
    public static ResourceMapCache getInstance()
    {
        return InstanceHolder.INSTANCE;
    }

    /**
     *
     */
    private final Map<String, Map<Locale, Map<Class<?>, Map<String, ?>>>> cache = new HashMap<>();

    /**
     * Erstellt ein neues {@link StaticResourceMapCache} Object.
     */
    private StaticResourceMapCache()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#clear(java.lang.String, java.util.Locale)
     */
    @Override
    public void clear(final String bundleName, final Locale locale)
    {
        Map<Locale, Map<Class<?>, Map<String, ?>>> byLocale = this.cache.computeIfAbsent(bundleName, k -> new HashMap<>());

        byLocale.remove(locale);
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#clearAll()
     */
    @Override
    public void clearAll()
    {
        this.cache.clear();
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#getValue(java.lang.String, java.util.Locale, java.lang.Class, java.lang.String)
     */
    @Override
    public <T> T getValue(final String bundleName, final Locale locale, final Class<T> type, final String key)
    {
        Map<String, T> byKey = getValues(bundleName, locale, type);

        return byKey.get(key);
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#getValues(java.lang.String, java.util.Locale, java.lang.Class)
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
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#putValue(java.lang.String, java.util.Locale, java.lang.Class, java.lang.String, java.lang.Object)
     */
    @Override
    public <T> void putValue(final String bundleName, final Locale locale, final Class<T> type, final String key, final T value)
    {
        Map<String, T> byKey = getValues(bundleName, locale, type);

        byKey.put(key, value);
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceMapCache#putValues(java.lang.String, java.util.Locale, java.lang.Class, java.util.Map)
     */
    @Override
    public <T> void putValues(final String bundleName, final Locale locale, final Class<T> type, final Map<String, T> values)
    {
        Map<Locale, Map<Class<?>, Map<String, ?>>> byLocale = this.cache.computeIfAbsent(bundleName, k -> new HashMap<>());
        Map<Class<?>, Map<String, ?>> byType = byLocale.computeIfAbsent(locale, k -> new HashMap<>());

        byType.put(type, values);
    }
}
