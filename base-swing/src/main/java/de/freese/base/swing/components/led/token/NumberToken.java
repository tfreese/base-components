// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class NumberToken extends AbstractToken {
    private final String format;

    public NumberToken(final Color color, final Number value, final String format) {
        super(color);

        this.format = Objects.requireNonNull(format, "format required");

        setValue(value);
    }

    public NumberToken(final Number value, final String format) {
        super();

        this.format = Objects.requireNonNull(format, "format required");

        setValue(value);
    }

    @Override
    public void setValue(final Object value) {
        Objects.requireNonNull(value, "value required");

        if (value instanceof Number n) {
            final String displayValue = String.format(format, n);
            createBitMasks(displayValue);
        }
        else {
            throw new IllegalArgumentException("Number expected");
        }
    }
}
