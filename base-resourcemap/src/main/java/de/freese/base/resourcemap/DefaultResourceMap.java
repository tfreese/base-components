// Created: 08.06.2020
package de.freese.base.resourcemap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.resourcemap.cache.ResourceCache;
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.converter.ResourceConverters;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * Basis-Implementation of {@link ResourceMap}.<br>
 * Configuration Example:
 *
 * <pre>
 * <code>
 * ResourceMap rootMap = ResourceMapBuilder.create()
 *      .resourceProvider(new ResourceBundleProvider())
 *      .converter(MyClass.class, new MyClassResourceConverter())
 *      .cacheDisabled()
 *      .bundleName("parentTest")
 *      .addChild()
 *          .bundleName("bundles/test1")
 *          .addChild()
 *              .bundleName("bundles/test2")
 *              .cacheDisabled()
 *              .done()
 *          .done()
 *      .build();
 * </code>
 * </pre>
 *
 * @author Thomas Freese
 */
class DefaultResourceMap implements ResourceMap {
    private final String bundleName;
    private final List<DefaultResourceMap> children = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<Locale, Map<String, String>> resources = new HashMap<>();

    private Locale locale;
    private DefaultResourceMap parent;
    private ResourceCache resourceCache;
    private ResourceConverters resourceConverters;
    private ResourceProvider resourceProvider;

    DefaultResourceMap(final String bundleName) {
        super();

        this.bundleName = Objects.requireNonNull(bundleName, "bundleName required");
    }

    @Override
    public String getBundleName() {
        return this.bundleName;
    }

    @Override
    public ResourceMap getChild(final String bundleName) {
        if (getBundleName().equals(bundleName)) {
            return this;
        }

        for (ResourceMap child : getChildren()) {
            final ResourceMap rm = child.getChild(bundleName);

            if (rm != null) {
                return rm;
            }
        }

        return null;
    }

    @Override
    public final <T> T getObject(final String key, final Class<T> type) {
        Objects.requireNonNull(key, "key required");
        Objects.requireNonNull(type, "type required");

        T value = getResourceCache().getValue(getBundleName(), getLocale(), type, key);

        if (value != null) {
            return value;
        }

        final String stringValue = getResource(key);

        if (stringValue == null) {
            return null;
        }

        final ResourceConverter<T> converter = getConverter(type);

        if (converter != null) {
            try {
                value = converter.convert(key, stringValue);
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        }
        else {
            getLogger().warn("{}: No ResourceConverter found for type '{}' and key '{}'", getBundleName(), type.getSimpleName(), key);
        }

        if (value != null) {
            getResourceCache().putValue(getBundleName(), getLocale(), type, key, value);
        }

        return value;
    }

    @Override
    public final String getString(final String key, final Object... args) {
        String value = getResource(key);

        if (value == null) {
            return "#" + key;
        }

        try {
            if (args.length > 0) {
                if (value.contains("{0}")) {
                    // The "old" Format.
                    value = MessageFormat.format(value, args);
                }
                else {
                    // The "new" Format.
                    value = String.format(value, args);
                }
            }

            return value;
        }
        catch (Exception ex) {
            getLogger().warn(null, ex);

            return "#" + key;
        }
    }

    @Override
    public void load(final Locale locale) {
        this.locale = Objects.requireNonNull(locale, "locale required");

        if (this.resources.get(locale) != null) {
            return;
        }

        final Map<String, String> resourcesLocale = getResourceProvider().getResources(getBundleName(), locale);

        this.resources.put(locale, resourcesLocale);

        substitutePlaceholder(resourcesLocale);

        getChildren().forEach(child -> child.load(locale));
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ResourceMap [bundleName=");
        builder.append(getBundleName());
        builder.append(", parent=");
        builder.append(getParent() == null ? "null" : getParent().getBundleName());
        builder.append("]");

        return builder.toString();
    }

    void addChild(final DefaultResourceMap child) {
        getChildren().add(Objects.requireNonNull(child, "child required"));
    }

    List<DefaultResourceMap> getChildren() {
        return this.children;
    }

    void setParent(final DefaultResourceMap parent) {
        this.parent = parent;
    }

    void setResourceCache(final ResourceCache resourceCache) {
        this.resourceCache = Objects.requireNonNull(resourceCache, "resourceCache required");
    }

    void setResourceConverters(final ResourceConverters resourceConverters) {
        this.resourceConverters = resourceConverters;
    }

    void setResourceProvider(final ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    protected <T> ResourceConverter<T> getConverter(final Class<T> type) {
        if (this.resourceConverters == null) {
            return getParent().getConverter(type);
        }

        ResourceConverter<T> converter = this.resourceConverters.getConverter(type);

        if (converter == null) {
            converter = getParent().getConverter(type);
        }

        return converter;
    }

    protected Locale getLocale() {
        return this.locale;
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected DefaultResourceMap getParent() {
        return this.parent;
    }

    protected String getResource(final String key) {
        String resource = this.resources.get(getLocale()).get(key);

        if ((resource == null) && (getParent() != null)) {
            resource = getParent().getResource(key);
        }

        if (resource == null) {
            getLogger().warn("{}: no resource found for Locale '{}' and key '{}'", getBundleName(), getLocale(), key);
        }

        return resource;
    }

    protected ResourceCache getResourceCache() {
        return this.resourceCache;
    }

    protected ResourceProvider getResourceProvider() {
        if (this.resourceProvider == null) {
            return getParent().getResourceProvider();
        }

        return this.resourceProvider;
    }

    /**
     * Replace the placeholders:
     *
     * <pre>
     *  hello = Hello
     *  world = World
     *  place = ${hello} ${world}
     * </pre>
     *
     * Value of ${null} is null.
     */
    protected final void substitutePlaceholder(final Map<String, String> resources) {
        final List<Entry<String, String>> entries = resources.entrySet().stream().filter(entry -> entry.getValue().contains("${")).collect(Collectors.toList());

        for (Iterator<Entry<String, String>> iterator = entries.iterator(); iterator.hasNext(); ) {
            final Entry<String, String> entry = iterator.next();
            String expression = entry.getValue();

            final List<String> keys = new ArrayList<>();
            int startIndex = 0;
            int lastEndIndex = 0;

            while ((startIndex = expression.indexOf("${", lastEndIndex)) != -1) {
                final int endIndex = expression.indexOf('}', startIndex);

                if (endIndex != -1) {
                    final String key = expression.substring(startIndex + 2, endIndex);
                    keys.add(key);

                    lastEndIndex = endIndex;
                }
            }

            for (String key : keys) {
                final String value = getResource(key);

                if (value == null) {
                    continue;
                }

                expression = expression.replace("${" + key + "}", value);
            }

            resources.put(entry.getKey(), expression);

            iterator.remove();
        }
    }
}
