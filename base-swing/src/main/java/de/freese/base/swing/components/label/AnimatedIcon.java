// Created: 24 Juli 2025
package de.freese.base.swing.components.label;

import javax.swing.Icon;

/**
 * @author Thomas Freese
 */
public interface AnimatedIcon extends Icon {
    /**
     * Calculate the next Content of the Animation.
     */
    void next();

    /**
     * Reset to defaults.
     */
    void reset();
}
