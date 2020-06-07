package de.freese.base.resourcemap.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 * {@link ResourceConverter} für {@link BufferedImage}.
 *
 * @author Thomas Freese
 */
public class ImageStringResourceConverter extends AbstractResourceConverter<BufferedImage>
{
    /**
     *
     */
    private final ClassLoader classLoader;

    /**
     * Erstellt ein neues {@link ImageStringResourceConverter} Object.
     *
     * @param classLoader {@link ClassLoader}
     */
    public ImageStringResourceConverter(final ClassLoader classLoader)
    {
        super();

        this.classLoader = Objects.requireNonNull(classLoader, "classLoader required");
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public BufferedImage convert(final String key, final String value)
    {
        return loadImage(key, value, this.classLoader);
    }

    /**
     * @param key String
     * @param value String
     * @param classLoader {@link ClassLoader}
     * @return {@link BufferedImage}
     */
    protected BufferedImage loadImage(final String key, final String value, final ClassLoader classLoader)
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
            try
            {
                return ImageIO.read(url);
            }
            catch (IOException ex)
            {
                throwException(key, value, ex);
            }
        }

        throwException(key, value, "couldn't find resource");

        return null;
    }
}
