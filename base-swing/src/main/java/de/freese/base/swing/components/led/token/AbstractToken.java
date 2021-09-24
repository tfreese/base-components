// Created: 28.12.2020
package de.freese.base.swing.components.led.token;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.freese.base.swing.components.led.LedMatrix;

/**
 * @author Thomas Freese
 *
 * @param <V> Value Type
 */
public abstract class AbstractToken<V> implements Token<V>
{
    /**
     *
     */
    private List<byte[]> bitMasks;
    /**
     *
     */
    private Color color;
    /**
     *
     */
    private CharSequence displayValue;

    /**
     * Erstellt ein neues {@link AbstractToken} Object.
     */
    protected AbstractToken()
    {
        this(Color.WHITE);
    }

    /**
     * Erstellt ein neues {@link AbstractToken} Object.
     *
     * @param color {@link Color}
     */
    protected AbstractToken(final Color color)
    {
        super();

        this.color = color;
    }

    /**
     * @param bitMask byte[]
     */
    protected void addBitMask(final byte[] bitMask)
    {
        if (this.bitMasks == null)
        {
            this.bitMasks = new ArrayList<>();
        }

        this.bitMasks.add(bitMask);
    }

    /**
     * @param displayValue {@link CharSequence}
     */
    protected void createBitMasks(final CharSequence displayValue)
    {
        this.displayValue = displayValue;

        this.bitMasks = new ArrayList<>(displayValue.length());

        for (int i = 0; i < displayValue.length(); i++)
        {
            byte[] bitMask = LedMatrix.getBitMask(String.valueOf(displayValue.charAt(i)));

            if (bitMask == null)
            {
                bitMask = LedMatrix.getBitMask("?");
            }

            this.bitMasks.add(bitMask);
        }
    }

    /**
     * @see de.freese.base.swing.components.led.token.Token#getBitMasks()
     */
    @Override
    public List<byte[]> getBitMasks()
    {
        return this.bitMasks;
    }

    /**
     * @see de.freese.base.swing.components.led.token.Token#getColor()
     */
    @Override
    public Color getColor()
    {
        return this.color;
    }

    /**
     * @return {@link CharSequence}
     */
    protected CharSequence getDisplayValue()
    {
        return this.displayValue;
    }

    /**
     * @param displayValue {@link CharSequence}
     */
    protected void setDisplayValue(final CharSequence displayValue)
    {
        this.displayValue = displayValue;
    }
}
