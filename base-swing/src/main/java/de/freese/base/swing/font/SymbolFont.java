/**
 * Created: 30.06.2011
 */

package de.freese.base.swing.font;

import java.awt.Font;
import java.io.InputStream;
import javax.swing.JLabel;

/**
 * Diese Klasse enthält nur den Marvosym-Font für Symbole.
 *
 * @author Thomas Freese
 */
public final class SymbolFont
{
    /**
     * Font für die Symbole
     */
    public static Font FONT_SYMBOL = new JLabel().getFont();

    static
    {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fonts/MARVOSYM.TTF"))
        {
            // Font für die Symbole laden
            FONT_SYMBOL = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            // LOGGER.error(null, ex);
        }
    }

    /**
     * Erstellt ein neues {@link SymbolFont} Object.
     */
    private SymbolFont()
    {
        super();
    }
}
