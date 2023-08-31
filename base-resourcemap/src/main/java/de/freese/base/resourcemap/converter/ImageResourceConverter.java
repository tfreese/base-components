package de.freese.base.resourcemap.converter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * @author Thomas Freese
 */
public class ImageResourceConverter extends AbstractResourceConverter<BufferedImage> {
    @Override
    public BufferedImage convert(final String key, final String value) {
        if (value == null) {
            throwException(key, "null", "path is null");
        }

        URL url = getUrl(value);

        if (url != null) {
            try {
                return ImageIO.read(url);
            }
            catch (IOException ex) {
                throwException(key, value, ex);
            }
        }

        throwException(key, value, "couldn't find resource");

        return null;
    }
}
