package de.freese.base.resourcemap.converter;

import java.awt.Font;

/**
 * Typical string is: "Arial-PLAIN-12".
 *
 * @author Thomas Freese
 */
public class FontResourceConverter extends AbstractResourceConverter<Font>
{
    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Font convert(final String key, final String value)
    {
        return Font.decode(value);
    }
}
