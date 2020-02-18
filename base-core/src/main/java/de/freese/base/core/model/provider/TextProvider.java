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
public interface TextProvider
{
	/**
	 * Liefert den String des Objektes oder null.
	 * 
	 * @param object Object
	 * @return {@link Image}
	 */
	public String getText(Object object);

	/**
	 * Liefert den Tooltip des Objektes oder null.
	 * 
	 * @param object Object
	 * @return {@link Image}
	 */
	public String getTooltip(Object object);
}
