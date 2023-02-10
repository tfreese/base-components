package de.freese.base.swing.components.list.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * ListCellRenderer zum Rendern von {@link JCheckBox} und {@link JToggleButton}.
 *
 * @author Thomas Freese
 */
public final class ToggleButtonListCellRenderer implements ListCellRenderer<Object> {
    public static ToggleButtonListCellRenderer createCheckBoxRenderer() {
        return new ToggleButtonListCellRenderer(new JCheckBox());
    }

    public static ToggleButtonListCellRenderer createRadioButtonRenderer() {
        return new ToggleButtonListCellRenderer(new JRadioButton());
    }

    private final DefaultListCellRenderer labelRenderer;

    private final JToggleButton toggleButton;

    private ToggleButtonListCellRenderer(final JToggleButton toggleButton) {
        super();

        this.toggleButton = toggleButton;
        this.toggleButton.setHorizontalAlignment(SwingConstants.CENTER);
        this.labelRenderer = new DefaultListCellRenderer();
        this.labelRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        if (value instanceof Boolean b) {
            if (isSelected) {
                this.toggleButton.setBackground(list.getSelectionBackground());
                this.toggleButton.setForeground(list.getSelectionForeground());
            }
            else {
                this.toggleButton.setBackground(list.getBackground());
                this.toggleButton.setForeground(list.getForeground());
            }

            this.toggleButton.setSelected(b);

            return this.toggleButton;
        }

        return this.labelRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
