// Created: 22.11.2010
package de.freese.base.swing;

import java.awt.Component;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ComponentProvider {
    Component getComponent();
}
