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
public class WatermarkTable extends JPanel implements WatermarkComponent {
    @Serial
    private static final long serialVersionUID = 4400500160883780741L;

    private JScrollPane scrollPane;
    private JTable table;
    private WatermarkViewport viewport;

    /**
     * Constructor calls <code>init()</code> for initialization
     */
    public WatermarkTable() {
        super();

        init();
    }

    /**
     * Constructor calls <code>init()</code> for initialization
     */
    public WatermarkTable(final boolean isDoubleBuffered) {
        super(isDoubleBuffered);

        init();
    }

    public JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            setBackground(Color.WHITE);
            scrollPane.setOpaque(false);
            scrollPane.setViewport(getViewport());
        }

        return scrollPane;
    }

    public JTable getTable() {
        if (null == table) {
            table = new ExtTable();
            table.setOpaque(false);
            table.setDefaultRenderer(Object.class, new TransparentRenderer());
        }

        return table;
    }

    @Override
    public ImageIcon getWatermark() {
        return getViewport().getWatermark();
    }

    @Override
    public void setPosition(final Point position) {
        getViewport().setPosition(position);
    }

    @Override
    public void setWatermark(final ImageIcon watermark) {
        getViewport().setWatermark(watermark);
    }

    /**
     * Returns the special viewport which draws the image
     */
    private WatermarkViewport getViewport() {
        if (viewport == null) {
            viewport = new WatermarkViewport(getTable());
        }

        return viewport;
    }

    private void init() {
        setLayout(new BorderLayout());
        add(getScrollPane(), BorderLayout.CENTER);
    }
}
