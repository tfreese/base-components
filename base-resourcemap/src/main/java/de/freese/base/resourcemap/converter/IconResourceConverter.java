package de.freese.base.resourcemap.converter;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Thomas Freese
 */
public class IconResourceConverter extends AbstractResourceConverter<Icon> {
    @Override
    public Icon convert(final String key, final String value) {
        if (value == null) {
            throwException(key, "null", "path is null");
        }

        final URL url = getUrl(value);

        if (url != null) {
            return new ImageIcon(url);
        }

        throwException(key, value, "couldn't find resource");

        return null;
    }
}
