/**
 * Created: 24.11.2010
 */

/**
 * 
 */
package de.freese.base.core.model.provider;

/**
 * Interface für einen TableContent-Lieferanten.
 * 
 * @author Thomas Freese
 */
public interface TableContentProvider
{
	/**
	 * Liefert den Inhalt fuer den Spaltenindex.
	 * 
	 * @param object Object
	 * @param column int
	 * @return Object
	 */
	public Object getValueAt(Object object, int column);
}
