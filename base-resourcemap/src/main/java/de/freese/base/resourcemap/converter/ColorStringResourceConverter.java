package de.freese.base.resourcemap.converter;

import java.awt.Color;

import de.freese.base.resourcemap.IResourceMap;

/**
 * Konvertiert einen Text in ein {@link Color} Object.<br>
 * An improved version of Color.decode() that supports colors with an alpha channel and comma
 * separated RGB[A] values. Legal format for color resources are: "#RRGGBB", "#AARRGGBB", "R, G, B",
 * "R, G, B, A".
 * 
 * @author Thomas Freese
 */
public class ColorStringResourceConverter extends AbstractResourceConverter<Color>
{
	/**
	 * Erstellt ein neues {@link ColorStringResourceConverter} Object.
	 */
	public ColorStringResourceConverter()
	{
		super();
	}

	/**
	 * @see de.freese.base.resourcemap.converter.IResourceConverter#parseString(java.lang.String,
	 *      de.freese.base.resourcemap.IResourceMap)
	 */
	@Override
	public Color parseString(final String key, final IResourceMap resourceMap)
		throws ResourceConverterException
	{
		Color color = null;

		if (key.startsWith("#"))
		{
			switch (key.length())
			{
				case 7:
					// RGB/hex color
					color = Color.decode(key);

					break;

				case 9:
					// ARGB/hex color
					int alpha = Integer.decode(key.substring(0, 3)).intValue();
					int rgb = Integer.decode("#" + key.substring(3)).intValue();
					color = new Color((alpha << 24) | rgb, true);

					break;
				default:
					throw new ResourceConverterException(
							"invalid #RRGGBB or #AARRGGBB color string", key);
			}
		}
		else
		{
			String[] parts = key.split(",");

			if ((parts.length < 3) || (parts.length > 4))
			{
				throw new ResourceConverterException("invalid R, G, B[, A] color string", key);
			}

			try
			{
				// with alpha component
				if (parts.length == 4)
				{
					int r = Integer.parseInt(parts[0].trim());
					int g = Integer.parseInt(parts[1].trim());
					int b = Integer.parseInt(parts[2].trim());
					int a = Integer.parseInt(parts[3].trim());
					color = new Color(r, g, b, a);
				}
				else
				{
					int r = Integer.parseInt(parts[0].trim());
					int g = Integer.parseInt(parts[1].trim());
					int b = Integer.parseInt(parts[2].trim());
					color = new Color(r, g, b);
				}
			}
			catch (NumberFormatException ex)
			{
				throw new ResourceConverterException("invalid R, G, B[, A] color string", key, ex);
			}
		}

		return color;
	}
}
