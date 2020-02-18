package de.freese.base.resourcemap.converter;

import java.net.MalformedURLException;
import java.net.URL;

import de.freese.base.resourcemap.IResourceMap;

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
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.IResourceMap)
	 */
	@Override
	public URL parseString(final String key, final IResourceMap resourceMap)
		throws ResourceConverterException
	{
		try
		{
			return new URL(key);
		}
		catch (MalformedURLException ex)
		{
			throw new ResourceConverterException("Invalid URL", key, ex);
		}
	}
}
