/**
 * Created: 17.07.2011
 */

package de.freese.base.mvc;

import de.freese.base.core.model.Initializeable;
import de.freese.base.core.model.NameProvider;
import de.freese.base.core.release.ReleasePrepareable;
import de.freese.base.core.release.Releaseable;
import de.freese.base.resourcemap.IResourceMap;
import de.freese.base.swing.ComponentProvider;

/**
 * Interface fuer ein Plugin des MVC Frameworks.
 * 
 * @author Thomas Freese
 */
public interface MVCPlugin extends NameProvider, ComponentProvider, Initializeable,
		Releaseable, ReleasePrepareable
{
	/**
	 * Liefert die ResourceMap des Plugins.
	 * 
	 * @return {@link IResourceMap}
	 */
	public IResourceMap getResourceMap();

	/**
	 * Setzt die Application.
	 * 
	 * @param application {@link AbstractApplication}
	 */
	public void setApplication(AbstractApplication application);
}
