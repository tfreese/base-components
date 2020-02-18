/**
 * Created: 24.11.2010
 */

/**
 * 
 */
package de.freese.base.core.model.provider;

import java.awt.Font;

/**
 * Interface für einen Font-Lieferanten.
 * 
 * @author Thomas Freese
 */
public interface TableFontProvider
{
	/**
	 * Liefert den Font des Objektes oder null.
	 * 
	 * @param object Object
	 * @param column int
	 * @return {@link Font}
	 */
	public Font getFont(Object object, int column);
}
