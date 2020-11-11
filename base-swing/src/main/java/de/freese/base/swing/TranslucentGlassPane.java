package de.freese.base.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Transparente Glasspane, zum Teil von SwingX Komponenten geklaut.
 *
 * @author Thomas Freese
 */
public class TranslucentGlassPane extends JComponent implements MouseListener
{
    /**
     *
     */
    private static final long serialVersionUID = -8037679488481229262L;

    /**
     *
     */
    private float alpha = 1.0F;

    /**
     *
     */
    private float alphaEnd = 0.6F;

    /**
     *
     */
    private float alphaIncrement = 0.02F;

    /**
     *
     */
    private float alphaStart = 0.0F;

    /**
     *
     */
    private Timer animateTimer;

    /**
     *
     */
    private List<Component> dispatchList;

    /**
     * If the old alpha value was 1.0, I keep track of the opaque setting because a translucent component is not opaque, but I want to be able to restore
     * opacity to its default setting if the alpha is 1.0. Honestly, I don't know if this is necessary or not, but it sounded good on paper :)
     * <p>
     * TODO: Check whether this variable is necessary or not
     * </p>
     */
    private boolean oldOpaque;

    /**
     *
     */
    private int showDelayMillies = 100;

    /**
     *
     */
    private int timerIncrementMillies = 10;

    /**
     * Creates a new {@link TranslucentGlassPane} object.
     */
    @SuppressWarnings("unchecked")
    public TranslucentGlassPane()
    {
        this(Collections.EMPTY_LIST);
    }

    /**
     * Creates a new {@link TranslucentGlassPane} object.
     *
     * @param dispatchList {@link List}, Liste von Komponenten, an denen MouseEvents weitergeleitet werden sollen
     */
    public TranslucentGlassPane(final List<Component> dispatchList)
    {
        super();

        this.dispatchList = dispatchList;
        addMouseListener(this);
        setOpaque(false);
        setAlpha(this.alphaStart);
        setBackground(Color.WHITE);

        this.animateTimer = new Timer(getTimerIncrementMillies(), event -> {
            setAlpha(getAlpha() + TranslucentGlassPane.this.alphaIncrement);
        });
    }

    /**
     * The alpha translucency level for this component
     *
     * @return This will be a value between 0 and 1, inclusive.
     */
    public float getAlpha()
    {
        if (this.alpha > 1)
        {
            this.alpha = 1;
        }

        return this.alpha;
    }

    /**
     * Liefert die JMmenuBar, wenn sie in der DispatchList enthalten ist.
     *
     * @return {@link JMenuBar}
     */
    private JMenuBar getJMenuBar()
    {
        for (Component element : this.dispatchList)
        {
            if (element instanceof JMenuBar)
            {
                return (JMenuBar) element;
            }
        }

        return null;
    }

    /**
     * Startverzögerung für die Animation in Millisekunden.
     *
     * @return int
     */
    public int getShowDelayMillies()
    {
        return this.showDelayMillies;
    }

    /**
     * Zeitabstand zwischen den Animationen in Millisekunden.
     *
     * @return int
     */
    public int getTimerIncrementMillies()
    {
        return this.timerIncrementMillies;
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(final MouseEvent e)
    {
        redispatchMouseEvent(e, false);

        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(final MouseEvent e)
    {
        redispatchMouseEvent(e, false);
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(final MouseEvent e)
    {
        redispatchMouseEvent(e, false);
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(final MouseEvent e)
    {
        redispatchMouseEvent(e, false);
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(final MouseEvent e)
    {
        redispatchMouseEvent(e, true);
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(final Graphics g)
    {
        super.paintComponent(g);

        // Hintergrund
        g.setColor(new Color(255, 255, 255, (int) (getAlpha() * 255)));

        Rectangle clip = g.getClipBounds();
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        // Vordergrund
        // Graphics2D g2d = (Graphics2D) g;
        // g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
        // getAlpha()));
        if (!this.animateTimer.isRunning() && (getAlpha() < this.alphaEnd))
        {
            this.animateTimer.start();
        }

        if (this.animateTimer.isRunning() && (getAlpha() >= this.alphaEnd))
        {
            this.animateTimer.stop();
        }

        // Compositewert auf default fuer Children
        // g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
        // 1));
        // super.paintChildren(g);
    }

    /**
     * Weiterleiten von MouseEvents an Komponenten der DispatchList.
     *
     * @param me {@link MouseEvent}
     * @param repaint boolean
     */
    private void redispatchMouseEvent(final MouseEvent me, final boolean repaint)
    {
        if (this.dispatchList.isEmpty())
        {
            return;
        }

        Point glassPanePoint = me.getPoint();
        Point containerPoint = SwingUtilities.convertPoint(this, glassPanePoint, getParent());

        if (containerPoint.y < 0)
        {
            // we're not in the content pane
            JMenuBar menuBar = getJMenuBar();

            if ((menuBar != null) && ((containerPoint.y + menuBar.getHeight()) >= 0))
            {
                // The mouse event is over the menu bar.
                // Could handle specially.
            }
            else
            {
                // The mouse event is over non-system window
                // decorations, such as the ones provided by
                // the Java look and feel.
                // Could handle specially.
            }
        }
        else
        {
            // The mouse event is probably over the content pane.
            // Find out exactly which component it's over.
            Component component = SwingUtilities.getDeepestComponentAt(getParent(), containerPoint.x, containerPoint.y);

            if ((component != null) && this.dispatchList.contains(component))
            {
                // Forward events over the component.
                Point componentPoint = SwingUtilities.convertPoint(this, glassPanePoint, component);
                component.dispatchEvent(new MouseEvent(component, me.getID(), me.getWhen(), me.getModifiersEx(), componentPoint.x, componentPoint.y,
                        me.getClickCount(), me.isPopupTrigger()));
            }
        }

        // Update the glass pane if requested.
        if (repaint)
        {
            repaint();
        }
    }

    /**
     * Set the alpha transparency level for this component. This automatically causes a repaint of the component.
     *
     * @param alpha must be a value between 0 and 1 inclusive.
     */
    public void setAlpha(final float alpha)
    {
        if (this.alpha != alpha)
        {
            float oldAlpha = this.alpha;
            this.alpha = alpha;

            if ((alpha > 0F) && (alpha < 1F))
            {
                if (oldAlpha == 1)
                {
                    // it used to be 1, but now is not. Save the oldOpaque
                    this.oldOpaque = isOpaque();
                    setOpaque(false);
                }
            }
            else if (alpha == 1F)
            {
                // restore the oldOpaque if it was true (since opaque is false
                // now)
                if (this.oldOpaque)
                {
                    setOpaque(true);
                }
            }

            // firePropertyChange("alpha", oldAlpha, alpha);
            repaint();
        }

        this.alphaIncrement = (this.alphaEnd - this.alphaStart) / (getShowDelayMillies() / getTimerIncrementMillies());
    }

    // /**
    // * @see javax.swing.JComponent#paintChildren(java.awt.Graphics)
    // */
    // protected void paintChildren(Graphics g)
    // {
    // // Die Children werden in der tranzparenz der Glasspane gezeichnet,
    // deswegen
    // // wird paintChildren(Graphics) am Ende der paintComponent(Graphics)
    // aufgerufen.
    // super.paintChildren(g);
    // }

    /**
     * Startverzögerung für die Animation in Millisekunden.
     *
     * @param showDelayMillies int
     */
    public void setShowDelayMillies(final int showDelayMillies)
    {
        this.showDelayMillies = showDelayMillies;
    }

    /**
     * Zeitabstand zwischen den Animationen in Millisekunden.
     *
     * @param timerIncrementMillies int
     */
    public void setTimerIncrementMillies(final int timerIncrementMillies)
    {
        this.timerIncrementMillies = timerIncrementMillies;
    }

    /**
     * @see javax.swing.JComponent#setVisible(boolean)
     */
    @Override
    public void setVisible(final boolean flag)
    {
        setAlpha(this.alphaStart);

        super.setVisible(flag);

        // Ueber setVisible den Timer der Childs beenden, falls implementiert
        Component[] childs = getComponents();

        for (Component child : childs)
        {
            child.setVisible(flag);
        }
    }
}
