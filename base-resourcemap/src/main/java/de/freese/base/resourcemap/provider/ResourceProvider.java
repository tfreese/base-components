package de.freese.base.resourcemap.provider;

import java.util.Locale;
import java.util.Map;

/**
 * Ein {@link ResourceProvider} ist zuständig für das Laden der Resources.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ResourceProvider
{
    /**
     * Laden der Daten von Resourcen für die Internationalisierung.
     *
     * @param bundleName String
     * @param locale {@link Locale}
     *
     * @return {@link Map}
     */
    Map<String, String> getResources(String bundleName, Locale locale);
}
