/**
 * Created: 17.07.2011
 */

package de.freese.base.mvc;

import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.ComponentProvider;
import de.freese.base.swing.exception.ReleaseVetoException;

/**
 * Interface fuer ein Plugin des MVC Frameworks.
 *
 * @author Thomas Freese
 */
public interface MVCPlugin extends ComponentProvider
{
    /**
     * Liefert den Namen des PlugIns.
     *
     * @return String
     */
    public String getName();

    /**
     * Liefert die ResourceMap des PlugIns.
     *
     * @return {@link ResourceMap}
     */
    public ResourceMap getResourceMap();

    /**
     * Initialisiert das PlugIns.
     */
    public void initialize();

    /**
     * Pruefung, ob das Release durchgefuehrt werden kann.
     *
     * @throws ReleaseVetoException Falls was schief geht.
     */
    public void prepareRelease() throws ReleaseVetoException;

    /**
     * Freigeben verwendeter Resourcen.
     */
    public void release();

    /**
     * Setzt die Application.
     *
     * @param application {@link AbstractApplication}
     */
    public void setApplication(AbstractApplication application);
}
