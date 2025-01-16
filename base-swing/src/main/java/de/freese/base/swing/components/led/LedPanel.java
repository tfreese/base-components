// Created: 28.12.2020
package de.freese.base.swing.components.led;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serial;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import de.freese.base.swing.components.led.token.Token;

/**
 * @author Thomas Freese
 */
public class LedPanel extends Component implements LedConfig {
    @Serial
    private static final long serialVersionUID = -2419621712513872997L;

    private final transient LedMatrix ledMatrix;

    private final transient List<Token> tokens = new LinkedList<>();

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

    /**
     * {@link #repaint()} is called.
     */
    public void addToken(final Token token) {
        tokens.add(token);

        repaint();
    }

    @Override
    public Color getColorBackground() {
        return colorBackground;
    }

    @Override
    public Color getColorBackgroundDot() {
        return colorBackgroundDot;
    }

    @Override
    public int getDotHeight() {
        return dotHeight;
    }

    @Override
    public int getDotWidth() {
        return dotWidth;
    }

    @Override
    public int getHgap() {
        return hgap;
    }

    @Override
    public int getTokenGap() {
        return tokenGap;
    }

    @Override
    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public int getVgap() {
        return vgap;
    }

    /**
     * Nur verwenden, wenn Klasse von Component vererbt!
     */
    @Override
    public void paint(final Graphics g) {
        // super.paint(g);

        final Graphics2D g2d = (Graphics2D) g;

        ledMatrix.paint(g2d, this, getWidth(), getHeight());
    }

    public Token removeFirstToken() {
        if (tokens.isEmpty()) {
            return null;
        }

        final Token token = tokens.removeFirst();

        doRepaint();

        return token;
    }

    public void rotateToken() {
        if (tokens.isEmpty()) {
            return;
        }

        final Token token = tokens.removeFirst();
        tokens.add(token);

        doRepaint();
    }

    public void setColorBackground(final Color colorBackground) {
        this.colorBackground = colorBackground;

        doRepaint();
    }

    // /**
    // * Nur verwenden wenn Klasse von JComponent vererbt !!!
    // */
    // @Override
    // protected void paintComponent(final Graphics g) {
    // // super.paintComponent(g);
    //
    // Graphics2D g2d = (Graphics2D) g;
    //
    // this.ledMatrix.paint(g2d, this, getWidth(), getHeight());
    // }

    public void setColorBackgroundDot(final Color colorBackgroundDot) {
        this.colorBackgroundDot = colorBackgroundDot;

        doRepaint();
    }

    public void setDotHeight(final int dotHeight) {
        this.dotHeight = dotHeight;

        doRepaint();
    }

    public void setDotWidth(final int dotWidth) {
        this.dotWidth = dotWidth;

        doRepaint();
    }

    public void setHgap(final int hgap) {
        this.hgap = hgap;

        doRepaint();
    }

    public void setTokenGap(final int tokenGap) {
        this.tokenGap = tokenGap;

        doRepaint();
    }

    public void setVgap(final int vgap) {
        this.vgap = vgap;

        doRepaint();
    }

    protected void doRepaint() {
        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
        }
        else {
            SwingUtilities.invokeLater(this::repaint);
        }
    }
}
