package de.freese.base.core.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Default: Locale.ROOT > Locale.ENGLISH
 *
 * @author Thomas Freese
 * @since 04.08.26
 */
public final class HybridResourceBundleControl extends ResourceBundle.Control {
    public enum Priority {
        /**
         * Provider überschreibt Property.
         */
        PROVIDER_OVERRIDES,

        /**
         * Property überschreibt Provider.
         */
        PROPERTY_OVERRIDES
    }

    private final Priority priority;
    private final ResourceProvider resourceProvider;
    private final long ttlMillis;

    /**
     * @param ttlMillis; 1_000L * 3600L (1h), or ResourceBundle.Control.TTL_DONT_CACHE / ResourceBundle.Control.TTL_NO_EXPIRATION_CONTROL
     */
    public HybridResourceBundleControl(final ResourceProvider resourceProvider, final Priority priority, final long ttlMillis) {
        super();

        this.resourceProvider = Objects.requireNonNull(resourceProvider, "resourceProvider required");
        this.priority = Objects.requireNonNull(priority, "priority required");
        this.ttlMillis = ttlMillis;
    }

    /**
     * A single, self-defined format – we handle the entire loading process.
     */
    @Override
    public List<String> getFormats(final String baseName) {
        return List.of("hybrid");
    }

    @Override
    public long getTimeToLive(final String baseName, final Locale locale) {
        return ttlMillis;
    }

    @Override
    public boolean needsReload(final String baseName, final Locale locale, final String format,
                               final ClassLoader loader, final ResourceBundle bundle, final long loadTime) {
        // return resourceProvider.needsReload(baseName, Locale.ROOT.equals(locale) ? Locale.ENGLISH : locale, loadTime);
        return resourceProvider.needsReload(baseName, locale, loadTime);
    }

    @Override
    public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IOException {
        if (!"hybrid".equals(format)) {
            return null;
        }

        final Map<String, String> fromProps = loadProperties(baseName, locale, loader, reload);
        final Map<String, String> fromProvider = loadProvider(baseName, locale);

        // No Properties for this Locale > return null to trigger the Fallback-Chain.
        if (fromProps.isEmpty() && fromProvider.isEmpty()) {
            return null;
        }

        final Map<String, String> merged = new HashMap<>();

        if (priority == Priority.PROVIDER_OVERRIDES) {
            merged.putAll(fromProps);

            // Provider wins.
            merged.putAll(fromProvider);
        }
        else {
            merged.putAll(fromProvider);

            // Property wins.
            merged.putAll(fromProps);
        }

        return new MapResourceBundle(merged);
    }

    private Map<String, String> loadProperties(final String baseName, final Locale locale, final ClassLoader loader, final boolean reload) throws IOException {
        final String bundleName = toBundleName(baseName, locale);
        final String resourceName = toResourceName(bundleName, "properties");
        final Map<String, String> map = new HashMap<>();

        try (InputStream inputStream = openStream(resourceName, loader, reload)) {
            if (inputStream != null) {
                final Properties props = new Properties();

                try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    props.load(reader);
                }

                for (final String name : props.stringPropertyNames()) {
                    map.put(name, props.getProperty(name));
                }
            }
        }

        // Load Default-Properties with Locale.ROOT.
        if (map.isEmpty() && !Locale.ROOT.equals(locale)) {
            map.putAll(loadProperties(baseName, Locale.ROOT, loader, reload));
        }

        return map;
    }

    private Map<String, String> loadProvider(final String baseName, final Locale locale) {
        // return resourceProvider.loadProperties(baseName, Locale.ROOT.equals(locale) ? Locale.ENGLISH : locale);
        return resourceProvider.loadProperties(baseName, locale);
    }

    /**
     * Cache-Bypass if reload=true (analogous to the Default-Control).
     */
    private InputStream openStream(final String resourceName, final ClassLoader loader, final boolean reload) throws IOException {
        if (!reload) {
            return loader.getResourceAsStream(resourceName);
        }

        final URL url = loader.getResource(resourceName);

        if (url == null) {
            return null;
        }

        final URLConnection urlConnection = url.openConnection();
        urlConnection.setUseCaches(false);

        return urlConnection.getInputStream();
    }
}
