/**
 * Created: 30.06.2011
 */

package de.freese.base.utils;

import java.awt.Font;
import java.io.InputStream;

/**
 * Diese Klasse enthält nur den Marvosym-Font für Symbole.
 *
 * @author Thomas Freese
 */
public final class FontUtils
{
    /**
     *
     */
    private static Font symbolFont;

    /**
     * Font für die Symbole
     *
     * @return {@link Font}
     */
    public static Font getSymbolFont()
    {
        if (symbolFont == null)
        {
            try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fonts/MARVOSYM.TTF"))
            {
                // Font für die Symbole laden
                symbolFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return symbolFont;
    }

    /**
     * Erstellt ein neues {@link FontUtils} Object.
     */
    private FontUtils()
    {
        super();
    }
}
