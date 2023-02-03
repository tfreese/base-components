// Created: 24.01.23
package de.freese.base.mvc2;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public interface View
{
    Map<String, ActionListener> getInterestedMenuAndToolbarActions();

    Component initComponent(ApplicationContext applicationContext);
}
