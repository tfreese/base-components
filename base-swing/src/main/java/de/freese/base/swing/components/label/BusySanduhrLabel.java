package de.freese.base.swing.components.label;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import de.freese.base.utils.ImageUtils;

/**
 * Label mit einer drehenden Sanduhr.
 *
 * @author Thomas Freese
 */
public class BusySanduhrLabel extends JLabel
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

        frame.getContentPane().add(new BusySanduhrLabel("Taeschd"), BorderLayout.NORTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     *
     */
    private Timer animateTimer;

    /**
     *
     */
    private final ImageIcon[] icons;

    /**
     *
     */
    private int imageIndex;

    /**
     * Creates a new {@link BusySanduhrLabel} object.
     */
    public BusySanduhrLabel()
    {
        this("");
    }

    /**
     * Creates a new {@link BusySanduhrLabel} object.
     *
     * @param text String
     */
    public BusySanduhrLabel(final String text)
    {
        super(text);

        this.icons = WaitIcons.getWaitIcons();
        setIcon(ImageUtils.createEmptyIcon());

        this.animateTimer = new Timer(150, e -> {
            BusySanduhrLabel.this.imageIndex++;

            if (BusySanduhrLabel.this.imageIndex == BusySanduhrLabel.this.icons.length)
            {
                BusySanduhrLabel.this.imageIndex = 0;
            }

            setIcon(BusySanduhrLabel.this.icons[BusySanduhrLabel.this.imageIndex]);

            // System.out.println(imageIndex);
            if (!isVisible())
            {
                BusySanduhrLabel.this.animateTimer.stop();
            }
            else
            {
                repaint();
            }
        });
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(final Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!this.animateTimer.isRunning() && isVisible())
        {
            this.animateTimer.start();
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
}
