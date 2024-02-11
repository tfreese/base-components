// Created: 08.06.2020
package de.freese.base.resourcemap.cache;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.freese.base.resourcemap.ResourceMap;

/**
 * {@link ResourceCache} for a single {@link ResourceMap}.<br>
 * The bundleName-Parameter is ignored.
 *
 * @author Thomas Freese
 */
public class SingleResourceCache implements ResourceCache {
    private final Map<Locale, Map<Class<?>, Map<String, ?>>> cache = new HashMap<>();

    @Override
    public void clear(final String bundleName, final Locale locale) {
        this.cache.remove(locale);
    }

    @Override
    public void clearAll() {
        this.cache.clear();
    }

    @Override
    public <T> T getValue(final String bundleName, final Locale locale, final Class<T> type, final String key) {
        final Map<String, T> byKey = getValues(bundleName, locale, type);

        return byKey.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getValues(final String bundleName, final Locale locale, final Class<T> type) {
        final Map<Class<?>, Map<String, ?>> byType = this.cache.computeIfAbsent(locale, k -> new HashMap<>());
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
        final Map<Class<?>, Map<String, ?>> byType = this.cache.computeIfAbsent(locale, k -> new HashMap<>());

        byType.put(type, values);
    }
}
