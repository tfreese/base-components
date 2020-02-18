package de.freese.base.resourcemap.converter;

/**
 * Resourceconverter fuer Shorts.
 * 
 * @author Thomas Freese
 */
public class ShortStringResourceConverter extends AbstractNumberResourceConverter<Short>
{
	/**
	 * Erstellt ein neues {@link ShortStringResourceConverter} Object.
	 */
	public ShortStringResourceConverter()
	{
		super();

		addType(short.class);
	}

	/**
	 * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#parseString(java.lang.String,
	 *      int)
	 */
	@Override
	protected Short parseString(final String key, final int radix) throws NumberFormatException
	{
		return (radix == -1) ? Short.decode(key) : Short.valueOf(key, radix);
	}
}
