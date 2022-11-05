// Created: 28.12.2020
package de.freese.base.swing.components.led;

import java.awt.Color;

import de.freese.base.swing.components.led.element.Element;

/**
 * @author Thomas Freese
 */
public interface LedConfig extends Element
{
    Color getColorBackground();

    Color getColorBackgroundDot();

    int getDotHeight();

    int getDotWidth();

    int getHgap();

    int getTokenGap();

    int getVgap();
}
