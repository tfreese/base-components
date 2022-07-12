package de.freese.base.swing.icon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Konfigurierbares Dreiecksicon.
 *
 * @author Thomas Freese
 */
public class ArrowIcon implements Icon
{
    /**
     *
     */
    private static final Map<RenderingHints.Key, Object> RENDERING_HINTS = new HashMap<>();

    static
    {
        RENDERING_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JLabel label = new JLabel(new ArrowIcon(30, 30, SwingConstants.NORTH, Color.MAGENTA));
        frame.getContentPane().add(BorderLayout.CENTER, label);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     *
     */
    private final int direction;
    /**
     *
     */
    private final Color foreground;
    /**
     *
     */
    private final int height;
    /**
     *
     */
    private final int width;

    /**
     * Creates a new {@link ArrowIcon} object.<br>
     * Defaults: Widht = 16, Height = 16, ForeGround = Black
     *
     * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
     */
    public ArrowIcon(final int direction)
    {
        this(16, 16, direction, Color.BLACK);
    }

    /**
     * Creates a new {@link ArrowIcon} object.
     *
     * @param width int
     * @param height int
     * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
     * @param foreground {@link Color}
     *
     * @throws IllegalArgumentException Falls Direction ungültig
     */
    public ArrowIcon(final int width, final int height, final int direction, final Color foreground)
    {
        super();

        this.width = width;
        this.height = height;
        this.direction = direction;
        this.foreground = foreground;

        if ((this.direction != SwingConstants.NORTH) && (this.direction != SwingConstants.SOUTH) && (this.direction != SwingConstants.EAST)
                && (this.direction != SwingConstants.WEST))
        {
            throw new IllegalArgumentException("Only SwingConstants.NORTH, SOUTH, EAST, WEST supported !");
        }

        // setImage(new ImageIcon(new byte[]
        // {
        // 0, 0
        // }).getImage());
    }

    /**
     * @see javax.swing.ImageIcon#getIconHeight()
     */
    @Override
    public int getIconHeight()
    {
        return this.height;
    }

    /**
     * @see javax.swing.ImageIcon#getIconWidth()
     */
    @Override
    public int getIconWidth()
    {
        return this.width;
    }

    /**
     * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
     */
    @Override
    public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y)
    {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.addRenderingHints(RENDERING_HINTS);
        g2d.setColor(this.foreground);
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int[] xPoints;
        int[] yPoints;

        if (this.direction == SwingConstants.NORTH)
        {
            xPoints = new int[]
                    {
                            x, x + centerX, x + (centerX * 2)
                    };
            yPoints = new int[]
                    {
                            y + (centerY * 2), y, y + (centerY * 2)
                    };
        }
        else if (this.direction == SwingConstants.SOUTH)
        {
            xPoints = new int[]
                    {
                            x, x + centerX, x + (centerX * 2)
                    };
            yPoints = new int[]
                    {
                            y, y + (centerY * 2), y
                    };
        }
        else if (this.direction == SwingConstants.WEST)
        {
            xPoints = new int[]
                    {
                            x + (centerX * 2), x, x + (centerX * 2)
                    };
            yPoints = new int[]
                    {
                            y, y + centerY, y + (centerY * 2)
                    };
        }
        else if (this.direction == SwingConstants.EAST)
        {
            xPoints = new int[]
                    {
                            x, x + (centerX * 2), x, x
                    };
            yPoints = new int[]
                    {
                            y, y + centerY, y + (centerY * 2)
                    };
        }
        else
        {
            throw new IllegalStateException();
        }

        g2d.fillPolygon(xPoints, yPoints, 3);
    }
}
