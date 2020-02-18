/**
 * Created: 23.07.2011
 */

package de.freese.base.core.release;

/**
 * Vorbereitendes Interface fuer alles was Resourcen freigeben kann/muss.
 * 
 * @author Thomas Freese
 */
public interface ReleasePrepareable
{
	/**
	 * Pruefung, ob das Release durchgefuehrt werden kann.
	 * 
	 * @throws ReleaseVetoException Falls was schief geht.
	 */
	public void prepareRelease() throws ReleaseVetoException;
}
