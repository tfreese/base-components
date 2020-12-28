// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.List;

/**
 * @author Thomas Freese
 * @param <V> Value Type
 */
public interface Token<V>
{
    // /**
    // * @return String
    // */
    // public String getDisplayValue();

    /**
     * BisMask für jedes darstellbare Zeichen.
     *
     * @return {@link List}
     */
    public List<byte[]> getBitMasks();

    /**
     * @return {@link Color}
     */
    public Color getColor();

    /**
     * @param value Object
     */
    public void setValue(V value);
}
