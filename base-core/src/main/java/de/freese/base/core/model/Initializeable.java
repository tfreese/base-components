/**
 * Created: 23.07.2011
 */

package de.freese.base.core.model;

/**
 * Interface fuer alles was initialisiert werden kann.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Initializeable
{
    /**
     * Initialisiert das Object.
     */
    public void initialize();
}
