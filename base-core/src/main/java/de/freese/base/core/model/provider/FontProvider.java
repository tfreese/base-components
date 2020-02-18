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
public interface FontProvider
{
	/**
	 * Liefert den Font des Objektes oder null.
	 * 
	 * @param object Object
	 * @return {@link Font}
	 */
	public Font getFont(Object object);
}
