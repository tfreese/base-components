// Created: 08.06.2020
package de.freese.base.resourcemap.cache;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Static and empty {@link ResourceCache}s
 *
 * @author Thomas Freese
 */
public final class NoOpResourceCache implements ResourceCache {
    /**
     * @author Thomas Freese
     */
    private static class InstanceHolder {
        private static final ResourceCache INSTANCE = new NoOpResourceCache();
    }

    public static ResourceCache getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private NoOpResourceCache() {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#clear(java.lang.String, java.util.Locale)
     */
    @Override
    public void clear(final String bundleName, final Locale locale) {
        // Empty
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#clearAll()
     */
    @Override
    public void clearAll() {
        // Empty
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#getValue(java.lang.String, java.util.Locale, java.lang.Class, java.lang.String)
     */
    @Override
    public <T> T getValue(final String bundleName, final Locale locale, final Class<T> type, final String key) {
        return null;
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#getValues(java.lang.String, java.util.Locale, java.lang.Class)
     */
    @Override
    public <T> Map<String, T> getValues(final String bundleName, final Locale locale, final Class<T> type) {
        return Collections.emptyMap();
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#putValue(java.lang.String, java.util.Locale, java.lang.Class, java.lang.String, java.lang.Object)
     */
    @Override
    public <T> void putValue(final String bundleName, final Locale locale, final Class<T> type, final String key, final T value) {
        // Empty
    }

    /**
     * @see de.freese.base.resourcemap.cache.ResourceCache#putValues(java.lang.String, java.util.Locale, java.lang.Class, java.util.Map)
     */
    @Override
    public <T> void putValues(final String bundleName, final Locale locale, final Class<T> type, final Map<String, T> values) {
        // Empty
    }
}
