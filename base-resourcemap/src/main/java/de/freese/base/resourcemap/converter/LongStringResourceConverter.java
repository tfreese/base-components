package de.freese.base.resourcemap.converter;

/**
 * Resourceconverter fuer Longs.
 * 
 * @author Thomas Freese
 */
public class LongStringResourceConverter extends AbstractNumberResourceConverter<Long>
{
	/**
	 * Erstellt ein neues {@link LongStringResourceConverter} Object.
	 */
	public LongStringResourceConverter()
	{
		super();

		addType(long.class);
	}

	/**
	 * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#parseString(java.lang.String,
	 *      int)
	 */
	@Override
	protected Long parseString(final String key, final int radix) throws NumberFormatException
	{
		return (radix == -1) ? Long.decode(key) : Long.valueOf(key, radix);
	}
}
