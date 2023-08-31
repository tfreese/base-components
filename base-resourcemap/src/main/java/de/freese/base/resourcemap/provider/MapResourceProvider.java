// Created: 05.02.2022
package de.freese.base.resourcemap.provider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Thomas Freese
 */
public final class MapResourceProvider implements ResourceProvider {
    private final Map<Locale, Map<String, String>> mapLocale = new HashMap<>();

    @Override
    public Map<String, String> getResources(final String bundleName, final Locale locale) {
        return this.mapLocale.computeIfAbsent(locale, k -> new HashMap<>());
    }

    public MapResourceProvider put(final Locale locale, final Map<String, String> resources) {
        this.mapLocale.computeIfAbsent(locale, k -> new HashMap<>()).putAll(resources);

        return this;
    }

    public MapResourceProvider put(final Locale locale, final String key, final String value) {
        this.mapLocale.computeIfAbsent(locale, k -> new HashMap<>()).put(key, value);

        return this;
    }

    public MapResourceProvider put(final Locale locale, Consumer<Map<String, String>> mapConsumer) {
        Map<String, String> map = this.mapLocale.computeIfAbsent(locale, k -> new HashMap<>());

        mapConsumer.accept(map);

        return this;
    }
}
