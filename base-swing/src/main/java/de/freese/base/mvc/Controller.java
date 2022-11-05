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
    String getName();

    ResourceMap getResourceMap();

    View getView();

    void handleException(Throwable throwable);

    void initialize();

    void release();
}
