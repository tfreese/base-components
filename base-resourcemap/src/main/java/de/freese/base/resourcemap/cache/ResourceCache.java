// Created: 08.06.2020
package de.freese.base.resourcemap.cache;

import java.util.Locale;
import java.util.Map;

import de.freese.base.resourcemap.ResourceMap;

/**
 * {@link ResourceCache} holds the created Objects of a {@link ResourceMap}.<br>
 *
 * @author Thomas Freese
 */
public interface ResourceCache {
    void clear(String bundleName, Locale locale);

    void clearAll();

    <T> T getValue(String bundleName, Locale locale, Class<T> type, String key);

    <T> Map<String, T> getValues(String bundleName, Locale locale, Class<T> type);

    <T> void putValue(String bundleName, Locale locale, Class<T> type, String key, T value);

    <T> void putValues(String bundleName, Locale locale, Class<T> type, Map<String, T> values);
}
