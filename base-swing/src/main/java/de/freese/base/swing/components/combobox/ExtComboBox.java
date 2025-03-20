package de.freese.base.swing.components.combobox;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.io.Serial;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.plaf.metal.MetalScrollBarUI;

/**
 * ExtComboBox, deren Popup automatisch die Grösse besitzt, um den Inhalt komplett darzustellen, auch wenn die ExtComboBox kleiner ist.
 *
 * @author Thomas Freese
 */
public class ExtComboBox<T> extends JComboBox<T> {
    @Serial
    private static final long serialVersionUID = -5210391879154918454L;

    /**
     * @author Thomas Freese
     */
    protected static class AutoWidthComboBoxUI extends MetalComboBoxUI {
        @Override
        protected ComboPopup createPopup() {
            final BasicComboPopup basicComboPopup = new AutoWidthComboPopup(comboBox);
            basicComboPopup.getAccessibleContext().setAccessibleParent(comboBox);

            return basicComboPopup;
        }
    }

    /**
     * @author Thomas Freese
     */
    protected static class AutoWidthComboPopup extends BasicComboPopup {
        @Serial
        private static final long serialVersionUID = 5619503805323024632L;

        protected AutoWidthComboPopup(final JComboBox<Object> comboBox) {
            super(comboBox);
        }

        @Override
        protected Rectangle computePopupBounds(final int px, final int py, final int pw, final int ph) {
            final Rectangle rectangle = super.computePopupBounds(px, py, pw, ph);

            final int itemCount = comboBox.getItemCount();
            final ListCellRenderer<Object> renderer = comboBox.getRenderer();

            int width = rectangle.width;

            // Detect max. width.
            for (int i = 0; i < itemCount; i++) {
                final Component c = renderer.getListCellRendererComponent(getList(), comboBox.getItemAt(i), i, false, false);

                if (c instanceof JLabel label) {
                    final int labelWidth = c.getFontMetrics(c.getFont()).stringWidth(label.getText());

                    width = Math.max(width, labelWidth);
                }
                else {
                    width = Math.max(width, c.getPreferredSize().width);
                }
            }

            // If the scroll bar appears.
            if (comboBox.getMaximumRowCount() < itemCount) {
                width += UIManager.getInt("ScrollBar.width");
            }

            int height = rectangle.height;

            // Add some space.
            width += 5;
            height += 5;

            rectangle.setBounds(rectangle.x, rectangle.y, width, height);

            return rectangle;
        }

        @Override
        protected void configureList() {
            super.configureList();

            list.setForeground(UIManager.getColor("MenuItem.foreground"));
            list.setBackground(UIManager.getColor("MenuItem.background"));
        }

        @Override
        protected void configureScroller() {
            super.configureScroller();

            scroller.getVerticalScrollBar().putClientProperty(MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
        }
    }

    private boolean fireOnNull;

    public ExtComboBox() {
        super();

        initialize();
    }

    public ExtComboBox(final ComboBoxModel<T> model) {
        super(model);

        initialize();
    }

    public ExtComboBox(final List<T> items) {
        super();

        initialize();

        items.forEach(this::addItem);
    }

    /**
     * true = SelectedEvent wird auch gefeuert wenn Object = NULL, false = Defaultverhalten
     */
    public boolean isFireOnNull() {
        return fireOnNull;
    }

    /**
     * true = SelectedEvent soll auch gefeuert werden, wenn Object = NULL, false = Defaultverhalten
     */
    public void setFireOnNull(final boolean fireOnNull) {
        this.fireOnNull = fireOnNull;
    }

    @Override
    protected void selectedItemChanged() {
        if (selectedItemReminder != null) {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, selectedItemReminder, ItemEvent.DESELECTED));
        }

        // Set the new selected item.
        selectedItemReminder = dataModel.getSelectedItem();

        // Fire Event even if Object = NULL.
        if (selectedItemReminder != null || isFireOnNull()) {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, selectedItemReminder, ItemEvent.SELECTED));
        }
    }

    private void initialize() {
        setUI(new AutoWidthComboBoxUI());
        setKeySelectionManager(new RendererKeySelectionManager(this));
    }
}
