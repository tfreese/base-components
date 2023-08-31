package de.freese.base.swing.components.watermark;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serial;

import javax.swing.ImageIcon;
import javax.swing.JViewport;

/**
 * @author Thomas Freese
 */
public class WatermarkViewport extends JViewport implements WatermarkComponent {
    @Serial
    private static final long serialVersionUID = 4443174665479649215L;

    private Point position;

    private ImageIcon watermark;

    public WatermarkViewport(final Component view) {
        super();

        setView(view);
        setOpaque(false);
    }

    @Override
    public ImageIcon getWatermark() {
        return this.watermark;
    }

    @Override
    public void paint(final Graphics g) {
        if (this.watermark != null) {
            int x = 0;
            int y = 0;

            if (null != this.position) {
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

    @Override
    public void setPosition(final Point position) {
        this.position = position;
    }

    @Override
    public void setWatermark(final ImageIcon watermark) {
        this.watermark = watermark;
    }
}
