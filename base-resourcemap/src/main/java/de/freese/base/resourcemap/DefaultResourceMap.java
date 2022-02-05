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

import de.freese.base.resourcemap.cache.ResourceMapCache;
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.provider.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis-Implementation of {@link ResourceMap}.
 *
 * @author Thomas Freese
 */
public class DefaultResourceMap implements ResourceMap
{
    /**
    *
    */
    private final String bundleName;
    /**
    *
    */
    private ResourceMapCache cache;
    /**
    *
    */
    private final List<DefaultResourceMap> childs = new ArrayList<>();
    /**
     *
     */
    private final Map<Class<?>, ResourceConverter<?>> converters = new HashMap<>();
    /**
     *
     */
    private Locale locale = Locale.getDefault();
    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
    *
    */
    private DefaultResourceMap parent;
    /**
    *
    */
    private final ResourceProvider resourceProvider;

    /**
    *
    */
    private final Map<Locale, Map<String, String>> resources = new HashMap<>();

    /**
     * Erstellt ein neues {@link DefaultResourceMap} Object.
     *
     * @param bundleName String
     * @param resourceProvider {@link ResourceProvider}
     * @param converters {@link Map}
     * @param cache {@link ResourceMapCache}
     */
    protected DefaultResourceMap(final String bundleName, final ResourceProvider resourceProvider, final Map<Class<?>, ResourceConverter<?>> converters,
            final ResourceMapCache cache)
    {
        super();

        this.bundleName = Objects.requireNonNull(bundleName, "bundleName required");
        this.resourceProvider = Objects.requireNonNull(resourceProvider, "resourceProvider required");
        this.converters.putAll(Objects.requireNonNull(converters, "converters required"));
        this.cache = Objects.requireNonNull(cache, "cache required");
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getBundleName()
     */
    @Override
    public String getBundleName()
    {
        return this.bundleName;
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getChild(java.lang.String)
     */
    @Override
    public ResourceMap getChild(final String bundleName)
    {
        if (getBundleName().equals(bundleName))
        {
            return this;
        }

        for (ResourceMap child : getChilds())
        {
            ResourceMap rm = child.getChild(bundleName);

            if (rm != null)
            {
                return rm;
            }
        }

        return null;
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getObject(java.lang.String, java.lang.Class)
     */
    @Override
    public final <T> T getObject(final String key, final Class<T> type)
    {
        Objects.requireNonNull(key, "key required");
        Objects.requireNonNull(type, "type required");

        T value;

        value = getCache().getValue(getBundleName(), getLocale(), type, key);

        if (value != null)
        {
            return value;
        }

        // Convert
        String stringValue = getResource(key);

        if (stringValue == null)
        {
            return null;
        }

        ResourceConverter<T> converter = getConverter(type);

        if (converter != null)
        {
            try
            {
                value = converter.convert(key, stringValue);
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }
        else
        {
            getLogger().warn("{}: No ResourceConverter found for type '{}' and key '{}'", getBundleName(), type.getSimpleName(), key);
        }

        if (value != null)
        {
            getCache().putValue(getBundleName(), getLocale(), type, key, value);
        }

        return value;
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getString(java.lang.String, java.lang.Object[])
     */
    @Override
    public final String getString(final String key, final Object...args)
    {
        String value = getResource(key);

        if (value == null)
        {
            return "#" + key;
        }

        try
        {
            if (args.length > 0)
            {
                if (value.indexOf("{0}") != -1)
                {
                    // The "old" Format.
                    value = MessageFormat.format(value, args);
                }
                else
                {
                    // The "new" Format.
                    value = String.format(value, args);
                }
            }

            return value;
        }
        catch (Exception ex)
        {
            getLogger().warn(null, ex);

            return "#" + key;
        }
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#load(java.util.Locale)
     */
    @Override
    public void load(final Locale locale)
    {
        this.locale = Objects.requireNonNull(locale, "locale required");

        if (this.resources.get(locale) != null)
        {
            return;
        }

        Map<String, String> resourcesLocale = getResourceProvider().getResources(getBundleName(), locale);

        this.resources.put(locale, resourcesLocale);

        substitutePlaceholder(resourcesLocale);

        getChilds().forEach(child -> child.load(locale));
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ResourceMap [bundleName=");
        builder.append(getBundleName());
        builder.append(", parent=");
        builder.append(getParent() == null ? "null" : getParent().getBundleName());
        builder.append("]");

        return builder.toString();
    }

    /**
     * @param child {@link DefaultResourceMap}
     */
    protected void addChild(final DefaultResourceMap child)
    {
        getChilds().add(child);
    }

    /**
     * @return {@link ResourceMapCache}
     */
    protected ResourceMapCache getCache()
    {
        return this.cache;
    }

    /**
     * @param type Class
     *
     * @return {@link ResourceConverter}
     */
    @SuppressWarnings("unchecked")
    protected <T> ResourceConverter<T> getConverter(final Class<T> type)
    {
        return (ResourceConverter<T>) this.converters.get(type);
    }

    /**
     * Liefert das {@link Locale}.
     *
     * @return {@link Locale}
     */
    protected Locale getLocale()
    {
        return this.locale;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link DefaultResourceMap}
     */
    protected DefaultResourceMap getParent()
    {
        return this.parent;
    }

    /**
     * @param key String
     *
     * @return String
     */
    protected String getResource(final String key)
    {
        String resource = this.resources.get(getLocale()).get(key);

        if ((resource == null) && (getParent() != null))
        {
            // Fallback: Parent lookup
            resource = getParent().getResource(key);
        }

        if (resource == null)
        {
            getLogger().warn("{}: no resource found for Locale '{}' and key '{}'", getBundleName(), getLocale(), key);
        }

        return resource;
    }

    /**
     * @return {@link ResourceProvider}
     */
    protected ResourceProvider getResourceProvider()
    {
        return this.resourceProvider;
    }

    /**
     * @param parent {@link DefaultResourceMap}
     */
    protected void setParent(final DefaultResourceMap parent)
    {
        this.parent = parent;
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
     *
     * @param resources {@link Map}
     */
    protected final void substitutePlaceholder(final Map<String, String> resources)
    {
        List<Entry<String, String>> entries = resources.entrySet().stream().filter(entry -> entry.getValue().contains("${")).collect(Collectors.toList());

        for (Iterator<Entry<String, String>> iterator = entries.iterator(); iterator.hasNext();)
        {
            Entry<String, String> entry = iterator.next();
            String expression = entry.getValue();

            List<String> keys = new ArrayList<>();
            int startIndex = 0;
            int lastEndIndex = 0;

            while ((startIndex = expression.indexOf("${", lastEndIndex)) != -1)
            {
                int endIndex = expression.indexOf('}', startIndex);

                if (endIndex != -1)
                {
                    String key = expression.substring(startIndex + 2, endIndex);
                    keys.add(key);

                    lastEndIndex = endIndex;
                }
            }

            for (String key : keys)
            {
                String value = getResource(key);

                if (value == null)
                {
                    continue;
                }

                expression = expression.replace("${" + key + "}", value);
            }

            resources.put(entry.getKey(), expression);

            iterator.remove();
        }
    }

    /**
     * @return {@link List}
     */
    List<DefaultResourceMap> getChilds()
    {
        return this.childs;
    }
}
