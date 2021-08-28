// Created: 27.05.2020
package de.freese.base.mvc;

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
    String getName();

    /**
     * Liefert die ResourceMap des Controllers.
     *
     * @return {@link ResourceMap}
     */
    ResourceMap getResourceMap();

    /**
     * Liefert die View des Controllers.
     *
     * @return {@link View}
     */
    View getView();

    /**
     * Fehlerbehandlung.
     *
     * @param throwable {@link Throwable}
     */
    void handleException(Throwable throwable);

    /**
     * Initialisiert den Controller.
     */
    void initialize();

    /**
     * Freigeben verwendeter Resourcen.
     */
    void release();
}
