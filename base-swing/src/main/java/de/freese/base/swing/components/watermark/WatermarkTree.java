package de.freese.base.swing.components.watermark;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.io.Serial;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * Kapselt einen {@link JTree} mit Watermark.
 *
 * @author Thomas Freese
 */
public class WatermarkTree extends JPanel implements WatermarkComponent
{
    @Serial
    private static final long serialVersionUID = -7736500453003929181L;

    private JScrollPane scrollPane;

    private JTree tree;

    private WatermarkViewport viewport;

    /**
     * Constructor calls <code>init()</code> for initialization
     */
    public WatermarkTree()
    {
        super();

        init();
    }

    /**
     * Constructor calls <code>init()</code> for initialization
     */
    public WatermarkTree(final boolean isDoubleBuffered)
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

    /**
     * Returns the customized Tree.
     */
    public JTree getTree()
    {
        if (null == this.tree)
        {
            this.tree = new JTree();
            this.tree.setOpaque(false);
            this.tree.setCellRenderer(new TransparentRenderer());
        }

        return this.tree;
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
            this.viewport = new WatermarkViewport(getTree());
        }

        return this.viewport;
    }

    private void init()
    {
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
    }
}
