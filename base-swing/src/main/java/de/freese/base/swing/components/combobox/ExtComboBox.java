package de.freese.base.swing.components.combobox;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.io.Serial;
import java.util.Vector;

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
    private static final class AutoWidthComboBoxUI<T> extends MetalComboBoxUI {
        /**
         * @author Thomas Freese
         */
        protected class AutoWidthComboPopup extends BasicComboPopup {
            @Serial
            private static final long serialVersionUID = 5619503805323024632L;

            @SuppressWarnings("unchecked")
            public AutoWidthComboPopup(final JComboBox<T> comboBox) {
                super((JComboBox<Object>) comboBox);
            }

            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            protected Rectangle computePopupBounds(final int px, final int py, final int pw, final int ph) {
                final Rectangle rect = super.computePopupBounds(px, py, pw, ph);

                int width = rect.width;
                final int itemCount = this.comboBox.getItemCount();
                final ListCellRenderer renderer = this.comboBox.getRenderer();

                // If the scroll bar appears, need to accommodate !
                int scroll = 0;

                if (this.comboBox.getMaximumRowCount() < itemCount) {
                    scroll = 20;
                }

                // Detect max. Width.
                for (int i = 0; i < itemCount; i++) {
                    final Component c = renderer.getListCellRendererComponent(getList(), this.comboBox.getItemAt(i), i, false, false);

                    if (c instanceof JLabel label) {
                        final int labelWidth = 1 + c.getFontMetrics(c.getFont()).stringWidth(label.getText());

                        width = Math.max(width, labelWidth + scroll);
                    }
                }

                rect.setBounds(rect.x, rect.y, width, rect.height);

                return rect;
            }

            @Override
            protected void configureList() {
                super.configureList();

                this.list.setForeground(UIManager.getColor("MenuItem.foreground"));
                this.list.setBackground(UIManager.getColor("MenuItem.background"));
            }

            @Override
            protected void configureScroller() {
                super.configureScroller();

                this.scroller.getVerticalScrollBar().putClientProperty(MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected ComboPopup createPopup() {
            final BasicComboPopup popup = new AutoWidthComboPopup((JComboBox<T>) this.comboBox);
            popup.getAccessibleContext().setAccessibleParent(this.comboBox);

            return popup;
        }
    }

    private boolean fireOnNull;

    public ExtComboBox() {
        super();

        initialize();
    }

    public ExtComboBox(final ComboBoxModel<T> aModel) {
        super(aModel);

        initialize();
    }

    public ExtComboBox(final T[] items) {
        super(items);

        initialize();
    }

    public ExtComboBox(final Vector<T> items) {
        super(items);

        initialize();
    }

    /**
     * true = SelectedEvent wird auch gefeuert wenn Object = NULL, false = Defaultverhalten
     */
    public boolean isFireOnNull() {
        return this.fireOnNull;
    }

    /**
     * true = SelectedEvent soll auch gefeuert werden, wenn Object = NULL, false = Defaultverhalten
     */
    public void setFireOnNull(final boolean fireOnNull) {
        this.fireOnNull = fireOnNull;
    }

    @Override
    protected void selectedItemChanged() {
        if (this.selectedItemReminder != null) {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this.selectedItemReminder, ItemEvent.DESELECTED));
        }

        // set the new selected item.
        this.selectedItemReminder = this.dataModel.getSelectedItem();

        // Fire Event even if Object = NULL
        if (this.selectedItemReminder != null || isFireOnNull()) {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this.selectedItemReminder, ItemEvent.SELECTED));
        }
    }

    private void initialize() {
        setUI(new AutoWidthComboBoxUI<T>());
        setKeySelectionManager(new RendererKeySelectionManager(this));
    }
}
