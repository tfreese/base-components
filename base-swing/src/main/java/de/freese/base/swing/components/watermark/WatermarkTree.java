package de.freese.base.swing.components.watermark;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * Kapselt einen {@link JTree} mit Watermark.
 *
 * @author Thomas Freese
 */
public class WatermarkTree extends JPanel implements IWatermarkComponent
{
    /**
     *
     */
    private static final long serialVersionUID = -7736500453003929181L;
    /**
     *
     */
    private JScrollPane scrollpane;
    /**
     *
     */
    private JTree tree;
    /**
     *
     */
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
     *
     * @param isDoubleBuffered boolean
     */
    public WatermarkTree(final boolean isDoubleBuffered)
    {
        super(isDoubleBuffered);

        init();
    }

    /**
     * Returns the customized scrollpane.
     *
     * @return {@link JScrollPane}
     */
    public JScrollPane getScrollPane()
    {
        if (null == this.scrollpane)
        {
            this.scrollpane = new JScrollPane();
            setBackground(Color.WHITE);
            this.scrollpane.setOpaque(false);
            this.scrollpane.setViewport(getViewport());
        }

        return this.scrollpane;
    }

    /**
     * Returns the customized Tree.
     *
     * @return {@link JTree}
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
     * Returns the special viewport which draws the image
     *
     * @return {@link WatermarkViewport}
     */
    private WatermarkViewport getViewport()
    {
        if (null == this.viewport)
        {
            this.viewport = new WatermarkViewport(getTree());
        }

        return this.viewport;
    }

    /**
     * @see de.freese.base.swing.components.watermark.IWatermarkComponent#getWatermark()
     */
    @Override
    public ImageIcon getWatermark()
    {
        return getViewport().getWatermark();
    }

    /**
     * Initializes the Component.
     */
    private void init()
    {
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
    }

    /**
     * @see de.freese.base.swing.components.watermark.IWatermarkComponent#setPosition(java.awt.Point)
     */
    @Override
    public void setPosition(final Point position)
    {
        getViewport().setPosition(position);
    }

    /**
     * @see de.freese.base.swing.components.watermark.IWatermarkComponent#setWatermark(javax.swing.ImageIcon)
     */
    @Override
    public void setWatermark(final ImageIcon watermark)
    {
        getViewport().setWatermark(watermark);
    }
}
