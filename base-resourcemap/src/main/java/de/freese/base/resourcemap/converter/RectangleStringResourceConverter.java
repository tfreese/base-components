package de.freese.base.resourcemap.converter;

import java.awt.Rectangle;
import java.util.List;

import de.freese.base.resourcemap.ResourceMap;

/**
 * IResourceConverter fuer ein {@link Rectangle}.
 * 
 * @author Thomas Freese
 */
public class RectangleStringResourceConverter extends AbstractResourceConverter<Rectangle>
{
	/**
	 * Erstellt ein neues {@link RectangleStringResourceConverter} Object.
	 */
	public RectangleStringResourceConverter()
	{
		super();
	}

	/**
	 * @see de.freese.base.resourcemap.converter.ResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.ResourceMap)
	 */
	@Override
	public Rectangle parseString(final String key, final ResourceMap resourceMap)
		throws ResourceConverterException
	{
		List<Double> xywh = parseDoubles(key, 4, "Invalid x,y,width,height Rectangle string");

		Rectangle rectangle = new Rectangle();
		rectangle.setFrame(xywh.get(0).doubleValue(), xywh.get(1).doubleValue(), xywh.get(2)
				.doubleValue(), xywh.get(3).doubleValue());

		return rectangle;
	}
}
