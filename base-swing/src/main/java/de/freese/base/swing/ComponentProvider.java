// Created: 22.11.2010
package de.freese.base.swing;

import java.awt.Component;

/**
 * Interface für alle Objekte, welche eine {@link Component} liefern können.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ComponentProvider {
    Component getComponent();
}
