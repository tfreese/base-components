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
    private static final class InstanceHolder {
        private static final ResourceCache INSTANCE = new NoOpResourceCache();
    }

    public static ResourceCache getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private NoOpResourceCache() {
        super();
    }

    @Override
    public void clear(final String bundleName, final Locale locale) {
        // Empty
    }

    @Override
    public void clearAll() {
        // Empty
    }

    @Override
    public <T> T getValue(final String bundleName, final Locale locale, final Class<T> type, final String key) {
        return null;
    }

    @Override
    public <T> Map<String, T> getValues(final String bundleName, final Locale locale, final Class<T> type) {
        return Collections.emptyMap();
    }

    @Override
    public <T> void putValue(final String bundleName, final Locale locale, final Class<T> type, final String key, final T value) {
        // Empty
    }

    @Override
    public <T> void putValues(final String bundleName, final Locale locale, final Class<T> type, final Map<String, T> values) {
        // Empty
    }
}
