/**
 * Created: 08.06.2020
 */

package de.freese.base.resourcemap.cache;

import java.util.Locale;
import java.util.Map;
import de.freese.base.resourcemap.ResourceMap;

/**
 * Interface der Cache-Implementierung der {@link ResourceMap}.
 *
 * @author Thomas Freese
 */
public interface ResourceMapCache
{
    /**
     * @param bundleName String
     * @param locale {@link Locale}
     */
    public void clear(String bundleName, Locale locale);

    /**
    *
    */
    public void clearAll();

    /**
     * @param bundleName String
     * @param locale {@link Locale}
     * @param type Class
     * @param key String
     * @return Object
     */
    public <T> T getValue(String bundleName, Locale locale, Class<T> type, String key);

    /**
     * @param bundleName String
     * @param locale {@link Locale}
     * @param type Class
     * @return {@link Map}
     */
    public <T> Map<String, T> getValues(String bundleName, Locale locale, Class<T> type);

    /**
     * @param bundleName String
     * @param locale {@link Locale}
     * @param type Class
     * @param key String
     * @param value Object
     */
    public <T> void putValue(String bundleName, Locale locale, Class<T> type, String key, T value);

    /**
     * @param bundleName String
     * @param locale {@link Locale}
     * @param type Class
     * @param values {@link Map}
     */
    public <T> void putValues(String bundleName, Locale locale, Class<T> type, Map<String, T> values);
}
