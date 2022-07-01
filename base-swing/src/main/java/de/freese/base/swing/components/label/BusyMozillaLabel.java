package de.freese.base.swing.components.label;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * Label mit einem drehenden Kreis, ähnlich wie bei Mozilla.
 *
 * @author Thomas Freese
 */
public class BusyMozillaLabel extends JLabel
{
    /**
     *
     */
    private static final long serialVersionUID = -1861610997435401369L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        final JFrame frame = new JFrame("GlassPaneDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setSize(new Dimension(400, 400));

        BusyMozillaLabel mozillaLabel = new BusyMozillaLabel("Taeschd");

        // mozillaLabel.setTrail(7);
        frame.getContentPane().add(mozillaLabel, BorderLayout.NORTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Anzahl animierter Kreise.
     */
    private final int maxCircles = 8;
    /**
     *
     */
    private Timer animateTimer;
    /**
     * Farbe des letzten Kreises.
     */
    private Color baseColor = Color.WHITE;
    /**
     * Nummer des animierten Kreises.
     */
    private int circleIndex;
    /**
     *
     */
    private int circleRadius = 40;
    /**
     * Farbe des führenden Kreises.
     */
    private Color highlightColor = Color.BLACK;
    /**
     * Länge des Schwanzes.
     */
    private int trail = 8;

    /**
     * Creates a new {@link BusyMozillaLabel} object.
     */
    public BusyMozillaLabel()
    {
        this("");
    }

    /**
     * Creates a new {@link BusyMozillaLabel} object.
     *
     * @param text String
     */
    public BusyMozillaLabel(final String text)
    {
        this(text, Color.BLACK);
    }

    /**
     * Creates a new {@link BusyMozillaLabel} object.
     *
     * @param text String
     * @param highlightColor {@link Color}
     */
    public BusyMozillaLabel(final String text, final Color highlightColor)
    {
        super(text);

        initialize(highlightColor, getBackground(), getMaxCircles());
    }

    /**
     * Creates a new {@link BusyMozillaLabel} object.
     *
     * @param text String
     * @param highlightColor {@link Color}
     * @param baseColor {@link Color}
     */
    public BusyMozillaLabel(final String text, final Color highlightColor, final Color baseColor)
    {
        super(text);

        initialize(highlightColor, baseColor, getMaxCircles());
    }

    /**
     * Creates a new {@link BusyMozillaLabel} object.
     *
     * @param text String
     * @param highlightColor {@link Color}
     * @param baseColor {@link Color}
     * @param trail int
     */
    public BusyMozillaLabel(final String text, final Color highlightColor, final Color baseColor, final int trail)
    {
        super(text);

        setTrail(trail);

        initialize(highlightColor, baseColor, getTrail());
    }

    /**
     * Farbe des letzten Kreises.
     *
     * @return {@link Color}
     */
    public Color getBaseColor()
    {
        return this.baseColor;
    }

    /**
     * Gesamtdurchmesser des Kreises.
     *
     * @return int
     */
    public int getCircleRadius()
    {
        return this.circleRadius;
    }

    /**
     * @see javax.swing.JComponent#getHeight()
     */
    @Override
    public int getHeight()
    {
        return getCircleRadius();
    }

    /**
     * Farbe des führenden Kreises.
     *
     * @return {@link Color}
     */
    public Color getHighlightColor()
    {
        return this.highlightColor;
    }

    /**
     * @see javax.swing.JComponent#getMaximumSize()
     */
    @Override
    public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }

    /**
     * @see javax.swing.JComponent#getMinimumSize()
     */
    @Override
    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    /**
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();

        d.height = getHeight();
        d.width += (getCircleRadius() + 5);

        return d;
    }

    /**
     * Länge des Schwanzes.
     *
     * @return int
     */
    public int getTrail()
    {
        return this.trail;
    }

    /**
     * Farbe des letzten Kreises.
     *
     * @param baseColor {@link Color}
     */
    public void setBaseColor(final Color baseColor)
    {
        this.baseColor = baseColor;
    }

    /**
     * Gesamtdurchmesser des Kreises.
     *
     * @param circleRadius int
     */
    public void setCircleRadius(final int circleRadius)
    {
        this.circleRadius = circleRadius;
    }

    /**
     * Farbe des führenden Kreises.
     *
     * @param highlightColor {@link Color}
     */
    public void setHighlightColor(final Color highlightColor)
    {
        this.highlightColor = highlightColor;
    }

    /**
     * Länge des Schwanzes.
     *
     * @param trail int
     */
    public void setTrail(final int trail)
    {
        this.trail = trail;

        if (this.trail > 8)
        {
            this.trail = 8;
        }
    }

    /**
     * @see javax.swing.JComponent#setVisible(boolean)
     */
    @Override
    public void setVisible(final boolean visible)
    {
        super.setVisible(visible);

        if (visible && !this.animateTimer.isRunning())
        {
            this.animateTimer.start();
        }
        else if (!visible)
        {
            this.animateTimer.stop();
        }
    }

    /**
     * Initialisierung der Farben und Schwanzlänge.
     *
     * @param highlightColor {@link Color}
     * @param baseColor {@link Color}
     * @param trail int
     */
    protected void initialize(final Color highlightColor, final Color baseColor, final int trail)
    {
        setHighlightColor(highlightColor);
        setBaseColor(baseColor);
        setTrail(trail);
        setVerticalTextPosition(SwingConstants.CENTER);

        this.animateTimer = new Timer(150, event -> performAnimation());

        // // Trick: Startet den Timer
        // setVisible(true);
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(final Graphics g)
    {
        // Point point = getLocation();
        // Rectangle rectangle = getBounds();

        // Text 5 Pixel rechts vom Kreis malen
        g.translate(getCircleRadius() + 5, 0);
        // g.translate(getCircleRadius() + 5 + getX(), getY());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        super.paintComponent(g);

        g.setColor(getBaseColor());

        // Zentrum des Kreises setzen
        g.translate(-(getCircleRadius() / 2) - 5, (getCircleRadius() / 2));

        double theta = (Math.PI * 2.0) / getMaxCircles();

        // Radius und Durchmesser der kleinen Kreise
        int r = getCircleRadius() / 8;
        int d = 2 * r;

        for (int index = 0; index < getMaxCircles(); index++)
        {
            g.setColor(calcCircleColor(index));

            g2d.fillOval(r, r, d, d);

            g2d.rotate(theta);
        }

        if (!this.animateTimer.isRunning() && isVisible())
        {
            this.animateTimer.start();
        }
    }

    /**
     * Berechnet die Animation.
     */
    protected void performAnimation()
    {
        this.circleIndex++;

        if (this.circleIndex == getMaxCircles())
        {
            this.circleIndex = 0;
        }

        // System.out.println(_circleIndex);
        if (!isVisible())
        {
            this.animateTimer.stop();
        }
        else
        {
            repaint();
        }
    }

    /**
     * Berechnet für den Index eines Kreises die entsprechende Farbe.
     *
     * @param index int
     *
     * @return {@link Color}
     */
    private Color calcCircleColor(final int index)
    {
        if (index == this.circleIndex)
        {
            return getHighlightColor();
        }

        for (int t = 0; t < getTrail(); t++)
        {
            if (index == (((this.circleIndex - t) + getMaxCircles()) % getMaxCircles()))
            {
                // Faktor für interpolation
                float terp = 1 - (((float) (getTrail() - t)) / (float) getTrail());

                // Farbe interpolieren
                return interpolate(getHighlightColor(), getBaseColor(), terp);
            }
        }

        return getBaseColor();
    }

    /**
     * Max. Anzahl an Kreisen.
     *
     * @return int
     */
    private int getMaxCircles()
    {
        return this.maxCircles;
    }

    /**
     * Mischen von 2 Farben mit Interpolationsfaktor.
     *
     * @param a Color
     * @param b Color
     * @param factor float
     *
     * @return Color
     */
    private Color interpolate(final Color a, final Color b, final float factor)
    {
        float[] acomp = a.getRGBComponents(null);
        float[] bcomp = b.getRGBComponents(null);
        float[] ccomp = new float[4];

        // System.out.println("a comp ");
        // for(float f : acomp) {
        // System.out.println(f);
        // }
        // for(float f : bcomp) {
        // System.out.println(f);
        // }
        for (int i = 0; i < 4; i++)
        {
            ccomp[i] = acomp[i] + ((bcomp[i] - acomp[i]) * factor);
        }

        // for(float f : ccomp) {
        // System.out.println(f);
        // }
        return new Color(ccomp[0], ccomp[1], ccomp[2], ccomp[3]);
    }
}
