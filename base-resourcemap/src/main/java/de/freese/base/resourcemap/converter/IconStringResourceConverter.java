package de.freese.base.resourcemap.converter;

import java.net.URL;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Laedt aus einem String (Path) ein {@link Icon}.
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
        return loadIcon(value, this.classLoader);
    }

    /**
     * @param imagePath String
     * @param classLoader {@link ClassLoader}
     * @return {@link ImageIcon}
     * @throws ResourceConverterException Falls was schief geht.
     */
    protected ImageIcon loadIcon(final String imagePath, final ClassLoader classLoader) throws ResourceConverterException
    {
        if (imagePath == null)
        {
            String msg = String.format("invalid image/icon path \"%s\"", imagePath);

            throw new ResourceConverterException(msg, null);
        }

        URL url = classLoader.getResource(imagePath);

        if (url == null)
        {
            url = Thread.currentThread().getContextClassLoader().getResource(imagePath);
        }

        if (url != null)
        {
            return new ImageIcon(url);
        }

        String msg = String.format("couldn't find Icon resource \"%s\"", imagePath);

        throw new ResourceConverterException(msg, imagePath);
    }
}
