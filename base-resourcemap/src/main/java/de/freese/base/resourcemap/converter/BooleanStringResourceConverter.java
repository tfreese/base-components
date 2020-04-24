package de.freese.base.resourcemap.converter;

import de.freese.base.resourcemap.ResourceMap;

/**
 * IResourceConverter fuer Booleans.
 * 
 * @author Thomas Freese
 */
public class BooleanStringResourceConverter extends AbstractResourceConverter<Boolean>
{
	/**
	 * 
	 */
	private final String[] trueStrings;

	/**
	 * Erstellt ein neues {@link BooleanStringResourceConverter} Object.
	 * 
	 * @param trueStrings String, zb. true, on, yes, 1
	 */
	public BooleanStringResourceConverter(final String...trueStrings)
	{
		super();

		addType(boolean.class);

		this.trueStrings = trueStrings;
	}

	/**
	 * @see de.freese.base.resourcemap.converter.ResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.ResourceMap)
	 */
	@Override
	public Boolean parseString(final String key, final ResourceMap resourceMap)
		throws ResourceConverterException
	{
		String m_key = key.trim();

		for (String trueString : this.trueStrings)
		{
			if (m_key.equalsIgnoreCase(trueString))
			{
				return Boolean.TRUE;
			}
		}

		return Boolean.FALSE;
	}
}
