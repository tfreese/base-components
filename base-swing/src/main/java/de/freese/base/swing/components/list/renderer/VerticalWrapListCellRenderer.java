package de.freese.base.swing.components.list.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * {@link ListCellRenderer} für eine Liste mit Vertical-Wrap.
 *
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public class VerticalWrapListCellRenderer<T> implements ListCellRenderer<T> {
    private final ListCellRenderer<T> delegate;
    private final Border matteBorder;

    public VerticalWrapListCellRenderer(final ListCellRenderer<T> delegate) {
        super();

        this.delegate = delegate;
        this.matteBorder = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK);
    }

    @Override
    public Component getListCellRendererComponent(final JList<? extends T> list, final T value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        final Component component = this.delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // 16 <=> see JList#getPreferredScrollableViewportSize()
        final int fixedCellHeight = list.getFixedCellHeight() <= 0 ? 16 : list.getFixedCellHeight();
        final int visibleRowCount = Math.max(1, list.getHeight() / fixedCellHeight);

        if (!isSelected && (index % (2 * visibleRowCount)) >= visibleRowCount) {
            component.setBackground(UIManager.getColor("Table.alternateRowColor"));
        }

        if (index >= visibleRowCount && component instanceof JComponent jComponent) {
            final Border compoundBorder = BorderFactory.createCompoundBorder(this.matteBorder, jComponent.getBorder());
            jComponent.setBorder(compoundBorder);
        }

        return component;
    }
}
