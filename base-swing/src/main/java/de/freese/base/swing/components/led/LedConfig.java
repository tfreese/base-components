// Created: 28.12.2020
package de.freese.base.swing.components.led;

import java.awt.Color;

import de.freese.base.swing.components.led.element.Element;

/**
 * @author Thomas Freese
 */
public interface LedConfig extends Element
{
    /**
     * @return {@link Color}
     */
    Color getColorBackground();

    /**
     * @return {@link Color}
     */
    Color getColorBackgroundDot();

    /**
     * @return int
     */
    int getDotHeight();

    /**
     * @return int
     */
    int getDotWidth();

    /**
     * @return int
     */
    int getHgap();

    /**
     * @return int
     */
    int getTokenGap();

    /**
     * @return int
     */
    int getVgap();
}
