package de.freese.base.resourcemap.converter;

import java.awt.Dimension;
import java.util.List;

import de.freese.base.resourcemap.IResourceMap;

/**
 * IResourceConverter fuer eine {@link Dimension}.
 * 
 * @author Thomas Freese
 */
public class DimensionStringResourceConverter extends AbstractResourceConverter<Dimension>
{
	/**
	 * Erstellt ein neues {@link DimensionStringResourceConverter} Object.
	 */
	public DimensionStringResourceConverter()
	{
		super();
	}

	/**
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.IResourceMap)
	 */
	@Override
	public Dimension parseString(final String key, final IResourceMap resourceMap)
		throws ResourceConverterException
	{
		List<Double> xy = parseDoubles(key, 2, "Invalid x,y Dimension string");

		Dimension dimension = new Dimension();
		dimension.setSize(xy.get(0).doubleValue(), xy.get(1).doubleValue());

		return dimension;
	}
}
