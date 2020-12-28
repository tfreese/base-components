// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class TextToken extends AbstractToken<CharSequence>
{
    /**
     * Erstellt ein neues {@link TextToken} Object.
     *
     * @param value {@link CharSequence}
     */
    public TextToken(final CharSequence value)
    {
        super();

        setValue(value);
    }

    /**
     * Erstellt ein neues {@link TextToken} Object.
     *
     * @param color {@link Color}
     * @param value {@link CharSequence}
     */
    public TextToken(final Color color, final CharSequence value)
    {
        super(color);

        setValue(value);
    }

    /**
     * @see de.freese.base.swing.components.led.token.Token#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final CharSequence value)
    {
        CharSequence displayValue = Objects.requireNonNull(value, "value required");

        createBitMasks(displayValue);
    }
}
