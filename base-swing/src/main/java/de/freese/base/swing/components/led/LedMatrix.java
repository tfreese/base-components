// Created: 28.12.2020
package de.freese.base.swing.components.led;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Painter;

import de.freese.base.swing.components.led.token.ArrowToken;
import de.freese.base.swing.components.led.token.Token;

/**
 * @author Thomas Freese
 */
public class LedMatrix implements Painter<LedConfig>
{
    /**
     *
     */
    private static final Map<Object, byte[]> bitMaskMap = new HashMap<>();

    static
    {
        addBitMask(" ", new byte[]
                {
                        0, 0, 0, 0, 0
                });
        addBitMask("A", new byte[]
                {
                        126, 9, 9, 9, 126
                });
        addBitMask("a", new byte[]
                {
                        32, 84, 84, 84, 120
                });
        addBitMask("B", new byte[]
                {
                        127, 73, 73, 73, 62
                });
        addBitMask("b", new byte[]
                {
                        127, 68, 68, 68, 56
                });
        addBitMask("C", new byte[]
                {
                        62, 65, 65, 65, 34
                });
        addBitMask("c", new byte[]
                {
                        56, 68, 68, 68, 0
                });
        addBitMask("D", new byte[]
                {
                        65, 127, 65, 65, 62
                });
        addBitMask("d", new byte[]
                {
                        56, 68, 68, 72, 127
                });
        addBitMask("E", new byte[]
                {
                        127, 73, 73, 65, 65
                });
        addBitMask("e", new byte[]
                {
                        56, 84, 84, 84, 24
                });
        addBitMask("F", new byte[]
                {
                        127, 9, 9, 1, 1
                });
        addBitMask("f", new byte[]
                {
                        8, 126, 9, 1, 2
                });
        addBitMask("G", new byte[]
                {
                        62, 65, 65, 73, 58
                });
        addBitMask("g", new byte[]
                {
                        72, 84, 84, 84, 60
                });
        addBitMask("H", new byte[]
                {
                        127, 8, 8, 8, 127
                });
        addBitMask("h", new byte[]
                {
                        127, 8, 4, 4, 120
                });
        addBitMask("I", new byte[]
                {
                        0, 65, 127, 65, 0
                });
        addBitMask("i", new byte[]
                {
                        0, 68, 125, 64, 0
                });
        addBitMask("J", new byte[]
                {
                        32, 64, 65, 63, 1
                });
        addBitMask("j", new byte[]
                {
                        32, 64, 68, 61, 0
                });
        addBitMask("K", new byte[]
                {
                        127, 8, 20, 34, 65
                });
        addBitMask("k", new byte[]
                {
                        127, 16, 40, 68, 0
                });
        addBitMask("L", new byte[]
                {
                        127, 64, 64, 64, 64
                });
        addBitMask("l", new byte[]
                {
                        0, 65, 127, 64, 0
                });
        addBitMask("M", new byte[]
                {
                        127, 2, 12, 2, 127
                });
        addBitMask("m", new byte[]
                {
                        124, 4, 24, 4, 120
                });
        addBitMask("N", new byte[]
                {
                        127, 4, 8, 16, 127
                });
        addBitMask("n", new byte[]
                {
                        124, 8, 4, 4, 120
                });
        addBitMask("O", new byte[]
                {
                        62, 65, 65, 65, 62
                });
        addBitMask("o", new byte[]
                {
                        56, 68, 68, 68, 56
                });
        addBitMask("P", new byte[]
                {
                        127, 9, 9, 9, 6
                });
        addBitMask("p", new byte[]
                {
                        124, 20, 20, 20, 8
                });
        addBitMask("Q", new byte[]
                {
                        62, 65, 81, 33, 94
                });
        addBitMask("q", new byte[]
                {
                        8, 20, 20, 20, 124
                });
        addBitMask("R", new byte[]
                {
                        127, 9, 25, 41, 70
                });
        addBitMask("r", new byte[]
                {
                        124, 8, 4, 4, 8
                });
        addBitMask("S", new byte[]
                {
                        38, 73, 73, 73, 50
                });
        addBitMask("s", new byte[]
                {
                        72, 84, 84, 84, 32
                });
        addBitMask("T", new byte[]
                {
                        1, 1, 127, 1, 1
                });
        addBitMask("t", new byte[]
                {
                        4, 63, 68, 64, 64
                });
        addBitMask("U", new byte[]
                {
                        63, 64, 64, 64, 63
                });
        addBitMask("u", new byte[]
                {
                        60, 64, 64, 32, 124
                });
        addBitMask("V", new byte[]
                {
                        7, 24, 96, 24, 7
                });
        addBitMask("v", new byte[]
                {
                        28, 32, 64, 32, 28
                });
        addBitMask("W", new byte[]
                {
                        127, 32, 24, 32, 127
                });
        addBitMask("w", new byte[]
                {
                        60, 64, 48, 64, 60
                });
        addBitMask("X", new byte[]
                {
                        99, 20, 8, 20, 99
                });
        addBitMask("x", new byte[]
                {
                        68, 40, 16, 40, 68
                });
        addBitMask("Y", new byte[]
                {
                        7, 8, 120, 8, 7
                });
        addBitMask("y", new byte[]
                {
                        12, 80, 80, 80, 60
                });
        addBitMask("Z", new byte[]
                {
                        97, 81, 73, 69, 67
                });
        addBitMask("z", new byte[]
                {
                        68, 100, 84, 76, 68
                });
        addBitMask("0", new byte[]
                {
                        62, 81, 73, 69, 62
                });
        addBitMask("1", new byte[]
                {
                        0, 66, 127, 64, 0
                });
        addBitMask("2", new byte[]
                {
                        98, 81, 81, 73, 70
                });
        addBitMask("3", new byte[]
                {
                        34, 65, 73, 73, 54
                });
        addBitMask("4", new byte[]
                {
                        24, 20, 18, 127, 16
                });
        addBitMask("5", new byte[]
                {
                        39, 69, 69, 69, 57
                });
        addBitMask("6", new byte[]
                {
                        60, 74, 73, 73, 49
                });
        addBitMask("7", new byte[]
                {
                        1, 113, 9, 5, 3
                });
        addBitMask("8", new byte[]
                {
                        54, 73, 73, 73, 54
                });
        addBitMask("9", new byte[]
                {
                        70, 73, 73, 41, 30
                });
        addBitMask("~", new byte[]
                {
                        2, 1, 2, 4, 2
                });
        addBitMask("`", new byte[]
                {
                        1, 2, 4, 0, 0
                });
        addBitMask("!", new byte[]
                {
                        0, 0, 111, 0, 0
                });
        addBitMask("@", new byte[]
                {
                        62, 65, 93, 85, 14
                });
        addBitMask("#", new byte[]
                {
                        20, 127, 20, 127, 20
                });
        addBitMask("$", new byte[]
                {
                        44, 42, 127, 42, 26
                });
        addBitMask("%", new byte[]
                {
                        38, 22, 8, 52, 50
                });
        addBitMask("^", new byte[]
                {
                        4, 2, 1, 2, 4
                });
        addBitMask("&", new byte[]
                {
                        54, 73, 86, 32, 80
                });
        addBitMask("*", new byte[]
                {
                        42, 28, 127, 28, 42
                });
        addBitMask("(", new byte[]
                {
                        0, 0, 62, 65, 0
                });
        addBitMask(")", new byte[]
                {
                        0, 65, 62, 0, 0
                });
        addBitMask("-", new byte[]
                {
                        8, 8, 8, 8, 8
                });
        addBitMask("_", new byte[]
                {
                        64, 64, 64, 64, 64
                });
        addBitMask("+", new byte[]
                {
                        8, 8, 127, 8, 8
                });
        addBitMask("=", new byte[]
                {
                        36, 36, 36, 36, 36
                });
        addBitMask("\\", new byte[]
                {
                        3, 4, 8, 16, 96
                });
        addBitMask("|", new byte[]
                {
                        0, 0, 127, 0, 0
                });
        addBitMask("{", new byte[]
                {
                        0, 8, 54, 65, 65
                });
        addBitMask("}", new byte[]
                {
                        65, 65, 54, 8, 0
                });
        addBitMask("[", new byte[]
                {
                        0, 127, 65, 65, 0
                });
        addBitMask("]", new byte[]
                {
                        0, 65, 65, 127, 0
                });
        addBitMask(":", new byte[]
                {
                        0, 0, 54, 54, 0
                });
        addBitMask(";", new byte[]
                {
                        0, 91, 59, 0, 0
                });
        addBitMask(",", new byte[]
                {
                        0, 0, 88, 56, 0
                });
        addBitMask(".", new byte[]
                {
                        0, 96, 96, 0, 0
                });
        addBitMask("<", new byte[]
                {
                        8, 20, 34, 65, 0
                });
        addBitMask(">", new byte[]
                {
                        65, 34, 20, 8, 0
                });
        addBitMask("?", new byte[]
                {
                        2, 1, 89, 5, 2
                });
        addBitMask("/", new byte[]
                {
                        96, 16, 8, 4, 3
                });
        addBitMask("'", new byte[]
                {
                        0, 0, 7, 0, 0
                });
        addBitMask("\"", new byte[]
                {
                        0, 7, 0, 7, 0
                });
        addBitMask(ArrowToken.ArrowDirection.UP, new byte[]
                {
                        16, 24, 28, 24, 16
                });
        addBitMask(ArrowToken.ArrowDirection.UNCHANGED, new byte[]
                {
                        8, 28, 28, 28, 8
                });
        addBitMask(ArrowToken.ArrowDirection.DOWN, new byte[]
                {
                        4, 12, 28, 12, 4
                });
        addBitMask(ArrowToken.ArrowDirection.LEFT, new byte[]
                {
                        0, 8, 28, 62, 0
                });
        addBitMask(ArrowToken.ArrowDirection.RIGHT, new byte[]
                {
                        0, 62, 28, 8, 0
                });
    }

    /**
     * @param object Object
     * @param bitMask byte[]
     */
    public static void addBitMask(final Object object, final byte[] bitMask)
    {
        bitMaskMap.put(object, bitMask);
    }

    /**
     * @param object Object
     *
     * @return byte[]
     */
    public static byte[] getBitMask(final Object object)
    {
        return bitMaskMap.get(object);
    }

    /**
     * Ermittelt aus den LED-Dots, die darstellbaren LED-Punkte, die BitMask für das Painting.<br>
     * Beispiel: Buchstabe 'A'; 0 = kein Dot, 1 = Dot<br>
     *
     * <pre>
     * byte[][] ledDots =
     * {
     *         {
     *                 0, 1, 1, 1, 0
     *         },
     *         {
     *                 1, 0, 0, 0, 1
     *         },
     *         {
     *                 1, 0, 0, 0, 1
     *         },
     *         {
     *                 1, 1, 1, 1, 1
     *         },
     *         {
     *                 1, 0, 0, 0, 1
     *         },
     *         {
     *                 1, 0, 0, 0, 1
     *         },
     *         {
     *                 1, 0, 0, 0, 1
     *         }
     * };
     * </pre>
     *
     * @param ledDots byte[][]
     *
     * @return byte[]
     */
    public static byte[] getTokenBitMask(final byte[][] ledDots)
    {
        byte[] bitMask = new byte[ledDots[0].length];

        for (int col = 0; col < ledDots[0].length; col++)
        {
            // Bitmask pro Spalte anlegen.
            byte mask = 0;

            for (int row = 0; row < ledDots.length; row++)
            {
                byte b = ledDots[row][col];

                mask |= (b << row);
            }

            bitMask[col] = mask;
        }

        return bitMask;
    }

    /**
     * @param args String[]
     */
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

        System.out.printf("Buchstabe 'A': BitMask = %s%n", Arrays.toString(getTokenBitMask(ledDots)));
    }

    /**
     * Erstellt ein neues {@link LedMatrix} Object.
     */
    public LedMatrix()
    {
        super();
    }

    /**
     * @see javax.swing.Painter#paint(java.awt.Graphics2D, java.lang.Object, int, int)
     */
    @Override
    public void paint(final Graphics2D g, final LedConfig config, final int width, final int height)
    {
        configureGraphics(g, config);

        paintBackground(g, config, width, height);

        paintElement(g, config, width, height);
    }

    /**
     * @param g {@link Graphics2D}
     * @param config {@link LedConfig}
     * @param width int
     * @param height int
     */
    public void paintElement(final Graphics2D g, final LedConfig config, final int width, final int height)
    {
        int leftInset = config.getDotWidth() + config.getHgap();
        int x = leftInset;

        // TODO Hier Ansetzen für das Scrolling.
        // LinkedList<byte[]> des gesamten Elements.
        // byte[] mask = linkedList.remove(0);
        // linkedList.add(mask);

        for (Token<?> token : config.getTokens())
        {
            x = paintToken(g, config, width, height, token, x);

            if (x >= width)
            {
                break;
            }
        }
    }

    /**
     * @param g {@link Graphics2D}
     * @param config {@link LedConfig}
     */
    protected void configureGraphics(final Graphics2D g, final LedConfig config)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * @param g {@link Graphics2D}
     * @param config {@link LedConfig}
     * @param width int
     * @param height int
     */
    protected void paintBackground(final Graphics2D g, final LedConfig config, final int width, final int height)
    {
        int dotWidth = config.getDotWidth();
        int dotHeight = config.getDotHeight();
        int hGap = config.getHgap();
        int vGap = config.getVgap();

        // Nur die Linien zeichnen.
        g.setColor(config.getColorBackgroundDot());
        g.fillRect(0, 0, width, height);
        g.setColor(config.getColorBackground());

        for (int x = dotWidth; x < width; x += dotWidth + hGap)
        {
            g.fillRect(x, 0, hGap, height);
        }

        for (int y = dotHeight; y < height; y += dotHeight + vGap)
        {
            g.fillRect(0, y, width, vGap);
        }

        // // Jeden Dot zeichnen.
        // g.setColor(config.getColorBackground());
        // g.fillRect(0, 0, width, height);
        // g.setColor(config.getColorBackgroundDot());
        //
        // for (int x = 0; x < width; x += dotWidth + hGap)
        // {
        // for (int y = 0; y < height; y += dotHeight + vGap)
        // {
        // g.fillRect(x, y, dotWidth, dotHeight);
        // // g.fillOval(x, y, dotWidth, dotHeight);
        // // g.fillArc(x, y, dotWidth, dotHeight, 0, 360);
        // }
        // }
    }

    /**
     * @param g {@link Graphics2D}
     * @param config {@link LedConfig}
     * @param token {@link Token}
     * @param width int
     * @param height int
     * @param x int
     *
     * @return int
     */
    protected int paintToken(final Graphics2D g, final LedConfig config, final int width, final int height, final Token<?> token, int x)
    {
        int dotWidth = config.getDotWidth();
        int hGap = config.getHgap();
        int tokenGap = config.getTokenGap();

        Color color = token.getColor();
        g.setColor(color);

        for (byte[] bitMask : token.getBitMasks())
        {
            x = paintTokenDots(g, config, width, height, bitMask, x);

            if (x >= width)
            {
                break;
            }
        }

        x += (tokenGap * (hGap + dotWidth));

        return x;
    }

    /**
     * @param g {@link Graphics2D}
     * @param config {@link LedConfig}
     * @param width int
     * @param height int
     * @param bitMask byte[]
     * @param x int
     *
     * @return int
     */
    protected int paintTokenDots(final Graphics2D g, final LedConfig config, final int width, final int height, final byte[] bitMask, int x)
    {
        int dotWidth = config.getDotWidth();
        int dotHeight = config.getDotHeight();
        int hGap = config.getHgap();
        int vGap = config.getVgap();
        int topInset = 1;

        for (byte mask : bitMask)
        {
            for (int row = 0; row < 7; row++)
            {
                if (((mask & 0xFF) & (1 << row)) != 0)
                {
                    int y = ((row + topInset) * (dotHeight + vGap));

                    g.fillRect(x, y, dotWidth, dotHeight);
                }
            }

            x += hGap + dotWidth;

            if (x >= width)
            {
                break;
            }
        }

        return x;
    }
}
