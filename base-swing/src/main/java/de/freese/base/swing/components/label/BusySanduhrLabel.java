package de.freese.base.swing.components.label;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.Serial;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * Label mit einer drehenden Sanduhr.
 *
 * @author Thomas Freese
 */
public class BusySanduhrLabel extends JLabel {
    @Serial
    private static final long serialVersionUID = -1861610997435401369L;

    private final Timer animateTimer;

    private final ImageIcon[] icons;

    private int imageIndex;

    public BusySanduhrLabel() {
        this("");
    }

    public BusySanduhrLabel(final String text) {
        this(text, WaitIcons.getWaitIcons());
    }

    public BusySanduhrLabel(final String text, ImageIcon[] icons) {
        super(text);

        this.icons = icons;

        this.animateTimer = new Timer(150, event -> {
            BusySanduhrLabel.this.imageIndex++;

            if (BusySanduhrLabel.this.imageIndex == BusySanduhrLabel.this.icons.length) {
                BusySanduhrLabel.this.imageIndex = 0;
            }

            setIcon(BusySanduhrLabel.this.icons[BusySanduhrLabel.this.imageIndex]);

            // System.out.println(imageIndex);
            if (!isVisible()) {
                BusySanduhrLabel.this.animateTimer.stop();
            }
            else {
                repaint();

                // The Toolkit.getDefaultToolkit().sync() synchronises the painting on systems that buffer graphics events.
                // Without this line, the animation might not be smooth on Linux.
                Toolkit.getDefaultToolkit().sync();
            }
        });
    }

    /**
     * @see javax.swing.JComponent#setVisible(boolean)
     */
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

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!this.animateTimer.isRunning() && isVisible()) {
            this.animateTimer.start();
        }
    }
}
