// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.Objects;

import de.freese.base.swing.components.led.LedMatrix;

/**
 * @author Thomas Freese
 */
public class ArrowToken extends AbstractToken<ArrowToken.ArrowDirection> {
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
    public void setValue(final ArrowDirection value) {
        String displayValue = Objects.requireNonNull(value, "value required").name();
        setDisplayValue(displayValue);

        byte[] bitMask = LedMatrix.getBitMask(value);
        addBitMask(bitMask);
    }
}
