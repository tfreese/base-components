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
public class LedPanel extends Component implements LedConfig {
    @Serial
    private static final long serialVersionUID = -2419621712513872997L;

    private final transient LedMatrix ledMatrix;

    private final transient List<Token<?>> tokens = new LinkedList<>();

    private Color colorBackground;
    private Color colorBackgroundDot;
    private int dotHeight;
    private int dotWidth;
    private int hgap;
    private int tokenGap;
    private int vgap;

    public LedPanel() {
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

    public void addToken(final Token<?> token) {
        this.tokens.add(token);

        repaint();
    }

    @Override
    public Color getColorBackground() {
        return this.colorBackground;
    }

    @Override
    public Color getColorBackgroundDot() {
        return this.colorBackgroundDot;
    }

    @Override
    public int getDotHeight() {
        return this.dotHeight;
    }

    @Override
    public int getDotWidth() {
        return this.dotWidth;
    }

    @Override
    public int getHgap() {
        return this.hgap;
    }

    @Override
    public int getTokenGap() {
        return this.tokenGap;
    }

    @Override
    public List<Token<?>> getTokens() {
        return this.tokens;
    }

    @Override
    public int getVgap() {
        return this.vgap;
    }

    /**
     * Nur verwenden, wenn Klasse von Component vererbt!
     */
    @Override
    public void paint(final Graphics g) {
        // super.paint(g);

        final Graphics2D g2d = (Graphics2D) g;

        this.ledMatrix.paint(g2d, this, getWidth(), getHeight());
    }

    public void removeFirstToken() {
        if (this.tokens.isEmpty()) {
            return;
        }

        this.tokens.removeFirst();
    }

    public void setColorBackground(final Color colorBackground) {
        this.colorBackground = colorBackground;

        repaint();
    }

    // /**
    // * Nur verwenden wenn Klasse von JComponent vererbt !!!
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

    public void setColorBackgroundDot(final Color colorBackgroundDot) {
        this.colorBackgroundDot = colorBackgroundDot;

        repaint();
    }

    public void setDotHeight(final int dotHeight) {
        this.dotHeight = dotHeight;

        repaint();
    }

    public void setDotWidth(final int dotWidth) {
        this.dotWidth = dotWidth;

        repaint();
    }

    public void setHgap(final int hgap) {
        this.hgap = hgap;

        repaint();
    }

    public void setTokenGap(final int tokenGap) {
        this.tokenGap = tokenGap;
    }

    public void setVgap(final int vgap) {
        this.vgap = vgap;

        repaint();
    }
}
