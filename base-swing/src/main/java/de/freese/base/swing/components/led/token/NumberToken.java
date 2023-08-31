// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class NumberToken extends AbstractToken<Number> {
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
    public void setValue(final Number value) {
        String displayValue = String.format(this.format, value);

        createBitMasks(displayValue);
    }
}
