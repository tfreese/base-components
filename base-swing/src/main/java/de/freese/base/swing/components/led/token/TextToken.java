// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class TextToken extends AbstractToken<CharSequence> {
    public TextToken(final CharSequence value) {
        super();

        setValue(value);
    }

    public TextToken(final Color color, final CharSequence value) {
        super(color);

        setValue(value);
    }

    @Override
    public void setValue(final CharSequence value) {
        final CharSequence displayValue = Objects.requireNonNull(value, "value required");

        createBitMasks(displayValue);
    }
}
