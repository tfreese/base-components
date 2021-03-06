package de.freese.base.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * LayoutManager fuer ein vertikales Layout.
 *
 * @author Thomas Freese
 */
public class VerticalLayout implements LayoutManager
{
    /**
     *
     */
    private int gap;

    /**
     * Erstellt ein neues {@link VerticalLayout} Object.
     */
    public VerticalLayout()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link VerticalLayout} Object.
     * 
     * @param gap int
     */
    public VerticalLayout(final int gap)
    {
        super();

        this.gap = gap;
    }

    /**
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
     */
    @Override
    public void addLayoutComponent(final String name, final Component c)
    {
        // Empty
    }

    /**
     * Liefert den Abstand zwischen den Komponenten.
     * 
     * @return int
     */
    public int getGap()
    {
        return this.gap;
    }

    /**
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    @Override
    public void layoutContainer(final Container parent)
    {
        Insets insets = parent.getInsets();
        Dimension size = parent.getSize();

        int width = size.width - insets.left - insets.right;
        int height = insets.top;

        for (int i = 0, c = parent.getComponentCount(); i < c; i++)
        {
            Component m = parent.getComponent(i);

            if (m.isVisible())
            {
                m.setBounds(insets.left, height, width, m.getPreferredSize().height);
                height += m.getSize().height + this.gap;
            }
        }
    }

    /**
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension minimumLayoutSize(final Container parent)
    {
        return preferredLayoutSize(parent);
    }

    /**
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension preferredLayoutSize(final Container parent)
    {
        Insets insets = parent.getInsets();
        Dimension pref = new Dimension(0, 0);

        for (int i = 0, c = parent.getComponentCount(); i < c; i++)
        {
            Component m = parent.getComponent(i);

            if (m.isVisible())
            {
                Dimension componentPreferredSize = parent.getComponent(i).getPreferredSize();
                pref.height += componentPreferredSize.height + this.gap;
                pref.width = Math.max(pref.width, componentPreferredSize.width);
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
    public void removeLayoutComponent(final Component c)
    {
        // Empty
    }

    /**
     * Setzt den Abstand zwischen den Komponenten.
     * 
     * @param gap int
     */
    public void setGap(final int gap)
    {
        this.gap = gap;
    }
}
