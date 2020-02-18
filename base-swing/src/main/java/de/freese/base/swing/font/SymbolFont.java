/**
 * Created: 30.06.2011
 */

package de.freese.base.swing.font;

import java.awt.Font;

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
		try
		{
			// Font für die Symbole laden
			FONT_SYMBOL =
					Font.createFont(Font.TRUETYPE_FONT, Thread.currentThread()
							.getContextClassLoader().getResourceAsStream("fonts/MARVOSYM.TTF"));
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
