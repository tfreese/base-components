package de.freese.base.resourcemap.converter;

import java.net.URL;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * {@link ResourceConverter} für {@link Icon}.
 *
 * @author Thomas Freese
 */
public class IconStringResourceConverter extends AbstractResourceConverter<Icon>
{
    /**
    *
    */
    private final ClassLoader classLoader;

    /**
     * Erstellt ein neues {@link IconStringResourceConverter} Object.
     *
     * @param classLoader {@link ClassLoader}
     */
    public IconStringResourceConverter(final ClassLoader classLoader)
    {
        super();

        this.classLoader = Objects.requireNonNull(classLoader, "classLoader required");
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Icon convert(final String key, final String value)
    {
        return loadIcon(key, value, this.classLoader);
    }

    /**
     * @param key String
     * @param value String
     * @param classLoader {@link ClassLoader}
     * @return {@link ImageIcon}
     */
    protected ImageIcon loadIcon(final String key, final String value, final ClassLoader classLoader)
    {
        if (value == null)
        {
            throwException(key, "null", "path is null");
        }

        URL url = classLoader.getResource(value);

        if (url == null)
        {
            url = Thread.currentThread().getContextClassLoader().getResource(value);
        }

        if (url != null)
        {
            return new ImageIcon(url);
        }

        throwException(key, value, "couldn't find resource");

        return null;
    }
}
