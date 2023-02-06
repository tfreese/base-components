// Created: 24.01.23
package de.freese.base.mvc2.view;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Map;

import de.freese.base.mvc2.ApplicationContext;

/**
 * @author Thomas Freese
 */
public interface View
{
    Component getComponent();

    default Map<String, ActionListener> getInterestedMenuAndToolbarActions()
    {
        return Collections.emptyMap();
    }

    void handleException(Throwable throwable);

    View initComponent(ApplicationContext applicationContext);
}
