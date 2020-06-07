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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * {@link ResourceMap} zum laden und verarbeiten lokalisierter Texte.
 *
 * @author Thomas Freese
 */
public class DefaultResourceMap implements ResourceMap
{
    /**
     *
     */
    private final static Object NULL_VALUE = "null value";

    /**
     *
     */
    private final String baseName;

    /**
     * Enthält alle bereits geparsten Resourcen die keine Strings sind.
     */
    private final Map<Locale, Map<Class<?>, Map<String, Object>>> cache = new HashMap<>();

    /**
     *
     */
    private final ClassLoader classLoader;

    /**
     * Enthält alle bereits geladenen Resourcen pro Locale.
     */
    private final Map<Locale, Map<String, String>> localeResources = new HashMap<>();

    /**
     *
     */
    private Supplier<Locale> localeSupplier = () -> getParent() != null ? ((DefaultResourceMap) getParent()).getLocale() : Locale.getDefault();

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final ResourceMap parent;

    /**
     *
     */
    private final Map<Class<?>, ResourceConverter<?>> resourceConverters = new HashMap<>();

    /**
     *
     */
    private final ResourceProvider resourceProvider;

    /**
     * Erstellt ein neues {@link DefaultResourceMap} Object.
     *
     * @param baseName String
     * @param parent {@link ResourceMap}
     * @param classLoader {@link ClassLoader}
     * @param resourceProvider {@link ResourceProvider}
     */
    protected DefaultResourceMap(final String baseName, final ResourceMap parent, final ClassLoader classLoader, final ResourceProvider resourceProvider)
    {
        super();

        this.baseName = Objects.requireNonNull(baseName, "baseName required");
        this.parent = parent;
        this.classLoader = classLoader;
        this.resourceProvider = resourceProvider;

        initDefaultConverter();
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
                int endIndex = expression.indexOf("}", startIndex);

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
     * @see de.freese.base.resourcemap.ResourceMap#getBaseName()
     */
    @Override
    public String getBaseName()
    {
        return this.baseName;
    }

    /**
     * Liefert das bereits geparste Objekt oder null.
     *
     * @param locale {@link Locale}
     * @param key String
     * @param type Class
     * @return Object
     */
    protected Object getCachedResource(final Locale locale, final Class<?> type, final String key)
    {
        if (type == String.class)
        {
            // Reine Strings sind ja schon in der localeResources Map.
            return this.localeResources.computeIfAbsent(locale, k -> new HashMap<>()).get(key);
        }

        Map<Class<?>, Map<String, Object>> cachedLocaleResources = this.cache.computeIfAbsent(locale, k -> new HashMap<>());
        Map<String, Object> cachedTypeResources = cachedLocaleResources.computeIfAbsent(type, k -> new HashMap<>());

        return cachedTypeResources.get(key);
    }

    /**
     * Liefert den ClassLoader der ResourceMap.
     *
     * @return {@link ClassLoader}
     */
    protected ClassLoader getClassLoader()
    {
        if ((this.classLoader == null) && (getParent() != null))
        {
            return ((DefaultResourceMap) getParent()).getClassLoader();
        }

        return this.classLoader;
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getKeys()
     */
    @Override
    public final Set<String> getKeys()
    {
        Locale locale = getLocale();

        loadResourcesIfAbsent(locale);

        Map<String, String> resources = this.localeResources.get(locale);

        if (resources == null)
        {
            return Collections.emptySet();
        }

        return resources.keySet();
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
     * @see de.freese.base.resourcemap.ResourceMap#getObject(java.lang.String, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <T> T getObject(final String key, final Class<T> type)
    {
        Objects.requireNonNull(key, "key required");
        Objects.requireNonNull(type, "type required");

        Locale locale = getLocale();

        loadResourcesIfAbsent(locale);

        Object value = null;

        // Object-Cache prüfen
        value = getCachedResource(locale, type, key);

        if (value != null)
        {
            if (value == NULL_VALUE)
            {
                return null;
            }

            return (T) value;
        }

        String stringValue = (String) getCachedResource(locale, String.class, key);

        if (stringValue == null)
        {
            // Im Parent suchen
            if (getParent() != null)
            {
                try
                {
                    value = getParent().getObject(key, type);
                }
                catch (Exception ex)
                {
                    getLogger().warn(null, ex);
                }
            }
        }
        else
        {
            ResourceConverter<?> stringConverter = getResourceConverter(type);

            if (stringConverter != null)
            {
                try
                {
                    value = stringConverter.convert(key, stringValue);
                }
                catch (Exception ex)
                {
                    getLogger().warn(null, ex);
                }
            }
            else
            {
                getLogger().warn("{} - {}: No ResourceConverter found for required type {}", key, value, type.getSimpleName());
            }
        }

        if (value == null)
        {
            getLogger().warn("Key {}: no resource found", key);
            value = NULL_VALUE;
        }

        putCachedResource(locale, type, key, value);

        if (value == NULL_VALUE)
        {
            return null;
        }

        return (T) value;
    }

    /**
     * Liefert den gesetzten Parent oder null.
     *
     * @return {@link ResourceMap}
     */
    protected ResourceMap getParent()
    {
        return this.parent;
    }

    /**
     * Liefert den {@link ResourceConverter} für den Typ.<br>
     * Sollte diese ResourceMap keinen passenden Converter enthalten, wird der Parent befraget, wenn vorhanden.
     *
     * @param supportedType {@link Class}
     * @return {@link ResourceConverter}
     */
    protected ResourceConverter<?> getResourceConverter(final Class<?> supportedType)
    {
        ResourceConverter<?> converter = this.resourceConverters.get(supportedType);

        if ((converter == null) && (getParent() != null))
        {
            converter = ((DefaultResourceMap) getParent()).getResourceConverter(supportedType);
        }

        return converter;
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getResourceProvider()
     */
    @Override
    public ResourceProvider getResourceProvider()
    {
        if ((this.resourceProvider == null) && (getParent() != null))
        {
            return getParent().getResourceProvider();
        }

        return this.resourceProvider;
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getString(java.lang.String, java.lang.Object[])
     */
    @Override
    public final String getString(final String key, final Object...args)
    {
        String value = null;

        try
        {
            if (args.length == 0)
            {
                value = getObject(key, String.class);
            }
            else
            {
                String format = getObject(key, String.class);

                if (format != null)
                {
                    if (format.indexOf("{0}") != -1)
                    {
                        // Das "alte" Format
                        value = MessageFormat.format(format, args);
                    }
                    else
                    {
                        // Das "neue" Format
                        value = String.format(format, args);
                    }
                }
            }
        }
        catch (LookupException ex)
        {
            getLogger().warn(null, ex);

            value = "#" + key;
        }

        return value;
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

        addResourceConverter(Icon.class, new IconStringResourceConverter(getClassLoader()));
        addResourceConverter(ImageIcon.class, getResourceConverter(Icon.class));
        addResourceConverter(Image.class, new ImageStringResourceConverter(getClassLoader()));
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
     * Laden der Resources, falls für aktuelles Locale noch nicht vorhanden.
     *
     * @param locale {@link Locale}
     */
    protected void loadResourcesIfAbsent(final Locale locale)
    {
        Map<String, String> resources = this.localeResources.get(locale);

        if (resources == null)
        {
            resources = getResourceProvider().getResources(getBaseName(), locale, getClassLoader());

            if (resources == null)
            {
                resources = Collections.emptyMap();
            }

            this.localeResources.put(locale, resources);

            evaluateStringExpressions(locale, resources);
        }
    }

    /**
     * Setzt das bereits geparste Objekt in den Cache.
     *
     * @param locale {@link Locale}
     * @param key String
     * @param type Class
     * @param object Object
     */
    protected void putCachedResource(final Locale locale, final Class<?> type, final String key, final Object object)
    {
        if (type == String.class)
        {
            // Reine Strings sind ja schon in der localeResources Map.
            return;
        }

        Map<Class<?>, Map<String, Object>> cachedLocaleResources = this.cache.computeIfAbsent(locale, k -> new HashMap<>());
        Map<String, Object> cachedTypeResources = cachedLocaleResources.computeIfAbsent(type, k -> new HashMap<>());
        cachedTypeResources.put(key, object);
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
        builder.append("ResourceMap [baseName=");
        builder.append(this.baseName);
        builder.append(", parent=");
        builder.append(this.parent == null ? "null" : this.parent.getBaseName());
        builder.append("]");

        return builder.toString();
    }
}
