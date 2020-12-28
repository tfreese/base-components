// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.Formatter;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class NumberToken extends AbstractToken<Number>
{
    /**
     *
     */
    private String format;

    /**
     * Erstellt ein neues {@link NumberToken} Object.
     *
     * @param color {@link Color}
     * @param value Number
     * @param format String; siehe {@link Formatter}
     */
    public NumberToken(final Color color, final Number value, final String format)
    {
        super(color);

        this.format = Objects.requireNonNull(format, "format required");

        setValue(value);
    }

    /**
     * Erstellt ein neues {@link NumberToken} Object.
     *
     * @param value Number
     * @param format String; siehe {@link Formatter}
     */
    public NumberToken(final Number value, final String format)
    {
        super();

        this.format = Objects.requireNonNull(format, "format required");

        setValue(value);
    }

    /**
     * @see de.freese.base.swing.components.led.token.Token#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final Number value)
    {
        String displayValue = String.format(this.format, value);

        createBitMasks(displayValue);
    }
}
