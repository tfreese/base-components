// Created: 22.11.2010
package de.freese.base.swing;

import java.awt.Component;

/**
 * Interface fuer alle Objekte, welche eine {@link Component} liefern koennen.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ComponentProvider
{
    /**
     * Liefert die GUI Componente eines Objektes.
     *
     * @return {@link Component}
     */
    Component getComponent();
}
