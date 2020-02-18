package de.freese.base.resourcemap.converter;

import de.freese.base.resourcemap.IResourceMap;

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
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.IResourceMap)
	 */
	@Override
	public Boolean parseString(final String key, final IResourceMap resourceMap)
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
