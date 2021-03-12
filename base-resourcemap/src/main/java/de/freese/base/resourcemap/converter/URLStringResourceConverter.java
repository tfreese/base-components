package de.freese.base.resourcemap.converter;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link ResourceConverter} für {@link URL}.
 *
 * @author Thomas Freese
 */
public class URLStringResourceConverter extends AbstractResourceConverter<URL>
{
    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public URL convert(final String key, final String value)
    {
        try
        {
            return new URL(value);
        }
        catch (MalformedURLException ex)
        {
            throwException(key, value, "Invalid URL");
        }

        return null;
    }
}
