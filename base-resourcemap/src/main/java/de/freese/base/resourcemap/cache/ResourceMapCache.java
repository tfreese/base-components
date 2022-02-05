// Created: 08.06.2020
package de.freese.base.resourcemap.cache;

import java.util.Locale;
import java.util.Map;

import de.freese.base.resourcemap.ResourceMap;

/**
 * {@link ResourceMapCache} holds the created Objects of a {@link ResourceMap}.<br>
 *
 * @see ResourceMap#getObject(String, Class)
 *
 * @author Thomas Freese
 */
public interface ResourceMapCache
{
    /**
     * @param bundleName String
     * @param locale {@link Locale}
     */
    void clear(String bundleName, Locale locale);

    /**
    *
    */
    void clearAll();

    /**
     * @param bundleName String
     * @param locale {@link Locale}
     * @param type Class
     * @param key String
     *
     * @return Object
     */
    <T> T getValue(String bundleName, Locale locale, Class<T> type, String key);

    /**
     * @param bundleName String
     * @param locale {@link Locale}
     * @param type Class
     *
     * @return {@link Map}
     */
    <T> Map<String, T> getValues(String bundleName, Locale locale, Class<T> type);

    /**
     * @param bundleName String
     * @param locale {@link Locale}
     * @param type Class
     * @param key String
     * @param value Object
     */
    <T> void putValue(String bundleName, Locale locale, Class<T> type, String key, T value);

    /**
     * @param bundleName String
     * @param locale {@link Locale}
     * @param type Class
     * @param values {@link Map}
     */
    <T> void putValues(String bundleName, Locale locale, Class<T> type, Map<String, T> values);
}
