package de.freese.base.resourcemap.converter;

/**
 * Resourceconverter fuer Bytes.
 * 
 * @author Thomas Freese
 */
public class ByteStringResourceConverter extends AbstractNumberResourceConverter<Byte>
{
	/**
	 * Erstellt ein neues {@link ByteStringResourceConverter} Object.
	 */
	public ByteStringResourceConverter()
	{
		super();

		addType(byte.class);
	}

	/**
	 * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#parseString(java.lang.String,
	 *      int)
	 */
	@Override
	protected Byte parseString(final String key, final int radix) throws NumberFormatException
	{
		return (radix == -1) ? Byte.decode(key) : Byte.valueOf(key, radix);
	}
}
