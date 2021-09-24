// Created: 08.06.2020
package de.freese.base.resourcemap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.resourcemap.cache.ResourceMapCache;
import de.freese.base.resourcemap.converter.BooleanStringResourceConverter;
import de.freese.base.resourcemap.converter.ByteStringResourceConverter;
import de.freese.base.resourcemap.converter.ColorStringResourceConverter;
import de.freese.base.resourcemap.converter.DimensionStringResourceConverter;
import de.freese.base.resourcemap.converter.DoubleStringResourceConverter;
import de.freese.base.resourcemap.converter.EmptyBorderStringResourceConverter;
import de.freese.base.resourcemap.converter.FloatStringResourceConverter;
import de.freese.base.resourcemap.converter.FontStringResourceConverter;
import de.freese.base.resourcemap.converter.IconStringResourceConverter;
import de.freese.base.resourcemap.converter.ImageStringResourceConverter;
import de.freese.base.resourcemap.converter.InsetsStringResourceConverter;
import de.freese.base.resourcemap.converter.IntegerStringResourceConverter;
import de.freese.base.resourcemap.converter.KeyStrokeStringResourceConverter;
import de.freese.base.resourcemap.converter.LongStringResourceConverter;
import de.freese.base.resourcemap.converter.PointStringResourceConverter;
import de.freese.base.resourcemap.converter.RectangleStringResourceConverter;
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.converter.ShortStringResourceConverter;
import de.freese.base.resourcemap.converter.URIStringResourceConverter;
import de.freese.base.resourcemap.converter.URLStringResourceConverter;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * Bsis-Implementierung einer {@link ResourceMap} zum laden und verarbeiten lokalisierter Texte.
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
    private final Map<Class<?>, ResourceConverter<?>> resourceConverters = new HashMap<>();
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
     * @param supportedType Class
     * @param converter {@link ResourceConverter}
     */
    protected void addResourceConverter(final Class<?> supportedType, final ResourceConverter<?> converter)
    {
        this.resourceConverters.put(supportedType, converter);
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
     * @see de.freese.base.resourcemap.ResourceMap#getBundleName()
     */
    @Override
    public String getBundleName()
    {
        return this.bundleName;
    }

    /**
     * @return {@link ResourceMapCache}
     */
    protected ResourceMapCache getCache()
    {
        return this.cache;
    }

    // /**
    // * @see de.freese.base.resourcemap.ResourceMap#getKeys()
    // */
    // @Override
    // public final Set<String> getKeys()
    // {
    // Locale locale = getLocale();
    //
    // loadResourcesIfAbsent(locale);
    //
    // Map<String, String> resources = getCache().getValues(getBundleName(), locale, String.class);
    //
    // if (resources == null)
    // {
    // return Collections.emptySet();
    // }
    //
    // return resources.keySet();
    // }

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
     * Liefert den {@link ResourceConverter} für den Typ.<br>
     * Sollte diese ResourceMap keinen passenden Converter besitzen, wird der Parent angefragt, wenn vorhanden.
     *
     * @param supportedType {@link Class}
     *
     * @return {@link ResourceConverter}
     */
    @SuppressWarnings("unchecked")
    protected <T> ResourceConverter<T> getResourceConverter(final Class<T> supportedType)
    {
        ResourceConverter<T> converter = (ResourceConverter<T>) this.resourceConverters.get(supportedType);

        if ((converter == null) && (getParent() != null))
        {
            converter = ((AbstractResourceMap) getParent()).getResourceConverter(supportedType);
        }

        return converter;
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
     *
     */
    protected void initDefaultConverter()
    {
        addResourceConverter(Boolean.class, new BooleanStringResourceConverter("true", "on", "yes", "1"));
        addResourceConverter(boolean.class, new BooleanStringResourceConverter("true", "on", "yes", "1"));
        addResourceConverter(Byte.class, new ByteStringResourceConverter());
        addResourceConverter(byte.class, getResourceConverter(Byte.class));

        addResourceConverter(Color.class, new ColorStringResourceConverter());

        addResourceConverter(Dimension.class, new DimensionStringResourceConverter());
        addResourceConverter(Double.class, new DoubleStringResourceConverter());
        addResourceConverter(double.class, getResourceConverter(Double.class));

        addResourceConverter(EmptyBorder.class, new EmptyBorderStringResourceConverter());

        addResourceConverter(Float.class, new FloatStringResourceConverter());
        addResourceConverter(float.class, getResourceConverter(Float.class));
        addResourceConverter(Font.class, new FontStringResourceConverter());

        addResourceConverter(Icon.class, new IconStringResourceConverter());
        addResourceConverter(ImageIcon.class, getResourceConverter(Icon.class));
        addResourceConverter(Image.class, new ImageStringResourceConverter());
        addResourceConverter(BufferedImage.class, getResourceConverter(Image.class));
        addResourceConverter(Integer.class, new IntegerStringResourceConverter());
        addResourceConverter(int.class, getResourceConverter(Integer.class));

        addResourceConverter(Insets.class, new InsetsStringResourceConverter());

        addResourceConverter(KeyStroke.class, new KeyStrokeStringResourceConverter());

        addResourceConverter(Long.class, new LongStringResourceConverter());
        addResourceConverter(long.class, getResourceConverter(Long.class));

        addResourceConverter(Point.class, new PointStringResourceConverter());

        addResourceConverter(Rectangle.class, new RectangleStringResourceConverter());

        addResourceConverter(Short.class, new ShortStringResourceConverter());
        addResourceConverter(short.class, getResourceConverter(Short.class));

        addResourceConverter(URL.class, new URLStringResourceConverter());
        addResourceConverter(URI.class, new URIStringResourceConverter());
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
     * @see de.freese.base.resourcemap.ResourceMap#setLocaleSupplier(java.util.function.Supplier)
     */
    @Override
    public void setLocaleSupplier(final Supplier<Locale> localeSupplier)
    {
        this.localeSupplier = Objects.requireNonNull(localeSupplier, "localeSupplier required");
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
}
