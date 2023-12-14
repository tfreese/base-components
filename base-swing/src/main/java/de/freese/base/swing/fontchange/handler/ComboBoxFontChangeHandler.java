package de.freese.base.swing.fontchange.handler;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.ListCellRenderer;

/**
 * @author Thomas Freese
 */
public class ComboBoxFontChangeHandler extends ComponentFontChangeHandler {
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        final JComboBox<?> comboBox = (JComboBox<?>) object;

        final ListCellRenderer<?> cellRenderer = comboBox.getRenderer();

        if (cellRenderer instanceof Component) {
            super.fontChanged(newFont, cellRenderer);
        }
    }
}
