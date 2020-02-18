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
public interface ColorProvider
{
	/**
	 * Liefert den Background des Objektes oder null.
	 * 
	 * @param object Object
	 * @return {@link Color}
	 */
	public Color getBackground(Object object);

	/**
	 * Liefert den Foreground des Objektes oder null.
	 * 
	 * @param object Object
	 * @return {@link Color}
	 */
	public Color getForeground(Object object);
}
