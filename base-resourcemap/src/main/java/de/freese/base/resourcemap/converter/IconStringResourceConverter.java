package de.freese.base.resourcemap.converter;

import java.net.URL;

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
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Icon convert(final String key, final String value)
    {
        if (value == null)
        {
            throwException(key, "null", "path is null");
        }

        URL url = getUrl(value);

        if (url != null)
        {
            return new ImageIcon(url);
        }

        throwException(key, value, "couldn't find resource");

        return null;
    }
}
