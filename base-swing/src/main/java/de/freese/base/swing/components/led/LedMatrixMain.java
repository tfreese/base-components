// Created: 13.11.22
package de.freese.base.swing.components.led;

import java.util.Arrays;

/**
 * @author Thomas Freese
 */
public final class LedMatrixMain
{
    public static void main(final String[] args)
    {
        // Dots für das 'A', am besten in Excel eintragen und kopieren.
        // byte[][] rasterBytes = new byte[7][5];
        byte[][] ledDots =
                {
                        {
                                0, 1, 1, 1, 0
                        },
                        {
                                1, 0, 0, 0, 1
                        },
                        {
                                1, 0, 0, 0, 1
                        },
                        {
                                1, 1, 1, 1, 1
                        },
                        {
                                1, 0, 0, 0, 1
                        },
                        {
                                1, 0, 0, 0, 1
                        },
                        {
                                1, 0, 0, 0, 1
                        }
                };

        System.out.printf("Buchstabe 'A': BitMask = %s%n", Arrays.toString(LedMatrix.getTokenBitMask(ledDots)));
    }

    private LedMatrixMain()
    {
        super();
    }
}
