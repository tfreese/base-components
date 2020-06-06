package de.freese.base.resourcemap.converter;

import java.awt.Color;

/**
 * Konvertiert einen Text in ein {@link Color} Object.<br>
 * An improved version of Color.decode() that supports colors with an alpha channel and comma separated RGB[A] values. Legal format for color resources are:
 * "#RRGGBB", "#AARRGGBB", "R, G, B", "R, G, B, A".
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
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Color convert(final String key, final String value)
    {
        Color color = null;

        if (value.startsWith("#"))
        {
            switch (value.length())
            {
                case 7:
                    // RGB/hex color
                    color = Color.decode(value);

                    break;

                case 9:
                    // ARGB/hex color
                    int alpha = Integer.decode(value.substring(0, 3)).intValue();
                    int rgb = Integer.decode("#" + value.substring(3)).intValue();
                    color = new Color((alpha << 24) | rgb, true);

                    break;
                default:
                    throw new ResourceConverterException("invalid #RRGGBB or #AARRGGBB color string", value);
            }
        }
        else
        {
            String[] parts = value.split(",");

            if ((parts.length < 3) || (parts.length > 4))
            {
                throw new ResourceConverterException("invalid R, G, B[, A] color string", value);
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
