package de.freese.base.resourcemap;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.converter.IconStringResourceConverter;
import de.freese.base.resourcemap.converter.ImageStringResourceConverter;
import de.freese.base.resourcemap.converter.InsetsStringResourceConverter;
import de.freese.base.resourcemap.converter.IntegerStringResourceConverter;
import de.freese.base.resourcemap.converter.KeyStrokeStringResourceConverter;
import de.freese.base.resourcemap.converter.LongStringResourceConverter;
import de.freese.base.resourcemap.converter.PointStringResourceConverter;
import de.freese.base.resourcemap.converter.RectangleStringResourceConverter;
import de.freese.base.resourcemap.converter.ResourceConverterException;
import de.freese.base.resourcemap.converter.ShortStringResourceConverter;
import de.freese.base.resourcemap.converter.URIStringResourceConverter;
import de.freese.base.resourcemap.converter.URLStringResourceConverter;
import de.freese.base.resourcemap.provider.ResourceProvider;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;

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
    private ClassLoader classLoader = null;

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
     */
    protected DefaultResourceMap(final String baseName)
    {
        super();

        Objects.requireNonNull(baseName, "baseName required");

        if (baseName.trim().length() == 0)
        {
            throw new IllegalArgumentException("baseName length = 0");
        }

        this.baseName = baseName.trim();

        addResourceConverter(new ColorStringResourceConverter());
        addResourceConverter(new IconStringResourceConverter());
        addResourceConverter(new ImageStringResourceConverter());
        addResourceConverter(new FontStringResourceConverter());
        addResourceConverter(new ByteStringResourceConverter());
        addResourceConverter(new ShortStringResourceConverter());
        addResourceConverter(new IntegerStringResourceConverter());
        addResourceConverter(new LongStringResourceConverter());
        addResourceConverter(new DoubleStringResourceConverter());
        addResourceConverter(new FloatStringResourceConverter());
        addResourceConverter(new BooleanStringResourceConverter("true", "on", "yes", "1"));
        addResourceConverter(new KeyStrokeStringResourceConverter());
        addResourceConverter(new URLStringResourceConverter());
        addResourceConverter(new URIStringResourceConverter());
        addResourceConverter(new EmptyBorderStringResourceConverter());
        addResourceConverter(new DimensionStringResourceConverter());
        addResourceConverter(new InsetsStringResourceConverter());
        addResourceConverter(new PointStringResourceConverter());
        addResourceConverter(new RectangleStringResourceConverter());
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#addResourceConverter(de.freese.base.resourcemap.converter.ResourceConverter)
     */
    @Override
    public void addResourceConverter(final ResourceConverter<?> converter)
    {
        Set<Class<?>> converterTypes = converter.getSupportedTypes();

        // Prüfen, ob die Typen schon von anderen Convertern abgedeckt sind
        if (!Collections.disjoint(this.converters.keySet(), converterTypes))
        {
            throw new IllegalArgumentException("Type(s) for converter are already supported by another converter");
        }

        for (Class<?> supportedType : converterTypes)
        {
            this.converters.put(supportedType, converter);
        }
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
     * @see de.freese.base.resourcemap.ResourceMap#getClassLoader()
     */
    @Override
    public ClassLoader getClassLoader()
    {
        if (this.classLoader == null)
        {
            this.classLoader = getClass().getClassLoader();
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
                        value = stringConverter.parseString(stringValue, this);
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
            else
            {
                this.resourceProvider = new ResourceBundleProvider();
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
     * @see de.freese.base.resourcemap.ResourceMap#setClassLoader(java.lang.ClassLoader)
     */
    @Override
    public void setClassLoader(final ClassLoader classLoader)
    {
        this.classLoader = classLoader;
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
     * @see de.freese.base.resourcemap.ResourceMap#setResourceProvider(de.freese.base.resourcemap.provider.ResourceProvider)
     */
    @Override
    public void setResourceProvider(final ResourceProvider resourceProvider)
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
        builder.append("ResourceMap [baseName=");
        builder.append(this.baseName);
        builder.append(", parent=");
        builder.append(this.parent == null ? "null" : this.parent.getBaseName());
        builder.append("]");

        return builder.toString();
    }
}
