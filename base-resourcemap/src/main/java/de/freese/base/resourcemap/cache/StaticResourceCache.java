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
public final class StaticResourceCache implements ResourceCache {
    /**
     * @author Thomas Freese
     */
    private static final class InstanceHolder {
        private static final ResourceCache INSTANCE = new StaticResourceCache();
    }

    public static ResourceCache getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private final Map<String, Map<Locale, Map<Class<?>, Map<String, ?>>>> cache = new HashMap<>();

    private StaticResourceCache() {
        super();
    }

    @Override
    public void clear(final String bundleName, final Locale locale) {
        final Map<Locale, Map<Class<?>, Map<String, ?>>> byLocale = cache.computeIfAbsent(bundleName, k -> new HashMap<>());

        byLocale.remove(locale);
    }

    @Override
    public void clearAll() {
        cache.clear();
    }

    @Override
    public <T> T getValue(final String bundleName, final Locale locale, final Class<T> type, final String key) {
        final Map<String, T> byKey = getValues(bundleName, locale, type);

        return byKey.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getValues(final String bundleName, final Locale locale, final Class<T> type) {
        final Map<Locale, Map<Class<?>, Map<String, ?>>> byLocale = cache.computeIfAbsent(bundleName, k -> new HashMap<>());
        final Map<Class<?>, Map<String, ?>> byType = byLocale.computeIfAbsent(locale, k -> new HashMap<>());
        final Map<String, ?> byKey = byType.computeIfAbsent(type, k -> new HashMap<>());

        return (Map<String, T>) byKey;
    }

    @Override
    public <T> void putValue(final String bundleName, final Locale locale, final Class<T> type, final String key, final T value) {
        final Map<String, T> byKey = getValues(bundleName, locale, type);

        byKey.put(key, value);
    }

    @Override
    public <T> void putValues(final String bundleName, final Locale locale, final Class<T> type, final Map<String, T> values) {
        final Map<Locale, Map<Class<?>, Map<String, ?>>> byLocale = cache.computeIfAbsent(bundleName, k -> new HashMap<>());
        final Map<Class<?>, Map<String, ?>> byType = byLocale.computeIfAbsent(locale, k -> new HashMap<>());

        byType.put(type, values);
    }
}
