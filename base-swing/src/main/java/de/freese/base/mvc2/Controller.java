// Created: 24.01.23
package de.freese.base.mvc2;

import java.awt.event.ActionListener;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public interface Controller
{
    Map<String, ActionListener> getInterestedActions();
}
