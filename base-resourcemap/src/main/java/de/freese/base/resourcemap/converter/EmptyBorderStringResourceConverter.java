package de.freese.base.resourcemap.converter;

import java.util.List;

import javax.swing.border.EmptyBorder;

import de.freese.base.resourcemap.IResourceMap;

/**
 * IResourceConverter fuer einen {@link EmptyBorder}.
 * 
 * @author Thomas Freese
 */
public class EmptyBorderStringResourceConverter extends AbstractResourceConverter<EmptyBorder>
{
	/**
	 * Erstellt ein neues {@link EmptyBorderStringResourceConverter} Object.
	 */
	public EmptyBorderStringResourceConverter()
	{
		super();
	}

	/**
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.IResourceMap)
	 */
	@Override
	public EmptyBorder parseString(final String key, final IResourceMap resourceMap)
		throws ResourceConverterException
	{
		List<Double> tlbr =
				parseDoubles(key, 4, "Invalid top,left,bottom,right EmptyBorder string");

		return new EmptyBorder(tlbr.get(0).intValue(), tlbr.get(1).intValue(), tlbr.get(2)
				.intValue(), tlbr.get(3).intValue());
	}
}
