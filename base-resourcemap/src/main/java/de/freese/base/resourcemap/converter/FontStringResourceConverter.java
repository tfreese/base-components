package de.freese.base.resourcemap.converter;

import java.awt.Font;

import de.freese.base.resourcemap.ResourceMap;

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
	 * @see de.freese.base.resourcemap.converter.ResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.ResourceMap)
	 */
	@Override
	public Font parseString(final String key, final ResourceMap resourceMap)
		throws ResourceConverterException
	{
		return Font.decode(key);
	}
}
