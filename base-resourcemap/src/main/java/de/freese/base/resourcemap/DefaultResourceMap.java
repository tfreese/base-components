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
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import de.freese.base.resourcemap.converter.ResourceConverterException;
import de.freese.base.resourcemap.converter.ShortStringResourceConverter;
import de.freese.base.resourcemap.converter.URIStringResourceConverter;
import de.freese.base.resourcemap.converter.URLStringResourceConverter;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * ResourceMap zum laden und verarbeiten lokalisierter Texte.
 *
 * @author Thomas Freese
 */
public class DefaultResourceMap implements ResourceMap
{
    /**
     *
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultResourceMap.class);

    /**
     *
     */
    private final static Object nullResource = new String("null resource");

    /**
     *
     */
    private final String baseName;

    /**
     *
     */
    private final ClassLoader classLoader;

    /**
     *
     */
    private final Map<Class<?>, ResourceConverter<?>> converters = new HashMap<>();

    /**
     * Enthält alle bereits geladenen Resourcen pro Locale.
     */
    private final Map<Locale, Map<String, String>> localeResources = new HashMap<>();

    /**
     * Enthält alle bereits geparsten Resourcen.
     */
    private final Map<Locale, Map<Class<?>, Map<String, Object>>> localeResourcesCache = new HashMap<>();

    /**
     *
     */
    private ResourceMap parent = null;

    /**
     *
     */
    private ResourceProvider resourceProvider = null;

    /**
     * Erstellt ein neues {@link DefaultResourceMap} Object.
     *
     * @param baseName String
     * @param classLoader {@link ClassLoader}
     * @param resourceProvider {@link ResourceProvider}; Optional, Parent wird verwendet, wenn nicht vorhanden.
     */
    protected DefaultResourceMap(final String baseName, final ClassLoader classLoader, final ResourceProvider resourceProvider)
    {
        super();

        Objects.requireNonNull(baseName, "baseName required");

        if (baseName.trim().length() == 0)
        {
            throw new IllegalArgumentException("baseName length = 0");
        }

        this.baseName = baseName.trim();
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader required");

        this.resourceProvider = resourceProvider;

        initDefaultConverter();
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#addResourceConverter(java.lang.Class, de.freese.base.resourcemap.converter.ResourceConverter)
     */
    @Override
    public void addResourceConverter(final Class<?> supportedType, final ResourceConverter<?> converter)
    {
        this.converters.put(supportedType, converter);
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
     * Der Wert von evaluateStringExpression("place") wäre "Hello World". Der Wert von einem ${null} ist null.
     *
     * @param expr String
     * @param locale {@link Locale}
     * @return String
     */
    protected final String evaluateStringExpression(final String expr, final Locale locale)
    {
        if (expr.trim().equals("${null}"))
        {
            return null;
        }

        StringBuilder value = new StringBuilder();

        int firstIndex = 0;
        int indexStart$Brace = 0;

        while ((indexStart$Brace = expr.indexOf("${", firstIndex)) != -1)
        {
            if ((indexStart$Brace == 0) || ((indexStart$Brace > 0) && (expr.charAt(indexStart$Brace - 1) != '\\')))
            {
                int indexEndBrace = expr.indexOf("}", indexStart$Brace);

                if ((indexEndBrace != -1) && (indexEndBrace > (indexStart$Brace + 2)))
                {
                    String k = expr.substring(indexStart$Brace + 2, indexEndBrace);
                    String v = getObject(k, String.class);
                    value.append(expr.substring(firstIndex, indexStart$Brace));

                    if (v != null)
                    {
                        value.append(v);
                    }
                    else
                    {
                        throw new LookupException(getBaseName(), k, String.class, locale, "no value");
                    }

                    // skip trailing "}"
                    firstIndex = indexEndBrace + 1;
                }
                else
                {
                    throw new LookupException(getBaseName(), expr, String.class, locale, "no closing brace");
                }
            }
            else
            {
                // we've found an escaped variable - "\${"
                value.append(expr.substring(firstIndex, indexStart$Brace - 1));
                value.append("${");

                // skip past "${"
                firstIndex = indexStart$Brace + 2;
            }
        }

        value.append(expr.substring(firstIndex));

        return value.toString();
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
        Map<Class<?>, Map<String, Object>> localeResources = this.localeResourcesCache.get(locale);

        if (localeResources == null)
        {
            localeResources = new HashMap<>();
            this.localeResourcesCache.put(locale, localeResources);
        }

        Map<String, Object> typeResources = localeResources.get(type);

        if (typeResources == null)
        {
            typeResources = new HashMap<>();
            localeResources.put(type, typeResources);
        }

        return typeResources.get(key);
    }

    /**
     * Liefert den ClassLoader der ResourceMap.
     *
     * @return {@link ClassLoader}
     */
    protected ClassLoader getClassLoader()
    {
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
        return Locale.getDefault();
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getObject(java.lang.String, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public final <T> T getObject(final String key, final Class<T> type)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("null key");
        }

        if (type == null)
        {
            throw new IllegalArgumentException("null type");
        }

        Locale locale = getLocale();

        loadResourcesIfAbsent(locale);

        Object value = null;

        // Cache prüfen
        value = getCachedResource(locale, type, key);

        if (value != null)
        {
            return (T) value;
        }

        Map<String, String> resources = this.localeResources.get(locale);

        String stringValue = resources.get(key);

        if (stringValue == null)
        {
            // Im Parent suchen
            if (getParent() != null)
            {
                try
                {
                    value = getParent().getObject(key, type);
                }
                catch (LookupException ex)
                {
                    // Exceptions vom Parent kapseln, um am Ende der Methode eigene Exception zu
                    // werfen.
                    value = null;
                }
            }

            // if (value == null)
            // {
            // value = nullResource;
            // }
        }
        else
        {
            if (type == String.class)
            {
                // Wenn ${key} Variablen existieren diese ersetzten
                if (stringValue.contains("${"))
                {
                    value = evaluateStringExpression(stringValue, locale);
                }
                else
                {
                    value = stringValue;
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
                    catch (ResourceConverterException ex)
                    {
                        LookupException lfe = new LookupException(getBaseName(), key, type, locale, "string conversion failed");
                        lfe.initCause(ex);

                        throw lfe;
                    }
                }
                else
                {
                    throw new LookupException(getBaseName(), key, type, locale, "no StringConverter for required type");
                }
            }
        }

        if (value == null)
        {
            throw new LookupException(getBaseName(), key, type, locale, "no value");
        }

        putCachedResource(locale, type, key, value);

        return (T) (value == nullResource ? null : value);
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
     * @see de.freese.base.resourcemap.ResourceMap#getResourceConverter(java.lang.Class)
     */
    @Override
    public ResourceConverter<?> getResourceConverter(final Class<?> type)
    {
        ResourceConverter<?> converter = this.converters.get(type);

        if ((converter == null) && (getParent() != null))
        {
            converter = getParent().getResourceConverter(type);
        }

        return converter;
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getResourceProvider()
     */
    @Override
    public ResourceProvider getResourceProvider()
    {
        if (this.resourceProvider == null)
        {
            if (getParent() != null)
            {
                this.resourceProvider = getParent().getResourceProvider();
            }
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
            LOGGER.warn(null, ex);

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
        Map<Class<?>, Map<String, Object>> localeResources = this.localeResourcesCache.get(locale);
        Map<String, Object> typeResources = localeResources.get(type);
        typeResources.put(key, object);
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#setParent(de.freese.base.resourcemap.ResourceMap)
     */
    @Override
    public void setParent(final ResourceMap parent)
    {
        this.parent = parent;
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
