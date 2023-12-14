package de.freese.base.swing.ui;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * @author Thomas Freese
 */
public class ThinHorizontalScrollBarUI extends BasicScrollBarUI {

    private final int height;

    public ThinHorizontalScrollBarUI(final int height) {
        super();

        this.height = height;
    }

    @Override
    public Dimension getMaximumSize(final JComponent c) {
        final Dimension d = super.getMaximumSize(c);

        return new Dimension(d.width, this.height);
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        final Dimension d = super.getPreferredSize(c);

        return new Dimension(d.width, this.height);
    }
}
