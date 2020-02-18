/**
 * Created: 24.11.2010
 */

/**
 * 
 */
package de.freese.base.core.model.provider;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Interface für einen Image-Lieferanten.
 * 
 * @author Thomas Freese
 */
public interface TableIconProvider
{
	/**
	 * Liefert das Image des Objektes oder null.
	 * 
	 * @param object Object
	 * @param column int
	 * @return {@link ImageIcon}
	 */
	public Icon getIcon(Object object, int column);
}
