package de.freese.base.resourcemap.converter;

import java.net.URI;
import java.net.URISyntaxException;

import de.freese.base.resourcemap.ResourceMap;

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
	 * @see de.freese.base.resourcemap.converter.ResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.ResourceMap)
	 */
	@Override
	public URI parseString(final String key, final ResourceMap resourceMap)
		throws ResourceConverterException
	{
		try
		{
			return new URI(key);
		}
		catch (URISyntaxException ex)
		{
			throw new ResourceConverterException("Invalid URI", key, ex);
		}
	}
}
