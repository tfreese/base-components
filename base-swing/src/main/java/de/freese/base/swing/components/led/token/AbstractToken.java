// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.freese.base.swing.components.led.LedMatrix;

/**
 * @author Thomas Freese
 */
public abstract class AbstractToken implements Token {
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

    @Override
    public List<byte[]> getBitMasks() {
        return bitMasks;
    }

    @Override
    public Color getColor() {
        return color;
    }

    protected void addBitMask(final byte[] bitMask) {
        if (bitMasks == null) {
            bitMasks = new ArrayList<>();
        }

        bitMasks.add(bitMask);
    }

    protected void createBitMasks(final CharSequence displayValue) {
        this.displayValue = displayValue;

        bitMasks = new ArrayList<>(displayValue.length());

        for (int i = 0; i < displayValue.length(); i++) {
            final char c = displayValue.charAt(i);
            byte[] bitMask = LedMatrix.getBitMask(String.valueOf(c));

            if (bitMask == null) {
                bitMask = LedMatrix.getBitMask("?");
            }

            bitMasks.add(bitMask);
        }
    }

    protected CharSequence getDisplayValue() {
        return displayValue;
    }

    protected void setDisplayValue(final CharSequence displayValue) {
        this.displayValue = displayValue;
    }
}
