// Created: 28.12.2020
package de.freese.base.swing.components.led;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serial;
import java.util.LinkedList;
import java.util.List;

import de.freese.base.swing.components.led.token.Token;

/**
 * @author Thomas Freese
 */
public class LedPanel extends Component implements LedConfig
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2419621712513872997L;
    /**
     *
     */
    private final LedMatrix ledMatrix;
    /**
     *
     */
    private final List<Token<?>> tokens = new LinkedList<>();
    /**
     *
     */
    private Color colorBackground;
    /**
     *
     */
    private Color colorBackgroundDot;
    /**
     *
     */
    private int dotHeight;
    /**
     *
     */
    private int dotWidth;
    /**
     *
     */
    private int hgap;
    /**
     *
     */
    private int tokenGap;
    /**
     *
     */
    private int vgap;

    /**
     * Erstellt ein neues {@link LedPanel} Object.
     */
    public LedPanel()
    {
        super();

        this.ledMatrix = new LedMatrix();

        this.colorBackground = new Color(51, 51, 51);
        this.colorBackgroundDot = new Color(17, 17, 17);

        this.dotHeight = 2;
        this.dotWidth = 2;

        this.hgap = 1;
        this.vgap = 1;

        this.tokenGap = 2;
    }

    /**
     * @param token {@link Token}
     */
    public void addToken(final Token<?> token)
    {
        this.tokens.add(token);

        repaint();
    }

    /**
     * @see de.freese.base.swing.components.led.LedConfig#getColorBackground()
     */
    @Override
    public Color getColorBackground()
    {
        return this.colorBackground;
    }

    /**
     * @see de.freese.base.swing.components.led.LedConfig#getColorBackgroundDot()
     */
    @Override
    public Color getColorBackgroundDot()
    {
        return this.colorBackgroundDot;
    }

    /**
     * @see de.freese.base.swing.components.led.LedConfig#getDotHeight()
     */
    @Override
    public int getDotHeight()
    {
        return this.dotHeight;
    }

    /**
     * @see de.freese.base.swing.components.led.LedConfig#getDotWidth()
     */
    @Override
    public int getDotWidth()
    {
        return this.dotWidth;
    }

    /**
     * @see de.freese.base.swing.components.led.LedConfig#getHgap()
     */
    @Override
    public int getHgap()
    {
        return this.hgap;
    }

    /**
     * @see de.freese.base.swing.components.led.LedConfig#getTokenGap()
     */
    @Override
    public int getTokenGap()
    {
        return this.tokenGap;
    }

    /**
     * @see de.freese.base.swing.components.led.element.Element#getTokens()
     */
    @Override
    public List<Token<?>> getTokens()
    {
        return this.tokens;
    }

    /**
     * @see de.freese.base.swing.components.led.LedConfig#getVgap()
     */
    @Override
    public int getVgap()
    {
        return this.vgap;
    }

    /**
     * Nur verwenden, wenn Klasse von Component vererbt !!!
     *
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        // super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

        this.ledMatrix.paint(g2d, this, getWidth(), getHeight());
    }

    /**
     *
     */
    public void removeFirstToken()
    {
        if (this.tokens.isEmpty())
        {
            return;
        }

        this.tokens.remove(0);
    }

    /**
     * @param colorBackground {@link Color}
     */
    public void setColorBackground(final Color colorBackground)
    {
        this.colorBackground = colorBackground;

        repaint();
    }

    // /**
    // * Nur verwenden wenn Klasse von JComponent vererbt !!!
    // *
    // * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
    // */
    // @Override
    // protected void paintComponent(final Graphics g)
    // {
    // // super.paintComponent(g);
    //
    // Graphics2D g2d = (Graphics2D) g;
    //
    // this.ledMatrix.paint(g2d, this, getWidth(), getHeight());
    // }

    /**
     * @param colorBackgroundDot {@link Color}
     */
    public void setColorBackgroundDot(final Color colorBackgroundDot)
    {
        this.colorBackgroundDot = colorBackgroundDot;

        repaint();
    }

    /**
     * @param dotHeight int
     */
    public void setDotHeight(final int dotHeight)
    {
        this.dotHeight = dotHeight;

        repaint();
    }

    /**
     * @param dotWidth int
     */
    public void setDotWidth(final int dotWidth)
    {
        this.dotWidth = dotWidth;

        repaint();
    }

    /**
     * @param hgap int
     */
    public void setHgap(final int hgap)
    {
        this.hgap = hgap;

        repaint();
    }

    /**
     * @param tokenGap int
     */
    public void setTokenGap(final int tokenGap)
    {
        this.tokenGap = tokenGap;
    }

    /**
     * @param vgap int
     */
    public void setVgap(final int vgap)
    {
        this.vgap = vgap;

        repaint();
    }
}
