package de.freese.base.swing.fontchange.handler;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;

/**
 * @author Thomas Freese
 */
public class ComboBoxFontChangeHandler extends ComponentFontChangeHandler {
    /**
     * @see de.freese.base.swing.fontchange.handler.ComponentFontChangeHandler#fontChanged(java.awt.Font, java.lang.Object)
     */
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        JComboBox<?> comboBox = (JComboBox<?>) object;

        ListCellRenderer<?> cellRenderer = comboBox.getRenderer();

        if (cellRenderer instanceof Component) {
            super.fontChanged(newFont, cellRenderer);
        }
    }
}
