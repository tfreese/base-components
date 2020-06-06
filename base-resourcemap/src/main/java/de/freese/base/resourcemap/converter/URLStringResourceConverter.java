package de.freese.base.resourcemap.converter;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * IResourceConverter fuer URLs.
 *
 * @author Thomas Freese
 */
public class URLStringResourceConverter extends AbstractResourceConverter<URL>
{
    /**
     * Erstellt ein neues {@link URLStringResourceConverter} Object.
     */
    public URLStringResourceConverter()
    {
        super();
    }

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
            throw new ResourceConverterException("Invalid URL", key, ex);
        }
    }
}
