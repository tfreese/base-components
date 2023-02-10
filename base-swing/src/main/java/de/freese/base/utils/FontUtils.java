// Created: 30.06.2011
package de.freese.base.utils;

import java.awt.Font;
import java.io.InputStream;

/**
 * @author Thomas Freese
 */
public final class FontUtils {
    private static Font symbolFont;

    public static Font getSymbolFont() {
        if (symbolFont == null) {
            try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fonts/MARVOSYM.TTF")) {
                // Font für die Symbole laden
                symbolFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return symbolFont;
    }

    private FontUtils() {
        super();
    }
}
