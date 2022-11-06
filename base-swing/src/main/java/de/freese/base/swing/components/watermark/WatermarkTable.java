package de.freese.base.swing.components.watermark;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.io.Serial;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.freese.base.swing.components.table.ExtTable;

/**
 * Kapselt eine {@link JTable} mit Watermark.
 *
 * @author Thomas Freese
 */
public class WatermarkTable extends JPanel implements WatermarkComponent
{
    @Serial
    private static final long serialVersionUID = 4400500160883780741L;

    private JScrollPane scrollPane;

    private JTable table;

    private WatermarkViewport viewport;

    /**
     * Constructor calls <code>init()</code> for initialization
     */
    public WatermarkTable()
    {
        super();

        init();
    }

    /**
     * Constructor calls <code>init()</code> for initialization
     */
    public WatermarkTable(final boolean isDoubleBuffered)
    {
        super(isDoubleBuffered);

        init();
    }

    public JScrollPane getScrollPane()
    {
        if (null == this.scrollPane)
        {
            this.scrollPane = new JScrollPane();
            setBackground(Color.WHITE);
            this.scrollPane.setOpaque(false);
            this.scrollPane.setViewport(getViewport());
        }

        return this.scrollPane;
    }

    public JTable getTable()
    {
        if (null == this.table)
        {
            this.table = new ExtTable();
            this.table.setOpaque(false);
            this.table.setDefaultRenderer(Object.class, new TransparentRenderer());
        }

        return this.table;
    }

    /**
     * @see WatermarkComponent#getWatermark()
     */
    @Override
    public ImageIcon getWatermark()
    {
        return getViewport().getWatermark();
    }

    /**
     * @see WatermarkComponent#setPosition(java.awt.Point)
     */
    @Override
    public void setPosition(final Point position)
    {
        getViewport().setPosition(position);
    }

    /**
     * @see WatermarkComponent#setWatermark(javax.swing.ImageIcon)
     */
    @Override
    public void setWatermark(final ImageIcon watermark)
    {
        getViewport().setWatermark(watermark);
    }

    /**
     * Returns the special viewport which draws the image
     */
    private WatermarkViewport getViewport()
    {
        if (null == this.viewport)
        {
            this.viewport = new WatermarkViewport(getTable());
        }

        return this.viewport;
    }

    private void init()
    {
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
    }
}
