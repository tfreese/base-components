package de.freese.base.resourcemap.converter;

import java.awt.Color;

/**
 * An improved version of Color.decode() that supports colors with an alpha channel and comma separated RGB[A] values.<br>
 * Legal format for color resources are: "#RRGGBB", "#AARRGGBB", "R, G, B", "R, G, B, A".
 *
 * @author Thomas Freese
 */
public class ColorStringResourceConverter extends AbstractResourceConverter<Color>
{
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
                    int alpha = Integer.decode(value.substring(0, 3));
                    int rgb = Integer.decode("#" + value.substring(3));
                    color = new Color((alpha << 24) | rgb, true);

                    break;
                default:
                    throwException(key, value, "invalid #RRGGBB or #AARRGGBB color string");
            }
        }
        else
        {
            String[] parts = value.split(",");

            if ((parts.length < 3) || (parts.length > 4))
            {
                throwException(key, value, "invalid R, G, B[, A] color string");
            }

            try
            {
                // with alpha component
                if (parts.length == 4)
                {
                    int r = Integer.parseInt(parts[0].strip());
                    int g = Integer.parseInt(parts[1].strip());
                    int b = Integer.parseInt(parts[2].strip());
                    int a = Integer.parseInt(parts[3].strip());
                    color = new Color(r, g, b, a);
                }
                else
                {
                    int r = Integer.parseInt(parts[0].strip());
                    int g = Integer.parseInt(parts[1].strip());
                    int b = Integer.parseInt(parts[2].strip());
                    color = new Color(r, g, b);
                }
            }
            catch (NumberFormatException ex)
            {
                throwException(key, value, "invalid R, G, B[, A] color string");
            }
        }

        return color;
    }
}
