package de.freese.base.swing.components.label;

import javax.swing.Icon;

/**
 * @author Thomas Freese
 * @since 24.07.2025
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
