// Created: 14.07.2020

package de.freese.base.swing.components.scrollpane;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * Synchronisiert 2 ScrollPanes miteinander.<br>
 * Einfache Variante: <code>
 * <pre>
 * JScrollPane sp1 = new JScrollPane(...);
 * JScrollBar v1 = sp1.getVerticalScrollBar();
 *
 * JScrollPane sp2 = new JScrollPane(...);
 * JScrollBar v2 = sp2.getVerticalScrollBar();
 *
 * v2.setModel(v1.getModel()); // Synchronize ScrollBar
 * </pre>
 * </code>
 *
 * @author Thomas Freese
 */
public class ScrollPaneSynchronizer implements AdjustmentListener {
    private final JScrollBar h1;
    private final JScrollBar h2;
    private final JScrollBar v1;
    private final JScrollBar v2;

    public ScrollPaneSynchronizer(final JScrollPane sp1, final JScrollPane sp2) {
        super();

        v1 = sp1.getVerticalScrollBar();
        h1 = sp1.getHorizontalScrollBar();
        v2 = sp2.getVerticalScrollBar();
        h2 = sp2.getHorizontalScrollBar();
    }

    @Override
    public void adjustmentValueChanged(final AdjustmentEvent event) {
        final JScrollBar scrollBar = (JScrollBar) event.getSource();

        final int value = scrollBar.getValue();
        JScrollBar target = null;

        if (scrollBar == v1) {
            target = v2;
        }
        else if (scrollBar == h1) {
            target = h2;
        }
        else if (scrollBar == v2) {
            target = v1;
        }
        else if (scrollBar == h2) {
            target = h1;
        }
        else {
            return;
        }

        target.setValue(value);
    }
}
