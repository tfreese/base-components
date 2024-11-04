package de.freese.base.swing.components.label;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * Label mit einem drehenden Kreis, ähnlich wie bei Mozilla.
 *
 * @author Thomas Freese
 */
public class BusyMozillaLabel extends JLabel {
    @Serial
    private static final long serialVersionUID = -1861610997435401369L;

    public static void main(final String[] args) {
        final JFrame frame = new JFrame(BusyMozillaLabel.class.getSimpleName());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(new Dimension(400, 400));

        final BusyMozillaLabel busyLabel = new BusyMozillaLabel("BusyLabel");
        busyLabel.setTrailCount(7);
        busyLabel.setCircleRadius(40);

        final JToggleButton jToggleButton = new JToggleButton("Start/Stop");
        jToggleButton.addActionListener(event -> busyLabel.setBusy(jToggleButton.isSelected()));

        frame.getContentPane().add(jToggleButton, BorderLayout.NORTH);
        frame.getContentPane().add(busyLabel, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                // Otherwise the Timer is still running and the JRE won't exit.
                busyLabel.setBusy(false);
            }
        });
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private final Timer animateTimer;
    /**
     * Anzahl der Kreise.
     */
    private int circleCount = 8;
    /**
     * Nummer des animierten Kreises.
     */
    private int circleIndex;
    /**
     * Gesamtdurchmesser des Kreises.
     */
    private int circleRadius = 20;
    /**
     * Farbe des ersten Kreises.
     */
    private Color colorFirst = Color.BLACK;
    /**
     * Farbe des letzten Kreises.
     */
    private Color colorLast = Color.WHITE;
    /**
     * Länge des Schwanzes.<br>
     * Max.: circleCount
     */
    private int trailCount = circleCount;

    public BusyMozillaLabel() {
        this(null);
    }

    public BusyMozillaLabel(final String text) {
        super(text);

        super.setVerticalTextPosition(SwingConstants.CENTER);

        colorLast = super.getBackground();

        animateTimer = new Timer(150, event -> performAnimation());
    }

    /**
     * Gesamtdurchmesser des Kreises.
     */
    public int getCircleRadius() {
        return circleRadius;
    }

    /**
     * Farbe des ersten Kreises.
     */
    public Color getColorFirst() {
        return colorFirst;
    }

    /**
     * Farbe des letzten Kreises.
     */
    public Color getColorLast() {
        return colorLast;
    }

    @Override
    public int getHeight() {
        return getCircleRadius();
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
     * Länge des Schwanzes.<br>
     * Max.: circleCount
     */
    public int getTrailCount() {
        return trailCount;
    }

    public boolean isRunning() {
        return animateTimer.isRunning();
    }

    public void setBusy(final boolean busy) {
        if (busy && !isRunning()) {
            animateTimer.start();
        }
        else if (!busy) {
            animateTimer.stop();
            repaint();
        }
    }

    /**
     * Anzahl der Kreise.
     */
    public void setCircleCount(final int circleCount) {
        this.circleCount = circleCount;

        if (trailCount > circleCount) {
            trailCount = circleCount;
        }
    }

    /**
     * Gesamtdurchmesser des Kreises.
     */
    public void setCircleRadius(final int circleRadius) {
        this.circleRadius = circleRadius;
    }

    /**
     * Farbe des ersten Kreises.
     */
    public void setColorFirst(final Color colorFirst) {
        this.colorFirst = colorFirst;
    }

    /**
     * Farbe des letzten Kreises.
     */
    public void setColorLast(final Color colorLast) {
        this.colorLast = colorLast;
    }

    /**
     * Länge des Schwanzes.<br>
     * Max.: circleCount
     */
    public void setTrailCount(final int trailCount) {
        if (trailCount > getCircleCount()) {
            this.trailCount = getCircleCount();
        }
        else {
            this.trailCount = trailCount;
        }
    }

    /**
     * Berechnet für den Index eines Kreises die entsprechende Farbe.
     */
    protected Color calcCircleColor(final int index) {
        if (index == circleIndex) {
            return getColorFirst();
        }

        for (int t = 0; t < getTrailCount(); t++) {
            if (index == (((circleIndex - t) + getCircleCount()) % getCircleCount())) {
                // Faktor für interpolation.
                final double factor = 1D - (((double) (getTrailCount() - t)) / (double) getTrailCount());

                // Farbe interpolieren.
                return interpolate(getColorFirst(), getColorLast(), (float) factor);
            }
        }

        return getColorLast();
    }

    /**
     * Anzahl der Kreise.
     */
    protected int getCircleCount() {
        return circleCount;
    }

    /**
     * Mischen von 2 Farben mit Interpolationsfaktor.
     */
    protected Color interpolate(final Color a, final Color b, final float factor) {
        final float[] aComp = a.getRGBComponents(null);
        final float[] bComp = b.getRGBComponents(null);
        final float[] cComp = new float[4];

        for (int i = 0; i < 4; i++) {
            cComp[i] = aComp[i] + ((bComp[i] - aComp[i]) * factor);
        }

        return new Color(cComp[0], cComp[1], cComp[2], cComp[3]);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (!isRunning()) {
            super.paintComponent(g);

            return;
        }

        // Text 5 Pixel rechts vom Kreis malen.
        g.translate(getCircleRadius() + 5, 0);
        // g.translate(getCircleRadius() + 5 + getX(), getY());

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        super.paintComponent(g);

        g.setColor(getColorLast());

        // Zentrum des Kreises setzen.
        g.translate(-(getCircleRadius() / 2) - 5, getCircleRadius() / 2);

        final double theta = Math.TAU / getCircleCount();

        // Radius und Durchmesser der kleinen Kreise.
        final int r = getCircleRadius() / 8;
        final int d = 2 * r;

        for (int index = 0; index < getCircleCount(); index++) {
            g.setColor(calcCircleColor(index));

            g2d.fillOval(r, r, d, d);

            g2d.rotate(theta);
        }
    }

    protected void performAnimation() {
        if (!isVisible()) {
            animateTimer.stop();

            return;
        }

        circleIndex++;

        if (circleIndex == getCircleCount()) {
            circleIndex = 0;
        }

        repaint();

        // The Toolkit.getDefaultToolkit().sync() synchronises the painting on systems that buffer graphics events.
        // Without this line, the animation might not be smooth on Linux.
        Toolkit.getDefaultToolkit().sync();
    }
}
