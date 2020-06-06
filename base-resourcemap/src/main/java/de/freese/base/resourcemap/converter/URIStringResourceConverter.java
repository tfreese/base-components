package de.freese.base.resourcemap.converter;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * IResourceConverter fuer URIs.
 *
 * @author Thomas Freese
 */
public class URIStringResourceConverter extends AbstractResourceConverter<URI>
{
    /**
     * Erstellt ein neues {@link URIStringResourceConverter} Object.
     */
    public URIStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public URI convert(final String key, final String value)
    {
        try
        {
            return new URI(value);
        }
        catch (URISyntaxException ex)
        {
            throw new ResourceConverterException("Invalid URI", key, ex);
        }
    }
}
