/**
 * Created: 24.11.2010
 */

/**
 * 
 */
package de.freese.base.core.model.provider;

import java.awt.Image;

/**
 * Interface für einen Label-Lieferanten.
 * 
 * @author Thomas Freese
 */
public interface TableLabelProvider
{
	/**
	 * Liefert den String des Objektes oder null.
	 * 
	 * @param object Object
	 * @param column int
	 * @return {@link Image}
	 */
	public String getText(Object object, int column);

	/**
	 * Liefert den Tooltip des Objektes oder null.
	 * 
	 * @param object Object
	 * @param column int
	 * @return {@link Image}
	 */
	public String getTooltip(Object object, int column);
}
