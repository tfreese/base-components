package de.freese.base.mvc.process;

import de.freese.base.core.model.Initializeable;
import de.freese.base.core.release.ReleasePrepareable;
import de.freese.base.core.release.Releaseable;

/**
 * Interface eines BusinessProcesses.
 * 
 * @author Thomas Freese
 */
public interface BusinessProcess extends Initializeable, Releaseable, ReleasePrepareable
{
	/**
	 * Initialisiert von Daten.
	 */
	public void reload();
}
