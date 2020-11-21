package de.freese.base.swing.components.memory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Diagrammkomponente für Speicherverbrauch.
 *
 * @author Thomas Freese
 */
public class MemoryMonitorComponent extends JComponent
{
    /**
     *
     */
    private static final Font FONT = new Font("Arial", Font.PLAIN, 11);

    /**
     *
     */
    private static final long serialVersionUID = 1943256358818718790L;

    /**
     *
     */
    private BufferedImage bufferedImage;

    /**
     *
     */
    private int columnInc = 0;

    /**
     *
     */
    private Color graphColor;

    /**
     *
     */
    private Graphics2D graphics2D;

    /**
     *
     */
    private Color memoryFreeColor;

    /**
     *
     */
    private Rectangle2D memoryFreeRect;

    /**
     *
     */
    private Rectangle2D memoryUsedRect;

    /**
     *
     */
    private int ptNum = 0;

    /**
     *
     */
    private int[] pts;

    /**
     *
     */
    private final Runtime runtime = Runtime.getRuntime();

    /**
     *
     */
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     *
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * Erstellt ein neues {@link MemoryMonitorComponent} Object.
     */
    public MemoryMonitorComponent()
    {
        this(Executors.newScheduledThreadPool(1));
    }

    /**
     * Erstellt ein neues {@link MemoryMonitorComponent} Object.
     *
     * @param scheduledExecutorService {@link ScheduledExecutorService}
     */
    public MemoryMonitorComponent(final ScheduledExecutorService scheduledExecutorService)
    {
        super();

        this.scheduledExecutorService = scheduledExecutorService;

        this.memoryFreeRect = new Rectangle2D.Float();
        this.memoryUsedRect = new Rectangle2D.Float();

        this.graphColor = new Color(46, 139, 87);
        this.memoryFreeColor = new Color(0, 100, 0);

        setBackground(Color.BLACK);

        addComponentListener(new ComponentAdapter()
        {
            /**
             * @see java.awt.event.ComponentAdapter#componentHidden(java.awt.event.ComponentEvent)
             */
            @Override
            public void componentHidden(final ComponentEvent e)
            {
                SwingUtilities.invokeLater(MemoryMonitorComponent.this::stop);
            }

            /**
             * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
             */
            @Override
            public void componentResized(final ComponentEvent e)
            {
                MemoryMonitorComponent.this.bufferedImage = (BufferedImage) createImage(getWidth(), getHeight());
                MemoryMonitorComponent.this.graphics2D = MemoryMonitorComponent.this.bufferedImage.createGraphics();
            }

            /**
             * @see java.awt.event.ComponentAdapter#componentShown(java.awt.event.ComponentEvent)
             */
            @Override
            public void componentShown(final ComponentEvent e)
            {
                SwingUtilities.invokeLater(MemoryMonitorComponent.this::start);
            }
        });

        addMouseListener(new MouseAdapter()
        {
            /**
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseClicked(final MouseEvent e)
            {
                if (((e.getModifiersEx()) & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK)
                {
                    System.gc();
                }
                else if (MemoryMonitorComponent.this.scheduledFuture == null)
                {
                    start();
                }
                else
                {
                    stop();
                }
            }
        });

        // SwingUtilities.invokeLater(this::start);
        this.scheduledExecutorService.schedule(this::start, 500, TimeUnit.MILLISECONDS);
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
        return new Dimension(180, 80);
    }

    /**
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        if (this.bufferedImage == null)
        {
            return;
        }

        g.drawImage(this.bufferedImage, 0, 0, this);
        // g.drawImage(this.bufferedImage, 0, 0, getWidth(), getHeight(), null);

        g.dispose();
    }

    /**
     * @param g {@link Graphics2D}
     */
    private void paintDiagramm(final Graphics2D g)
    {
        if (g == null)
        {
            return;
        }

        g.setBackground(getBackground());
        g.clearRect(0, 0, getWidth(), getHeight());

        FontMetrics fm = g.getFontMetrics(FONT);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();

        float freeMemory = this.runtime.freeMemory();
        float totalMemory = this.runtime.totalMemory();
        g.setColor(Color.GREEN);
        g.setFont(FONT);
        g.drawString(String.valueOf((int) totalMemory / 1024) + "K allocated", 4F, ascent + 0.5F);
        g.drawString(String.valueOf((int) (totalMemory - freeMemory) / 1024) + "K used", 4, getHeight() - descent);

        float ssH = ascent + descent;
        float remainingHeight = getHeight() - (ssH * 2.0F) - 0.5F;
        float blockHeight = remainingHeight / 10F;
        float blockWidth = 20F;
        // float remainingWidth = this.w - blockWidth - 10F;
        g.setColor(this.memoryFreeColor);

        int memUsage = (int) ((freeMemory / totalMemory) * 10F);
        int i;

        for (i = 0; i < memUsage; i++)
        {
            this.memoryFreeRect.setRect(5D, ssH + (i * blockHeight), blockWidth, blockHeight - 1.0F);
            g.fill(this.memoryFreeRect);
        }

        g.setColor(Color.GREEN);

        for (; i < 10; i++)
        {
            this.memoryUsedRect.setRect(5D, ssH + (i * blockHeight), blockWidth, blockHeight - 1.0F);
            g.fill(this.memoryUsedRect);
        }

        g.setColor(this.graphColor);

        int graphX = 30;
        int graphY = (int) ssH;
        int graphWidth = getWidth() - graphX - 5;
        int graphHeight = (int) remainingHeight;
        g.drawRect(graphX, graphY, graphWidth, graphHeight);

        int graphRow = graphHeight / 10;

        for (int j = graphY; j <= (graphHeight + graphY); j += graphRow)
        {
            g.drawLine(graphX, j, graphX + graphWidth, j);
        }

        int graphColumn = graphWidth / 15;

        if (this.columnInc == 0)
        {
            this.columnInc = graphColumn;
        }

        for (int x = graphX + this.columnInc; x < (graphWidth + graphX); x += graphColumn)
        {
            g.drawLine(x, graphY, x, graphY + graphHeight);
        }

        this.columnInc--;

        if (this.pts == null)
        {
            this.pts = new int[graphWidth];
            this.ptNum = 0;
        }
        else if (this.pts.length != graphWidth)
        {
            int[] tmp = null;

            if (this.ptNum < graphWidth)
            {
                tmp = new int[this.ptNum];
                System.arraycopy(this.pts, 0, tmp, 0, tmp.length);
            }
            else
            {
                tmp = new int[graphWidth];
                System.arraycopy(this.pts, this.pts.length - tmp.length, tmp, 0, tmp.length);
                this.ptNum = tmp.length - 2;
            }

            this.pts = new int[graphWidth];
            System.arraycopy(tmp, 0, this.pts, 0, tmp.length);
        }
        else
        {
            g.setColor(Color.YELLOW);
            this.pts[this.ptNum] = (int) (graphY + (graphHeight * (freeMemory / totalMemory)));

            int j = (graphX + graphWidth) - this.ptNum;

            for (int k = 0; k < this.ptNum;)
            {
                if (k != 0)
                {
                    if (this.pts[k] != this.pts[k - 1])
                    {
                        g.drawLine(j - 1, this.pts[k - 1], j, this.pts[k]);
                    }
                    else
                    {
                        g.fillRect(j, this.pts[k], 1, 1);
                    }
                }

                k++;
                j++;
            }

            if ((this.ptNum + 2) == this.pts.length)
            {
                for (int k = 1; k < this.ptNum; k++)
                {
                    this.pts[k - 1] = this.pts[k];
                }

                this.ptNum--;
            }
            else
            {
                this.ptNum++;
            }
        }

        repaint();
    }

    /**
     *
     */
    public void start()
    {
        stop();

        if (!isShowing() || (getWidth() == 0) || (getHeight() == 0))
        {
            stop();
            return;
        }

        // Runnable runnable = this::paintDiagramm;
        Runnable runnable = () -> paintDiagramm(this.graphics2D);
        this.scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(runnable, 500, 500, TimeUnit.MILLISECONDS);
    }

    /**
     *
     */
    public void stop()
    {
        if ((this.scheduledFuture != null))
        {
            this.scheduledFuture.cancel(false);
            this.scheduledFuture = null;
        }

        // notify();
    }
}
