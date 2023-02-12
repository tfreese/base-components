package de.freese.base.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * @author Thomas Freese
 */
public class HorizontalLayout implements LayoutManager {
    private int gap;

    public HorizontalLayout() {
        super();
    }

    public HorizontalLayout(final int gap) {
        super();

        this.gap = gap;
    }

    /**
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
     */
    @Override
    public void addLayoutComponent(final String name, final Component c) {
        // Empty
    }

    public int getGap() {
        return this.gap;
    }

    /**
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    @Override
    public void layoutContainer(final Container parent) {
        Insets insets = parent.getInsets();
        Dimension size = parent.getSize();

        int height = size.height - insets.top - insets.bottom;
        int width = insets.left;

        for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
            Component m = parent.getComponent(i);

            if (m.isVisible()) {
                m.setBounds(width, insets.top, m.getPreferredSize().width, height);
                width += m.getSize().width + this.gap;
            }
        }
    }

    /**
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension minimumLayoutSize(final Container parent) {
        return preferredLayoutSize(parent);
    }

    /**
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension preferredLayoutSize(final Container parent) {
        Insets insets = parent.getInsets();
        Dimension pref = new Dimension(0, 0);

        for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
            Component m = parent.getComponent(i);

            if (m.isVisible()) {
                Dimension componentPreferredSize = parent.getComponent(i).getPreferredSize();
                pref.height = Math.max(pref.height, componentPreferredSize.height);
                pref.width += componentPreferredSize.width + this.gap;
            }
        }

        pref.width += insets.left + insets.right;
        pref.height += insets.top + insets.bottom;

        return pref;
    }

    /**
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    @Override
    public void removeLayoutComponent(final Component c) {
        // Empty
    }

    public void setGap(final int gap) {
        this.gap = gap;
    }
}
