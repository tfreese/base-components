/**
 * Created: 23.07.2011
 */

package de.freese.base.core.release;

/**
 * Interface fuer alles was Resourcen freigeben kann/muss.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Releaseable
{
    /**
     * Freigeben verwendeter Resourcen.
     */
    public void release();
}
