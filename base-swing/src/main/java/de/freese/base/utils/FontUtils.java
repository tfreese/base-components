package de.freese.base.utils;

import java.awt.Font;
import java.io.InputStream;

/**
 * @author Thomas Freese
 * @since 30.06.2011
 */
public final class FontUtils {
    private static Font symbolFont;

    public static Font getSymbolFont() {
        if (symbolFont == null) {
            try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fonts/MARVOSYM.TTF")) {
                // Font für die Symbole laden.
                symbolFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            }
            catch (final Exception ex) {
                if (ex instanceof final RuntimeException re) {
                    throw re;
                }

                throw new RuntimeException(ex);
            }
        }

        return symbolFont;
    }

    private FontUtils() {
        super();
    }
}
