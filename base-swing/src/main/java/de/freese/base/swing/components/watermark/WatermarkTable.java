package de.freese.base.swing.components.watermark;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;

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
public class WatermarkTable extends JPanel implements IWatermarkComponent
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4400500160883780741L;

	/**
	 *
	 */
	private JScrollPane scrollpane = null;

	/**
	 *
	 */
	private JTable table = null;

	/**
	 * 
	 */
	private WatermarkViewport viewport = null;

	/**
	 * Constructor calls <code>init()</code> for initialization
	 * 
	 * @param isDoubleBuffered boolean
	 */
	public WatermarkTable(final boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);

		init();
	}

	/**
	 * Constructor calls <code>init()</code> for initialization
	 */
	public WatermarkTable()
	{
		super();

		init();
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
	 * Returns the customized scrollpane
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
	 * Returns the customized Table.
	 * 
	 * @return {@link JTable}
	 */
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
	 * @see de.freese.base.swing.components.watermark.IWatermarkComponent#setWatermark(javax.swing.ImageIcon)
	 */
	@Override
	public void setWatermark(final ImageIcon watermark)
	{
		getViewport().setWatermark(watermark);
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
	 * Returns the special viewport which draws the image
	 * 
	 * @return {@link WatermarkViewport}
	 */
	private WatermarkViewport getViewport()
	{
		if (null == this.viewport)
		{
			this.viewport = new WatermarkViewport(getTable());
		}

		return this.viewport;
	}

	/**
	 * Initializes the Component.
	 */
	private void init()
	{
		setLayout(new BorderLayout());
		add(getScrollPane(), BorderLayout.CENTER);
	}
}
