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
    public Color getColorBackground();

    /**
     * @return {@link Color}
     */
    public Color getColorBackgroundDot();

    /**
     * @return int
     */
    public int getDotHeight();

    /**
     * @return int
     */
    public int getDotWidth();

    /**
     * @return int
     */
    public int getHgap();

    /**
     * @return int
     */
    public int getTokenGap();

    /**
     * @return int
     */
    public int getVgap();
}
