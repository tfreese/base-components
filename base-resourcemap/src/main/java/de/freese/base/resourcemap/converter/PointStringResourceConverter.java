package de.freese.base.resourcemap.converter;

import java.awt.Point;
import java.util.List;

import de.freese.base.resourcemap.IResourceMap;

/**
 * IResourceConverter fuer einen {@link Point}.
 * 
 * @author Thomas Freese
 */
public class PointStringResourceConverter extends AbstractResourceConverter<Point>
{
	/**
	 * Erstellt ein neues {@link PointStringResourceConverter} Object.
	 */
	public PointStringResourceConverter()
	{
		super();
	}

	/**
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.IResourceMap)
	 */
	@Override
	public Point parseString(final String key, final IResourceMap resourceMap)
		throws ResourceConverterException
	{
		List<Double> xy = parseDoubles(key, 2, "Invalid x,y Point string");

		Point point = new Point();
		point.setLocation(xy.get(0).doubleValue(), xy.get(1).doubleValue());

		return point;
	}
}
