/**
 * Created: 27.05.2020
 */

package de.freese.base.mvc.controller;

import java.awt.Component;
import de.freese.base.mvc.view.View;
import de.freese.base.resourcemap.ResourceMap;

/**
 * Interface eines Controllers.
 *
 * @author Thomas Freese
 */
public interface Controller
{
    /**
     * Liefert den Namen des Controllers.
     *
     * @return String
     */
    public String getName();

    /**
     * Liefert die ResourceMap des Controllers.
     *
     * @return {@link ResourceMap}
     */
    public ResourceMap getResourceMap();

    /**
     * Liefert die View des Controllers.
     *
     * @return {@link View}
     */
    public <C extends Component> View<C> getView();

    /**
     * Fehlerbehandlung.
     *
     * @param throwable {@link Throwable}
     */
    public void handleException(Throwable throwable);

    /**
     * Initialisiert den Controller.
     */
    public void initialize();

    /**
     * Freigeben verwendeter Resourcen.
     */
    public void release();
}
