package de.freese.base.core.i18n;

import java.util.Locale;
import java.util.Map;

/**
 * @author Thomas Freese
 * @since 30.09.26
 */
public interface ResourceProvider {
    Map<String, String> loadProperties(final String baseName, final Locale locale);

    boolean needsReload(final String baseName, final Locale locale, long loadTime);
}
