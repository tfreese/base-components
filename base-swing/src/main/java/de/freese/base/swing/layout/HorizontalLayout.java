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

    @Override
    public void addLayoutComponent(final String name, final Component c) {
        // Empty
    }

    public int getGap() {
        return gap;
    }

    @Override
    public void layoutContainer(final Container parent) {
        final Insets insets = parent.getInsets();
        final Dimension size = parent.getSize();

        final int height = size.height - insets.top - insets.bottom;
        int width = insets.left;

        for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
            final Component m = parent.getComponent(i);

            if (m.isVisible()) {
                m.setBounds(width, insets.top, m.getPreferredSize().width, height);
                width += m.getSize().width + gap;
            }
        }
    }

    @Override
    public Dimension minimumLayoutSize(final Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public Dimension preferredLayoutSize(final Container parent) {
        final Insets insets = parent.getInsets();
        final Dimension pref = new Dimension(0, 0);

        for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
            final Component m = parent.getComponent(i);

            if (m.isVisible()) {
                final Dimension componentPreferredSize = parent.getComponent(i).getPreferredSize();
                pref.height = Math.max(pref.height, componentPreferredSize.height);
                pref.width += componentPreferredSize.width + gap;
            }
        }

        pref.width += insets.left + insets.right;
        pref.height += insets.top + insets.bottom;

        return pref;
    }

    @Override
    public void removeLayoutComponent(final Component c) {
        // Empty
    }

    public void setGap(final int gap) {
        this.gap = gap;
    }
}
