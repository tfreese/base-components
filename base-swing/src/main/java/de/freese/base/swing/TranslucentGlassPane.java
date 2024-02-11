package de.freese.base.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * @author Thomas Freese
 */
public class TranslucentGlassPane extends JComponent implements MouseListener {
    @Serial
    private static final long serialVersionUID = -8037679488481229262L;

    private final double alphaEnd = 0.6D;
    private final Timer animateTimer;
    private final transient List<Component> dispatchComponents;
    private double alpha = 1.0D;
    private double alphaIncrement = 0.02D;
    private double alphaStart;
    /**
     * If the old alpha value was 1.0, I keep track of the opaque setting because a translucent component is not opaque, but I want to be able to restore
     * opacity to its default setting if the alpha is 1.0. Honestly, I don't know if this is necessary or not, but it sounded good on paper :)
     */
    private boolean oldOpaque;
    private int showDelayMillies = 100;
    private int timerIncrementMillies = 10;

    public TranslucentGlassPane() {
        this(Collections.emptyList());
    }

    public TranslucentGlassPane(final List<Component> dispatchComponents) {
        super();

        this.dispatchComponents = dispatchComponents;

        addMouseListener(this);
        setOpaque(false);
        setAlpha(this.alphaStart);
        setBackground(Color.WHITE);

        this.animateTimer = new Timer(getTimerIncrementMillies(), event -> setAlpha(getAlpha() + TranslucentGlassPane.this.alphaIncrement));
    }

    /**
     * @return This will be a value between 0 and 1, inclusive.
     */
    public double getAlpha() {
        if (this.alpha > 1D) {
            this.alpha = 1D;
        }

        return this.alpha;
    }

    /**
     * Startverzögerung für die Animation in Millisekunden.
     */
    public int getShowDelayMillies() {
        return this.showDelayMillies;
    }

    /**
     * Zeitabstand zwischen den Animationen in Millisekunden.
     */
    public int getTimerIncrementMillies() {
        return this.timerIncrementMillies;
    }

    @Override
    public void mouseClicked(final MouseEvent event) {
        redispatchMouseEvent(event, false);

        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void mouseEntered(final MouseEvent event) {
        redispatchMouseEvent(event, false);
    }

    @Override
    public void mouseExited(final MouseEvent event) {
        redispatchMouseEvent(event, false);
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        redispatchMouseEvent(event, false);
    }

    @Override
    public void mouseReleased(final MouseEvent event) {
        redispatchMouseEvent(event, true);
    }

    /**
     * Set the alpha transparency level for this component. This automatically causes a repaint of the component.
     *
     * @param alpha must be a value between 0 and 1 inclusive.
     */
    public void setAlpha(final double alpha) {
        if (Double.compare(this.alpha, alpha) != 0) {
            final double oldAlpha = this.alpha;
            this.alpha = alpha;

            if ((alpha > 0D) && (alpha < 1D)) {
                if (Double.compare(oldAlpha, 1D) == 0) {
                    // it used to be 1, but now is not. Save the oldOpaque
                    this.oldOpaque = isOpaque();
                    setOpaque(false);
                }
            }
            else if (Double.compare(alpha, 1D) == 0) {
                // restore the oldOpaque if it was true (since opaque is false now)
                if (this.oldOpaque) {
                    setOpaque(true);
                }
            }

            // firePropertyChange("alpha", oldAlpha, alpha);
            repaint();
        }

        this.alphaIncrement = (this.alphaEnd - this.alphaStart) / (getShowDelayMillies() / getTimerIncrementMillies());
    }

    /**
     * Startverzögerung für die Animation in Millisekunden.
     */
    public void setShowDelayMillies(final int showDelayMillies) {
        this.showDelayMillies = showDelayMillies;
    }

    /**
     * Zeitabstand zwischen den Animationen in Millisekunden.
     */
    public void setTimerIncrementMillies(final int timerIncrementMillies) {
        this.timerIncrementMillies = timerIncrementMillies;
    }

    @Override
    public void setVisible(final boolean flag) {
        setAlpha(this.alphaStart);

        super.setVisible(flag);

        // Über setVisible den Timer der Children beenden, falls implementiert
        for (Component child : getComponents()) {
            child.setVisible(flag);
        }
    }

    // protected void paintChildren(Graphics g)
    // {
    // // Die Children werden in der transparenz der Glass-pane gezeichnet,
    // deswegen
    // // wird paintChildren(Graphics) am Ende der paintComponent(Graphics)
    // aufgerufen.
    // super.paintChildren(g);
    // }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        // Hintergrund
        g.setColor(new Color(255, 255, 255, (int) (getAlpha() * 255)));

        final Rectangle clip = g.getClipBounds();
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        // Vordergrund
        // final Graphics2D g2d = (Graphics2D) g;
        // g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
        // getAlpha()));
        if (!this.animateTimer.isRunning() && (getAlpha() < this.alphaEnd)) {
            this.animateTimer.start();
        }

        if (this.animateTimer.isRunning() && (getAlpha() >= this.alphaEnd)) {
            this.animateTimer.stop();
        }

        // Composite-Wert auf default für Children
        // g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
        // 1));
        // super.paintChildren(g);
    }

    private JMenuBar getJMenuBar() {
        for (Component component : this.dispatchComponents) {
            if (component instanceof JMenuBar b) {
                return b;
            }
        }

        return null;
    }

    private void redispatchMouseEvent(final MouseEvent event, final boolean repaint) {
        if (this.dispatchComponents.isEmpty()) {
            return;
        }

        final Point glassPanePoint = event.getPoint();
        final Point containerPoint = SwingUtilities.convertPoint(this, glassPanePoint, getParent());

        //        if (containerPoint.y < 0)
        //        {
        //            // we're not in the content pane
        //            final JMenuBar menuBar = getJMenuBar();
        //
        //            if ((menuBar != null) && ((containerPoint.y + menuBar.getHeight()) >= 0))
        //            {
        //                // The mouse event is over the menu bar.
        //                // Could handle specially.
        //            }
        //            else
        //            {
        //                // The mouse event is over non-system window
        //                // decorations, such as the ones provided by
        //                // the Java look and feel.
        //                // Could handle specially.
        //            }
        //        }
        if (containerPoint.y >= 0) {
            // The mouse event is probably over the content pane.
            // Find out exactly which component it's over.
            final Component component = SwingUtilities.getDeepestComponentAt(getParent(), containerPoint.x, containerPoint.y);

            if ((component != null) && this.dispatchComponents.contains(component)) {
                // Forward events over the component.
                final Point componentPoint = SwingUtilities.convertPoint(this, glassPanePoint, component);
                component.dispatchEvent(new MouseEvent(component, event.getID(), event.getWhen(), event.getModifiersEx(), componentPoint.x, componentPoint.y, event.getClickCount(),
                        event.isPopupTrigger()));
            }
        }

        // Update the glass pane if requested.
        if (repaint) {
            repaint();
        }
    }
}
