/**
 * Created: 24.11.2010
 */

/**
 * 
 */
package de.freese.base.core.model.provider;

import javax.swing.Icon;

/**
 * Interface für einen Icon-Lieferanten.
 * 
 * @author Thomas Freese
 */
public interface IconProvider
{
	/**
	 * Liefert das Icon des Objektes oder null.
	 * 
	 * @param object Object
	 * @return {@link Icon}
	 */
	public Icon getIcon(Object object);
}
