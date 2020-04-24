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
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class VerticalWrapListCellRenderer<T> implements ListCellRenderer<T>
{
    /**
     *
     */
    private final ListCellRenderer<T> delegate;

    /**
     *
     */
    private final Border matteBorder;

    /**
     * Erstellt ein neues {@link VerticalWrapListCellRenderer} Objekt.
     *
     * @param delegate {@link ListCellRenderer}
     */
    public VerticalWrapListCellRenderer(final ListCellRenderer<T> delegate)
    {
        super();

        this.delegate = delegate;
        this.matteBorder = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK);
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(final JList<? extends T> list, final T value, final int index, final boolean isSelected,
                                                  final boolean cellHasFocus)
    {
        Component component = this.delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // 16 <=> @see JList#getPreferredScrollableViewportSize()
        int fixedCellHeight = list.getFixedCellHeight() <= 0 ? 16 : list.getFixedCellHeight();
        int visibleRowCount = Math.max(1, (list.getHeight() / fixedCellHeight));

        if (!isSelected && ((index % (2 * visibleRowCount)) >= visibleRowCount))
        {
            component.setBackground(UIManager.getColor("Table.alternateRowColor"));
        }

        if ((index >= visibleRowCount) && (component instanceof JComponent))
        {
            JComponent jComponent = (JComponent) component;
            Border compoundBorder = BorderFactory.createCompoundBorder(this.matteBorder, jComponent.getBorder());
            jComponent.setBorder(compoundBorder);
        }

        return component;
    }
}
