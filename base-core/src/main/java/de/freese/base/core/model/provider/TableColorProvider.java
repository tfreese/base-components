/**
 * Created: 24.11.2010
 */

/**
 * 
 */
package de.freese.base.core.model.provider;

import java.awt.Color;

/**
 * Interface für einen Color-Lieferanten.
 * 
 * @author Thomas Freese
 */
public interface TableColorProvider
{
	/**
	 * Liefert den Background des Objektes oder null.
	 * 
	 * @param object Object
	 * @param column int
	 * @return {@link Color}
	 */
	public Color getBackground(Object object, int column);

	/**
	 * Liefert den Foreground des Objektes oder null.
	 * 
	 * @param object Object
	 * @param column int
	 * @return {@link Color}
	 */
	public Color getForeground(Object object, int column);
}
