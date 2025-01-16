// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.List;

/**
 * @author Thomas Freese
 */
public interface Token {
    // String getDisplayValue();

    List<byte[]> getBitMasks();

    Color getColor();

    void setValue(Object value);
}
