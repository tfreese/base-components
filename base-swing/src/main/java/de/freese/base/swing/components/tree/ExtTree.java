package de.freese.base.swing.components.tree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.io.Serial;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * Erweiterter JTree mit automatischem Scrolling bei DnD Aktionen.
 *
 * @author Thomas Freese
 */
public class ExtTree extends JTree implements Autoscroll {
    @Serial
    private static final long serialVersionUID = -9201687902908324380L;
    /**
     * Rand zur Aussenkomponente.
     */
    private int margin = 15;
    /**
     * Zeichnen des Autoscroll Rahmens ?
     */
    private boolean paintAutoscrollBorder;

    public ExtTree() {
        super();

        initialize();
    }

    public ExtTree(final Hashtable<?, ?> value) {
        super(value);

        initialize();
    }

    public ExtTree(final Object[] value) {
        super(value);

        initialize();
    }

    public ExtTree(final TreeModel newModel) {
        super(newModel);

        initialize();
    }

    public ExtTree(final TreeNode root) {
        super(root);

        initialize();
    }

    public ExtTree(final TreeNode root, final boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);

        initialize();
    }

    public ExtTree(final Vector<?> value) {
        super(value);

        initialize();
    }

    /**
     * @see java.awt.dnd.Autoscroll#autoscroll(java.awt.Point)
     */
    @Override
    public void autoscroll(final Point p) {
        int realRow = getRowForLocation(p.x, p.y);

        // Rectangle outer = getBounds();
        // realRow = (
        // ((p.y + outer.y) <= getMargin()) ? ((realRow < 1) ? 0 : (realRow - 1))
        // : ((realRow < (getRowCount() - 1)) ? (realRow + 1) : realRow)
        // );
        scrollRowToVisible(realRow);
    }

    /**
     * @see java.awt.dnd.Autoscroll#getAutoscrollInsets()
     */
    @Override
    public Insets getAutoscrollInsets() {
        Rectangle outer = getBounds();
        Rectangle inner = getParent().getBounds();

        int top = (inner.y - outer.y) + getMargin();
        int left = (inner.x - outer.x) + getMargin();
        int bottom = (outer.height - inner.height - inner.y) + outer.y + getMargin();
        int right = (outer.width - inner.width - inner.x) + outer.x + getMargin();

        return new Insets(top, left, bottom, right);
    }

    /**
     * Abstand vom Rand des Trees für DnD Scrolling.
     */
    public int getMargin() {
        return this.margin;
    }

    /**
     * Rendert einen Border die DnD sensitive Fläche.
     */
    public boolean isPaintAutoscrollBorder() {
        return this.paintAutoscrollBorder;
    }

    /**
     * Abstand vom Rand des Trees für DnD Scrolling.
     */
    public void setMargin(final int margin) {
        this.margin = margin;
    }

    /**
     * Rendert einen Border die DnD sensitive Fläche.
     */
    public void setPaintAutoscrollBorder(final boolean paintAutoscrollBorder) {
        this.paintAutoscrollBorder = paintAutoscrollBorder;
    }

    protected void initialize() {
        setAutoscrolls(true);
    }

    /**
     * Zeichnet den Tree, in Abhängigkeit von isPaintAutoscrollBorder auch den Autoscroll-Rahmen.
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (isPaintAutoscrollBorder()) {
            Rectangle outer = getBounds();
            Rectangle inner = getParent().getBounds();

            g.setColor(Color.red);
            g.drawRect(-outer.x + getMargin(), -outer.y + getMargin(), inner.width - (getMargin() * 2), inner.height - (getMargin() * 2));
        }
    }
}
