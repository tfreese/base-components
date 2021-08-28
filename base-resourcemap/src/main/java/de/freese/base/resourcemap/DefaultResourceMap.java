package de.freese.base.resourcemap;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * Default-{@link ResourceMap} zum laden und verarbeiten lokalisierter Texte.
 *
 * @author Thomas Freese
 */
public class DefaultResourceMap extends AbstractResourceMap
{
    /**
     * Erstellt ein neues {@link DefaultResourceMap} Object.
     *
     * @param bundleName String
     * @param parent {@link ResourceMap}
     * @param resourceProvider {@link ResourceProvider}
     */
    protected DefaultResourceMap(final String bundleName, final ResourceMap parent, final ResourceProvider resourceProvider)
    {
        super(bundleName);

        setParent(parent);
        setResourceProvider(resourceProvider);

        initDefaultConverter();
    }

    /**
     * @param <T> Type
     * @param locale {@link Locale}
     * @param type Class
     * @param key String
     *
     * @return Object
     */
    protected final <T> T getConvertedValue(final Locale locale, final Class<T> type, final String key)
    {
        String stringValue = getValue(locale, String.class, key);

        if (stringValue == null)
        {
            return null;
        }

        T value = null;
        ResourceConverter<T> converter = getResourceConverter(type);

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

        return value;
    }

    /**
     * @see de.freese.base.resourcemap.ResourceMap#getObject(java.lang.String, java.lang.Class)
     */
    @Override
    public final <T> T getObject(final String key, final Class<T> type)
    {
        Locale locale = getLocale();

        Objects.requireNonNull(key, "key required");
        Objects.requireNonNull(type, "type required");
        Objects.requireNonNull(locale, "locale required");

        T value;

        value = getValue(locale, type, key);

        if (value != null)
        {
            return value;
        }

        value = getConvertedValue(locale, type, key);

        if (value == null)
        {
            // Fallback: Im Parent suchen
            if (getParent() != null)
            {
                try
                {
                    value = getParent().getObject(key, type);
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);
                }
            }
        }

        if (value == null)
        {
            getLogger().warn("{}: no resource found for key '{}'", getBundleName(), key);
        }

        putValue(locale, type, key, value);

        return value;
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
                        // Das "alte" Format.
                        value = MessageFormat.format(format, args);
                    }
                    else
                    {
                        // Das "neue" Format.
                        value = String.format(format, args);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            getLogger().warn(null, ex);

            value = "#" + key;
        }

        return value;
    }
}
