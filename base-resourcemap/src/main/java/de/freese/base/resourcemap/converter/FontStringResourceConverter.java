package de.freese.base.resourcemap.converter;

import java.awt.Font;

/**
 * Laedt aus einem String einen Font.<br>
 * Typical string is: face-STYLE-size, for example "Arial-PLAIN-12".
 *
 * @author Thomas Freese
 */
public class FontStringResourceConverter extends AbstractResourceConverter<Font>
{
    /**
     * Erstellt ein neues {@link FontStringResourceConverter} Object.
     */
    public FontStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Font convert(final String key, final String value)
    {
        return Font.decode(value);
    }
}
