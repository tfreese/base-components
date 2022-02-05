// Created: 05.02.2022
package de.freese.base.resourcemap.provider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public final class MapResourceProvider implements ResourceProvider
{
    /**
     *
     */
    private final Map<Locale, Map<String, String>> mapLocale = new HashMap<>();

    /**
     * @see de.freese.base.resourcemap.provider.ResourceProvider#getResources(java.lang.String, java.util.Locale)
     */
    @Override
    public Map<String, String> getResources(final String bundleName, final Locale locale)
    {
        return this.mapLocale.computeIfAbsent(locale, k -> new HashMap<>());
    }

    /**
     * @param locale @param locale {@link Locale}
     * @param resources {@link Map}
     *
     * @return {@link MapResourceProvider}
     */
    public MapResourceProvider put(final Locale locale, final Map<String, String> resources)
    {
        this.mapLocale.computeIfAbsent(locale, k -> new HashMap<>()).putAll(resources);

        return this;
    }

    /**
     * @param locale {@link Locale}
     * @param key String
     * @param value String
     *
     * @return {@link MapResourceProvider}
     */
    public MapResourceProvider put(final Locale locale, final String key, final String value)
    {
        this.mapLocale.computeIfAbsent(locale, k -> new HashMap<>()).put(key, value);

        return this;
    }
}
