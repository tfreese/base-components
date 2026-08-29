package de.freese.base.swing;

import java.awt.Component;

/**
 * @author Thomas Freese
 * @since 22.11.2010
 */
@FunctionalInterface
public interface ComponentProvider {
    Component getComponent();
}
