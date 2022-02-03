// Created: 08.06.2020
package de.freese.base.resourcemap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.freese.base.resourcemap.cache.ResourceMapCache;
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
    private final List<ResourceMap> childs = new ArrayList<>();
    /**
    *
    */
    private Supplier<Locale> localeSupplier = () -> getParent() != null ? ((AbstractResourceMap) getParent()).getLocale() : Locale.getDefault();
    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
    *
    */
    private ResourceMap parent;
    /**
    *
    */
    private ResourceProvider resourceProvider;

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

        for (ResourceMap child : this.childs)
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
     * @see de.freese.base.resourcemap.ResourceMap#setLocaleSupplier(java.util.function.Supplier)
     */
    @Override
    public void setLocaleSupplier(final Supplier<Locale> localeSupplier)
    {
        this.localeSupplier = Objects.requireNonNull(localeSupplier, "localeSupplier required");
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
     * @param child {@link ResourceMap}
     */
    protected void addChild(final ResourceMap child)
    {
        this.childs.add(child);
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
     * @param locale {@link Locale}
     * @param resources {@link Map}
     */
    protected final void evaluateStringExpressions(final Locale locale, final Map<String, String> resources)
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
                String value = getObject(key, String.class);

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
     * @return {@link ResourceMapCache}
     */
    protected ResourceMapCache getCache()
    {
        return this.cache;
    }

    /**
     * Liefert das {@link Locale}.
     *
     * @return {@link Locale}
     */
    protected Locale getLocale()
    {
        return this.localeSupplier.get();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link ResourceMap}
     */
    protected ResourceMap getParent()
    {
        return this.parent;
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
            return ((AbstractResourceMap) getParent()).getResourceProvider();
        }

        return this.resourceProvider;
    }

    /**
     * Liefert das bereits geparste Objekt oder null.
     *
     * @param locale {@link Locale}
     * @param key String
     * @param type Class
     *
     * @return Object
     */
    protected <T> T getValue(final Locale locale, final Class<T> type, final String key)
    {
        loadResourcesIfAbsent(locale);

        return getCache().getValue(getBundleName(), locale, type, key);
    }

    /**
     * @param locale {@link Locale}
     *
     * @return Map<String,String>
     */
    protected Map<String, String> loadResourcesIfAbsent(final Locale locale)
    {
        Map<String, String> resources = getCache().getValues(getBundleName(), locale, String.class);

        if ((resources == null) || resources.isEmpty())
        {
            resources = getResourceProvider().getResources(getBundleName(), locale);

            getCache().putValues(getBundleName(), locale, String.class, resources);

            evaluateStringExpressions(locale, resources);
        }

        return resources;
    }

    /**
     * Setzt das bereits geparste Objekt in den Cache.
     *
     * @param locale {@link Locale}
     * @param key String
     * @param type Class
     * @param value Object
     */
    protected <T> void putValue(final Locale locale, final Class<T> type, final String key, final T value)
    {
        getCache().putValue(getBundleName(), locale, type, key, value);
    }

    /**
     * @param cache {@link ResourceMapCache}
     */
    protected void setCache(final ResourceMapCache cache)
    {
        this.cache = cache;
    }

    /**
     * @param parent {@link ResourceMap}
     */
    protected void setParent(final ResourceMap parent)
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
}
