package de.freese.base.resourcemap.provider;

import java.util.Locale;
import java.util.Map;

import de.freese.base.resourcemap.ResourceMap;

/**
 * A {@link ResourceProvider} can load the resources for a {@link ResourceMap}.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ResourceProvider
{
    /**
     * @param bundleName String
     * @param locale {@link Locale}
     *
     * @return {@link Map}
     */
    Map<String, String> getResources(String bundleName, Locale locale);
}
