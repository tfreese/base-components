package de.freese.base.mvc.process;

import de.freese.base.swing.exception.ReleaseVetoException;

/**
 * Interface eines BusinessProcesses.
 *
 * @author Thomas Freese
 */
public interface BusinessProcess
{
    /**
     * Initialisiert den Prozess.
     */
    public void initialize();

    /**
     * Prüfung, ob das Release durchgeführt werden kann.
     *
     * @throws ReleaseVetoException Falls was schief geht.
     */
    public void prepareRelease() throws ReleaseVetoException;

    /**
     * Freigeben verwendeter Resourcen.
     */
    public void release();

    /**
     * Reload von Daten.
     */
    public void reload();
}
