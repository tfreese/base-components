package de.freese.base.swing.fontchange.handler;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

/**
 * @author Thomas Freese
 */
public class TreeFontChangeHandler extends ComponentFontChangeHandler {
    @Override
    public void fontChanged(final Font newFont, final Object object) {
        super.fontChanged(newFont, object);

        final JTree tree = (JTree) object;
        final int rowHeightNew = newFont.getSize() + 4;

        // if (tree.getRowHeight() < rowHeightNew) {
        tree.setRowHeight(rowHeightNew);

        // CellRenderer
        final TreeCellRenderer cellRenderer = tree.getCellRenderer();

        if (cellRenderer instanceof Component) {
            super.fontChanged(newFont, cellRenderer);
        }

        // CellEditor
        final TreeCellEditor cellEditor = tree.getCellEditor();
        final Component cellEditorComponent = cellEditor.getTreeCellEditorComponent(tree, tree.getModel().getRoot(), false, false, false, 0);

        if (cellEditorComponent != null) {
            super.fontChanged(newFont, cellEditorComponent);
        }
    }
}
