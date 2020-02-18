package de.freese.base.resourcemap.converter;

import java.awt.Insets;
import java.util.List;

import de.freese.base.resourcemap.IResourceMap;

/**
 * IResourceConverter fuer {@link Insets}.
 * 
 * @author Thomas Freese
 */
public class InsetsStringResourceConverter extends AbstractResourceConverter<Insets>
{
	/**
	 * Erstellt ein neues {@link InsetsStringResourceConverter} Object.
	 */
	public InsetsStringResourceConverter()
	{
		super();
	}

	/**
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.IResourceMap)
	 */
	@Override
	public Insets parseString(final String key, final IResourceMap resourceMap)
		throws ResourceConverterException
	{
		List<Double> tlbr = parseDoubles(key, 4, "Invalid top,left,bottom,right Insets string");

		return new Insets(tlbr.get(0).intValue(), tlbr.get(1).intValue(), tlbr.get(2).intValue(),
				tlbr.get(3).intValue());
	}
}
