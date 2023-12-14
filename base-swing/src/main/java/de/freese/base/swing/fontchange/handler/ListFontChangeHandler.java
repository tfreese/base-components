package de.freese.base.swing.fontchange.handler;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Thomas Freese
 */
public class ListFontChangeHandler extends ComponentFontChangeHandler {
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        final JList<?> list = (JList<?>) object;
        final int rowHeightNew = newFont.getSize() + 4;

        // if (list.getFixedCellHeight() < rowHeightNew) {
        list.setFixedCellHeight(rowHeightNew);

        final ListCellRenderer<?> cellRenderer = list.getCellRenderer();

        if (cellRenderer instanceof Component) {
            super.fontChanged(newFont, cellRenderer);
        }
    }
}
