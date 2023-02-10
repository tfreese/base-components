package de.freese.base.swing.fontchange.handler;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Thomas Freese
 */
public class ListFontChangeHandler extends ComponentFontChangeHandler {
    /**
     * @see de.freese.base.swing.fontchange.handler.ComponentFontChangeHandler#fontChanged(java.awt.Font, java.lang.Object)
     */
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        JList<?> list = (JList<?>) object;
        int rowHeightNew = newFont.getSize() + 4;

        // if (list.getFixedCellHeight() < rowHeightNew) {
        list.setFixedCellHeight(rowHeightNew);

        ListCellRenderer<?> cellRenderer = list.getCellRenderer();

        if (cellRenderer instanceof Component) {
            super.fontChanged(newFont, cellRenderer);
        }
    }
}
