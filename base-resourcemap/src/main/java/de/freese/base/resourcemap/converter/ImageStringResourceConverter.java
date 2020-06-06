package de.freese.base.resourcemap.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 * Laedt aus einem String (Path) ein {@link BufferedImage}.
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
        return loadImage(value, this.classLoader);
    }

    /**
     * @param imagePath String
     * @param classLoader {@link ClassLoader}
     * @return {@link BufferedImage}
     * @throws ResourceConverterException Falls was schief geht.
     */
    protected BufferedImage loadImage(final String imagePath, final ClassLoader classLoader) throws ResourceConverterException
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
            try
            {
                return ImageIO.read(url);
            }
            catch (IOException ex)
            {
                throw new ResourceConverterException("", imagePath, ex);
            }
        }

        String msg = String.format("couldn't find Icon resource \"%s\"", imagePath);

        throw new ResourceConverterException(msg, imagePath);
    }
}
