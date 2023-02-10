// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.freese.base.swing.components.led.LedMatrix;

/**
 * @param <V> Value Type
 *
 * @author Thomas Freese
 */
public abstract class AbstractToken<V> implements Token<V> {
    private final Color color;

    private List<byte[]> bitMasks;

    private CharSequence displayValue;

    protected AbstractToken() {
        this(Color.WHITE);
    }

    protected AbstractToken(final Color color) {
        super();

        this.color = color;
    }

    /**
     * @see de.freese.base.swing.components.led.token.Token#getBitMasks()
     */
    @Override
    public List<byte[]> getBitMasks() {
        return this.bitMasks;
    }

    /**
     * @see de.freese.base.swing.components.led.token.Token#getColor()
     */
    @Override
    public Color getColor() {
        return this.color;
    }

    protected void addBitMask(final byte[] bitMask) {
        if (this.bitMasks == null) {
            this.bitMasks = new ArrayList<>();
        }

        this.bitMasks.add(bitMask);
    }

    protected void createBitMasks(final CharSequence displayValue) {
        this.displayValue = displayValue;

        this.bitMasks = new ArrayList<>(displayValue.length());

        for (int i = 0; i < displayValue.length(); i++) {
            byte[] bitMask = LedMatrix.getBitMask(String.valueOf(displayValue.charAt(i)));

            if (bitMask == null) {
                bitMask = LedMatrix.getBitMask("?");
            }

            this.bitMasks.add(bitMask);
        }
    }

    protected CharSequence getDisplayValue() {
        return this.displayValue;
    }

    protected void setDisplayValue(final CharSequence displayValue) {
        this.displayValue = displayValue;
    }
}
