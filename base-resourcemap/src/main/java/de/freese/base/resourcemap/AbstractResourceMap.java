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
 * Basis-Implementierung einer {@link ResourceMap} zum laden und verarbeiten lokalisierter Texte.
 *
 * @author Thomas Freese
 */
public abstract class AbstractResourceMap implements ResourceMap
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
    private final List<AbstractResourceMap> childs = new ArrayList<>();
    /**
     *
     */
    private final Map<Class<?>, ResourceConverter<?>> converters = new HashMap<>();
    /**
     * () -> getParent() != null ? ((AbstractResourceMap) getParent()).getLocale() : Locale.getDefault()
     */
    private Locale locale = Locale.getDefault();
    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
    *
    */
    private AbstractResourceMap parent;
    /**
    *
    */
    private ResourceProvider resourceProvider;

    /**
    *
    */
    private final Map<Locale, Map<String, String>> resources = new HashMap<>();

    /**
     * Erstellt ein neues {@link AbstractResourceMap} Object.
     *
     * @param bundleName String
     */
    protected AbstractResourceMap(final String bundleName)
    {
        super();

        this.bundleName = Objects.requireNonNull(bundleName, "bundleName required");
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

        // Konvert
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
                    // Das "alte" Format.
                    value = MessageFormat.format(value, args);
                }
                else
                {
                    // Das "neue" Format.
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
     * @param child {@link AbstractResourceMap}
     */
    protected void addChild(final AbstractResourceMap child)
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
     * @return {@link AbstractResourceMap}
     */
    protected AbstractResourceMap getParent()
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
            // Fallback: Im Parent suchen
            resource = getParent().getResource(key);
        }

        if (resource == null)
        {
            getLogger().warn("{}: no resource found for Locale '{}' and key '{}'", getBundleName(), getLocale(), key);
        }

        return resource;
    }

    /**
     * Liefert den verwendeten {@link ResourceProvider}.<br>
     * Sollte diese ResourceMap keinen ResourceProvider besitzen, wird der vorhandene Parent angefragt, wenn vorhanden.
     *
     * @return {@link ResourceProvider}
     */
    protected ResourceProvider getResourceProvider()
    {
        if ((this.resourceProvider == null) && (getParent() != null))
        {
            return getParent().getResourceProvider();
        }

        return this.resourceProvider;
    }

    /**
     * @param cache {@link ResourceMapCache}
     */
    protected void setCache(final ResourceMapCache cache)
    {
        this.cache = cache;
    }

    /**
     * @param converters {@link Map}
     */
    protected void setConverters(final Map<Class<?>, ResourceConverter<?>> converters)
    {
        this.converters.putAll(converters);
    }

    /**
     * @param parent {@link AbstractResourceMap}
     */
    protected void setParent(final AbstractResourceMap parent)
    {
        this.parent = parent;
    }

    /**
     * @param resourceProvider {@link ResourceProvider}
     */
    protected void setResourceProvider(final ResourceProvider resourceProvider)
    {
        this.resourceProvider = resourceProvider;
    }

    /**
     * Gegeben sind die folgenden Resourcen:
     *
     * <pre>
     *  hello = Hello
     *  world = World
     *  place = ${hello} ${world}
     * </pre>
     *
     * Der Wert von "place" wäre "Hello World".<br>
     * Der Wert von einem ${null} ist null.
     *
     * @param resources {@link Map}
     */
    protected final void substitutePlaceholder(final Map<String, String> resources)
    {
        // Finde alle Values mit '${' Expression.
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

            // Expressions ersetzen.
            for (String key : keys)
            {
                String value = getResource(key);

                if (value == null)
                {
                    continue;
                }

                expression = expression.replace("${" + key + "}", value);
            }

            // Aufgelöste Expression wieder in die Map stecken.
            resources.put(entry.getKey(), expression);

            iterator.remove();
        }
    }

    /**
     * @return {@link List}
     */
    List<AbstractResourceMap> getChilds()
    {
        return this.childs;
    }
}
