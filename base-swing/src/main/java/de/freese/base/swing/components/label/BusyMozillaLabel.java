package de.freese.base.swing.components.label;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.Serial;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Label mit einem drehenden Kreis, ähnlich wie bei Mozilla.
 *
 * @author Thomas Freese
 */
public class BusyMozillaLabel extends JLabel {
    @Serial
    private static final long serialVersionUID = -1861610997435401369L;

    /**
     * Anzahl animierter Kreise.
     */
    private final int maxCircles = 8;

    private Timer animateTimer;
    /**
     * Farbe des letzten Kreises.
     */
    private Color baseColor = Color.WHITE;
    /**
     * Nummer des animierten Kreises.
     */
    private int circleIndex;
    private int circleRadius = 40;
    /**
     * Farbe des führenden Kreises.
     */
    private Color highlightColor = Color.BLACK;
    /**
     * Länge des Schwanzes.
     */
    private int trail = 8;

    public BusyMozillaLabel() {
        this("");
    }

    public BusyMozillaLabel(final String text) {
        this(text, Color.BLACK);
    }

    public BusyMozillaLabel(final String text, final Color highlightColor) {
        super(text);

        initialize(highlightColor, getBackground(), getMaxCircles());
    }

    public BusyMozillaLabel(final String text, final Color highlightColor, final Color baseColor) {
        super(text);

        initialize(highlightColor, baseColor, getMaxCircles());
    }

    public BusyMozillaLabel(final String text, final Color highlightColor, final Color baseColor, final int trail) {
        super(text);

        setTrail(trail);

        initialize(highlightColor, baseColor, getTrail());
    }

    /**
     * Farbe des letzten Kreises.
     */
    public Color getBaseColor() {
        return this.baseColor;
    }

    public int getCircleRadius() {
        return this.circleRadius;
    }

    @Override
    public int getHeight() {
        return getCircleRadius();
    }

    /**
     * Farbe des führenden Kreises.
     */
    public Color getHighlightColor() {
        return this.highlightColor;
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        final Dimension d = super.getPreferredSize();

        d.height = getHeight();
        d.width += getCircleRadius() + 5;

        return d;
    }

    /**
     * Länge des Schwanzes.
     */
    public int getTrail() {
        return this.trail;
    }

    /**
     * Farbe des letzten Kreises.
     */
    public void setBaseColor(final Color baseColor) {
        this.baseColor = baseColor;
    }

    /**
     * Gesamtdurchmesser des Kreises.
     */
    public void setCircleRadius(final int circleRadius) {
        this.circleRadius = circleRadius;
    }

    /**
     * Farbe des führenden Kreises.
     */
    public void setHighlightColor(final Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    /**
     * Länge des Schwanzes.
     */
    public void setTrail(final int trail) {
        this.trail = trail;

        if (this.trail > 8) {
            this.trail = 8;
        }
    }

    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);

        if (visible && !this.animateTimer.isRunning()) {
            this.animateTimer.start();
        }
        else if (!visible) {
            this.animateTimer.stop();
        }
    }

    protected void initialize(final Color highlightColor, final Color baseColor, final int trail) {
        setHighlightColor(highlightColor);
        setBaseColor(baseColor);
        setTrail(trail);
        setVerticalTextPosition(SwingConstants.CENTER);

        this.animateTimer = new Timer(150, event -> performAnimation());

        // // Trick: Startet den Timer
        // setVisible(true);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        // Point point = getLocation();
        // Rectangle rectangle = getBounds();

        // Text 5 Pixel rechts vom Kreis malen
        g.translate(getCircleRadius() + 5, 0);
        // g.translate(getCircleRadius() + 5 + getX(), getY());

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        super.paintComponent(g);

        g.setColor(getBaseColor());

        // Zentrum des Kreises setzen
        g.translate(-(getCircleRadius() / 2) - 5, getCircleRadius() / 2);

        final double theta = (Math.PI * 2.0) / getMaxCircles();

        // Radius und Durchmesser der kleinen Kreise
        final int r = getCircleRadius() / 8;
        final int d = 2 * r;

        for (int index = 0; index < getMaxCircles(); index++) {
            g.setColor(calcCircleColor(index));

            g2d.fillOval(r, r, d, d);

            g2d.rotate(theta);
        }

        if (!this.animateTimer.isRunning() && isVisible()) {
            this.animateTimer.start();
        }

        // The Toolkit.getDefaultToolkit().sync() synchronises the painting on systems that buffer graphics events.
        // Without this line, the animation might not be smooth on Linux.
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Berechnet die Animation.
     */
    protected void performAnimation() {
        this.circleIndex++;

        if (this.circleIndex == getMaxCircles()) {
            this.circleIndex = 0;
        }

        // System.out.println(_circleIndex);
        if (!isVisible()) {
            this.animateTimer.stop();
        }
        else {
            repaint();
        }
    }

    /**
     * Berechnet für den Index eines Kreises die entsprechende Farbe.
     */
    private Color calcCircleColor(final int index) {
        if (index == this.circleIndex) {
            return getHighlightColor();
        }

        for (int t = 0; t < getTrail(); t++) {
            if (index == (((this.circleIndex - t) + getMaxCircles()) % getMaxCircles())) {
                // Faktor für interpolation
                final float terp = 1F - (((float) (getTrail() - t)) / (float) getTrail());

                // Farbe interpolieren
                return interpolate(getHighlightColor(), getBaseColor(), terp);
            }
        }

        return getBaseColor();
    }

    /**
     * Max. Anzahl an Kreisen.
     */
    private int getMaxCircles() {
        return this.maxCircles;
    }

    /**
     * Mischen von 2 Farben mit Interpolationsfaktor.
     */
    private Color interpolate(final Color a, final Color b, final float factor) {
        final float[] acomp = a.getRGBComponents(null);
        final float[] bcomp = b.getRGBComponents(null);
        final float[] ccomp = new float[4];

        // System.out.println("a comp ");
        // for(float f : acomp) {
        // System.out.println(f);
        // }
        // for(float f : bcomp) {
        // System.out.println(f);
        // }
        for (int i = 0; i < 4; i++) {
            ccomp[i] = acomp[i] + ((bcomp[i] - acomp[i]) * factor);
        }

        // for(float f : ccomp) {
        // System.out.println(f);
        // }
        return new Color(ccomp[0], ccomp[1], ccomp[2], ccomp[3]);
    }
}
