package de.freese.base.resourcemap.converter;

import java.awt.Color;

/**
 * An improved version of Color.decode() that supports colors with an alpha channel and comma separated RGB[A] values.<br>
 * Legal format for color resources are: "#RRGGBB", "#AARRGGBB", "R, G, B", "R, G, B, A".
 *
 * @author Thomas Freese
 */
public class ColorResourceConverter extends AbstractResourceConverter<Color> {
    @Override
    public Color convert(final String key, final String value) {
        Color color = null;

        if (value.startsWith("#")) {
            switch (value.length()) {
                // RGB/hex color
                case 7 -> color = Color.decode(value);

                // ARGB/hex color
                case 9 -> {
                    final int alpha = Integer.decode(value.substring(0, 3));
                    final int rgb = Integer.decode("#" + value.substring(3));
                    color = new Color((alpha << 24) | rgb, true);
                }
                default -> throwException(key, value, "invalid #RRGGBB or #AARRGGBB color string");
            }
        }
        else {
            final String[] parts = value.split(",");

            if (parts.length < 3 || parts.length > 4) {
                throwException(key, value, "invalid R, G, B[, A] color string");
            }

            try {
                // with alpha component
                if (parts.length == 4) {
                    final int r = Integer.parseInt(parts[0].strip());
                    final int g = Integer.parseInt(parts[1].strip());
                    final int b = Integer.parseInt(parts[2].strip());
                    final int a = Integer.parseInt(parts[3].strip());
                    color = new Color(r, g, b, a);
                }
                else {
                    final int r = Integer.parseInt(parts[0].strip());
                    final int g = Integer.parseInt(parts[1].strip());
                    final int b = Integer.parseInt(parts[2].strip());
                    color = new Color(r, g, b);
                }
            }
            catch (NumberFormatException _) {
                throwException(key, value, "invalid R, G, B[, A] color string");
            }
        }

        return color;
    }
}
