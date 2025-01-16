// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.Objects;

import de.freese.base.swing.components.led.LedMatrix;

/**
 * @author Thomas Freese
 */
public class ArrowToken extends AbstractToken {
    /**
     * @author Thomas Freese
     */
    public enum ArrowDirection {
        DOWN,
        LEFT,
        RIGHT,
        UNCHANGED,
        UP
    }

    public ArrowToken(final ArrowToken.ArrowDirection value) {
        super();

        setValue(value);
    }

    public ArrowToken(final Color color, final ArrowToken.ArrowDirection value) {
        super(color);

        setValue(value);
    }

    @Override
    public void setValue(final Object value) {
        Objects.requireNonNull(value, "value required");

        if (value instanceof ArrowDirection ad) {
            final String displayValue = ad.name();
            setDisplayValue(displayValue);

            final byte[] bitMask = LedMatrix.getBitMask(value);
            addBitMask(bitMask);
        }
        else {
            throw new IllegalArgumentException("Number expected");
        }
    }
}
