package de.freese.base.swing.components.watermark;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serial;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * @author Thomas Freese
 */
public class TransparentRenderer extends JLabel implements ListCellRenderer<Object>, TreeCellRenderer, TableCellRenderer {
    @Serial
    private static final long serialVersionUID = 2387759630865685848L;
    private final Color background;
    @SuppressWarnings("unused")
    private final Color focusBackground;
    private final transient Border focusBorder;
    @SuppressWarnings("unused")
    private final Color focusForeground;
    private final Color foreground;
    private final transient Border noFocusBorder;
    private final Color selectedBackground;
    private final Color selectedForeground;

    private int index = -1;

    public TransparentRenderer() {
        super();

        this.foreground = UIManager.getColor("Table.foreground");
        this.background = UIManager.getColor("Table.background");
        this.selectedForeground = UIManager.getColor("Table.selectionForeground");
        this.selectedBackground = UIManager.getColor("Table.selectionBackground");
        this.focusForeground = UIManager.getColor("Table.focusCellForeground");
        this.focusBackground = UIManager.getColor("Table.focusCellBackground");

        this.noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        this.focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");

        setOpaque(false);
        setBorder(this.noFocusBorder);
    }

    @Override
    public void firePropertyChange(final String propertyName, final boolean oldValue, final boolean newValue) {
        // Empty
    }

    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean hasFocus) {
        generalSetup(list, isSelected, hasFocus, index);

        if (value instanceof Icon i) {
            setIcon(i);
            setText("");
        }
        else {
            setIcon(null);
            setText((value == null) ? "" : value.toString());
        }

        return this;
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        generalSetup(table, isSelected, hasFocus, row);

        if (hasFocus) {
            if (table.isCellEditable(row, column)) {
                super.setForeground(UIManager.getColor("Table.focusCellForeground"));
                super.setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        }

        setText((value == null) ? "" : value.toString());

        return this;
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean isSelected, final boolean isExpanded, final boolean isLeaf, final int row,
                                                  final boolean hasFocus) {
        generalSetup(tree, isSelected, hasFocus, row);
        setText(tree.convertValueToText(value, isSelected, isExpanded, isLeaf, row, hasFocus));

        if (tree.isEnabled()) {
            setIcon(isLeaf ? UIManager.getIcon("Tree.leafIcon") : (isExpanded ? UIManager.getIcon("Tree.openIcon") : UIManager.getIcon("Tree.closedIcon")));
        }
        else {
            setDisabledIcon(isLeaf ? UIManager.getIcon("Tree.leafIcon") : (isExpanded ? UIManager.getIcon("Tree.openIcon") : UIManager.getIcon("Tree.closedIcon")));
        }

        return this;
    }

    @Override
    public void repaint(final long tm, final int x, final int y, final int width, final int height) {
        // Empty
    }

    @Override
    public void repaint(final Rectangle r) {
        // Empty
    }

    @Override
    public void revalidate() {
        // Empty
    }

    @Override
    public void validate() {
        // Empty
    }

    @Override
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        // Strings get interned...
        if ("text".equals(propertyName)) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Sets the Components colors relevant to ist current status.
     */
    protected void generalSetup(final JComponent parent, final boolean isSelected, final boolean hasFocus, final int index) {
        if (isSelected) {
            this.index = index;
            super.setForeground(this.selectedForeground);
            super.setBackground(this.selectedBackground);
        }
        else {
            super.setForeground(this.foreground);
            super.setBackground(this.background);
        }

        setFont(parent.getFont());

        if (hasFocus) {
            setBorder(this.focusBorder);
        }
        else {
            setBorder(this.noFocusBorder);
        }

        setOpaque(index == this.index);
    }

    /**
     * Sets the <code>String</code> object for the cell being rendered to <code>value</code>.
     *
     * @param value the string value for this cell; if value is <code>null</code> it sets the text value to an empty string
     */
    protected void setValue(final Object value) {
        setText((value == null) ? "" : value.toString());
    }
}
