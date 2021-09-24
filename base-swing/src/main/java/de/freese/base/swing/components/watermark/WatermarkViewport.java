package de.freese.base.swing.components.watermark;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JViewport;

/**
 * Viewport fuer ein Watermark.
 *
 * @author Thomas Freese
 */
public class WatermarkViewport extends JViewport implements IWatermarkComponent
{
    /**
     *
     */
    private static final long serialVersionUID = 4443174665479649215L;
    /**
     *
     */
    private Point position;
    /**
     *
     */
    private ImageIcon watermark;

    /**
     * Constructor
     *
     * @param view Component
     */
    public WatermarkViewport(final Component view)
    {
        super();

        setView(view);
        setOpaque(false);
    }

    /**
     * @see de.freese.base.swing.components.watermark.IWatermarkComponent#getWatermark()
     */
    @Override
    public ImageIcon getWatermark()
    {
        return this.watermark;
    }

    /**
     * @see javax.swing.JViewport#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        if (this.watermark != null)
        {
            int x = 0;
            int y = 0;

            if (null != this.position)
            {
                x = (int) this.position.getX();
                y = (int) this.position.getY();
            }

            // Draw the background image
            // Rectangle d = getViewRect();
            // for( int x = 0; x < d.width; x += watermark.getIconWidth() )
            // for( int y = 0; y < d.height; y += watermark.getIconHeight() )
            g.drawImage(this.watermark.getImage(), x, y, null, null);

            // Do not use cached image for scrolling
            setScrollMode(JViewport.BLIT_SCROLL_MODE);
        }

        super.paint(g);
    }

    /**
     * @see de.freese.base.swing.components.watermark.IWatermarkComponent#setPosition(java.awt.Point)
     */
    @Override
    public void setPosition(final Point position)
    {
        this.position = position;
    }

    /**
     * @see de.freese.base.swing.components.watermark.IWatermarkComponent#setWatermark(javax.swing.ImageIcon)
     */
    @Override
    public void setWatermark(final ImageIcon watermark)
    {
        this.watermark = watermark;
    }
}
