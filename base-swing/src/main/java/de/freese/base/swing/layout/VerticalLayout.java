package de.freese.base.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * @author Thomas Freese
 */
public class VerticalLayout implements LayoutManager {
    private int gap;

    public VerticalLayout() {
        super();
    }

    public VerticalLayout(final int gap) {
        super();

        this.gap = gap;
    }

    @Override
    public void addLayoutComponent(final String name, final Component c) {
        // Empty
    }

    public int getGap() {
        return this.gap;
    }

    @Override
    public void layoutContainer(final Container parent) {
        final Insets insets = parent.getInsets();
        final Dimension size = parent.getSize();

        final int width = size.width - insets.left - insets.right;
        int height = insets.top;

        for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
            final Component m = parent.getComponent(i);

            if (m.isVisible()) {
                m.setBounds(insets.left, height, width, m.getPreferredSize().height);
                height += m.getSize().height + this.gap;
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
                pref.height += componentPreferredSize.height + this.gap;
                pref.width = Math.max(pref.width, componentPreferredSize.width);
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
