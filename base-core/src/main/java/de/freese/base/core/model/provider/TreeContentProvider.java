/**
 * Created: 24.11.2010
 */

/**
 * 
 */
package de.freese.base.core.model.provider;

import java.util.List;

/**
 * Interface für einen TreeModel-Lieferanten.
 * 
 * @author Thomas Freese
 */
public interface TreeContentProvider
{
	/**
	 * Liefert alle Childs des Parents.
	 * 
	 * @param parent {@link List}
	 * @return Object[]
	 */
	public List<?> getChilds(Object parent);

	/**
	 * Liefert den Pfad vom Root zum Objekt.
	 * 
	 * @param object Object
	 * @return Object[]
	 */
	public Object[] getPathToRoot(final Object object);

	/**
	 * Liefert den Parent des Childs.
	 * 
	 * @param child Object
	 * @return Object
	 */
	public Object getParent(Object child);
}
