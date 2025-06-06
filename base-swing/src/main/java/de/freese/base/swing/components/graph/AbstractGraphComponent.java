// Created: 15.11.2020
package de.freese.base.swing.components.graph;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.Objects;

import javax.swing.Painter;
import javax.swing.SwingUtilities;

/**
 * @author Thomas Freese
 */
public abstract class AbstractGraphComponent extends Component {
    @Serial
    private static final long serialVersionUID = -7006824316195250962L;

    private final transient Painter<Component> painter;

    private transient BufferedImage bufferedImage;
    private transient Graphics2D bufferedImageGraphics2d;
    private boolean useBufferedImage;

    protected AbstractGraphComponent(final Painter<Component> painter) {
        super();

        this.painter = Objects.requireNonNull(painter, "painter required");

        init();
    }

    public boolean isUseBufferedImage() {
        return useBufferedImage;
    }

    /**
     * Nur verwenden, wenn Klasse von Component vererbt!
     */
    @Override
    public void paint(final Graphics g) {
        // super.paint(g);

        if (isUseBufferedImage() && getBufferedImage() != null) {
            getPainter().paint(getBufferedImageGraphics2d(), this, getWidth(), getHeight());

            g.drawImage(getBufferedImage(), 0, 0, this);
        }
        else {
            final Graphics2D g2d = (Graphics2D) g;

            getPainter().paint(g2d, this, getWidth(), getHeight());
        }
    }

    /**
     * Führt den Repaint immer im EDT aus.
     */
    public void paintGraph() {
        // ((AbstractGraphPainter) getPainter()).generateValue(getWidth());

        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
        }
        else {
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    public void useBufferedImage(final boolean useBufferedImage) {
        this.useBufferedImage = useBufferedImage;
    }

    protected BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    protected Graphics2D getBufferedImageGraphics2d() {
        return bufferedImageGraphics2d;
    }

    protected Painter<Component> getPainter() {
        return painter;
    }

    protected void init() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent event) {
                onMouseClicked(event);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent event) {
                onComponentResized(event);
            }
        });
    }

    protected void onComponentResized(final ComponentEvent event) {
        // bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        bufferedImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
        // bufferedImage = (BufferedImage) createImage(getWidth(), getHeight());

        bufferedImageGraphics2d = bufferedImage.createGraphics();
    }

    protected void onMouseClicked(final MouseEvent event) {
        // Empty
    }

    // /**
    // * Nur verwenden wenn Klasse von JComponent vererbt!
    // */
    // @Override
    // protected void printChildren(final Graphics g) {
    // // There are no Children.
    // // super.paintChildren(g);
    // }
    // @Override
    // protected void paintComponent(final Graphics g) {
    // // super.paintComponent(g);
    //
    // if (bufferedImage != null) {
    // getPainter().paint(bufferedImageGraphics2d, this, getWidth(), getHeight());
    //
    // g.drawImage(bufferedImage, 0, 0, this);
    // }
    // else {
    // Graphics2D g2d = (Graphics2D) g;
    //
    // getPainter().paint(g2d, this, getWidth(), getHeight());
    // }
    // }
}
