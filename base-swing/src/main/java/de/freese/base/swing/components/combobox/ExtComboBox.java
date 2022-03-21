package de.freese.base.swing.components.combobox;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
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
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public class ExtComboBox<T> extends JComboBox<T>
{
    /**
     *
     */
    private static final long serialVersionUID = -5210391879154918454L;

    /**
     * UI für die automatische Popup Grösse.
     *
     * @param <T> Konkreter Typ
     *
     * @author Thomas Freese
     */
    private static class AutowidthComboBoxUI<T> extends MetalComboBoxUI
    {
        /**
         * Popup mit automatischer Grösse.
         *
         * @author Thomas Freese
         */
        protected class AutowidthComboPopup extends BasicComboPopup
        {
            /**
             *
             */
            private static final long serialVersionUID = 5619503805323024632L;

            /**
             * Erstellt ein neues {@link AutowidthComboPopup} Objekt.
             *
             * @param comboBox {@link JComboBox}
             */
            @SuppressWarnings("unchecked")
            public AutowidthComboPopup(final JComboBox<T> comboBox)
            {
                super((JComboBox<Object>) comboBox);
            }

            /**
             * @see javax.swing.plaf.basic.BasicComboPopup#computePopupBounds(int, int, int, int)
             */
            @SuppressWarnings(
                    {
                            "rawtypes", "unchecked"
                    })
            @Override
            protected Rectangle computePopupBounds(final int px, final int py, final int pw, final int ph)
            {
                Rectangle rect = super.computePopupBounds(px, py, pw, ph);

                int width = rect.width;
                int itemCount = this.comboBox.getItemCount();
                ListCellRenderer renderer = this.comboBox.getRenderer();

                // If the scroll bar appears, need to accommodate !
                int scroll = 0;

                if (this.comboBox.getMaximumRowCount() < itemCount)
                {
                    scroll = 20;
                }

                // Breitestes Element ermitteln
                for (int i = 0; i < itemCount; i++)
                {
                    Component c = renderer.getListCellRendererComponent(getList(), this.comboBox.getItemAt(i), i, false, false);

                    if (c instanceof JLabel label)
                    {
                        int labelWidth = 1 + c.getFontMetrics(c.getFont()).stringWidth(label.getText());

                        width = Math.max(width, labelWidth + scroll);
                    }
                }

                rect.setBounds(rect.x, rect.y, width, rect.height);

                return rect;
            }

            /**
             * @see javax.swing.plaf.basic.BasicComboPopup#configureList()
             */
            @Override
            protected void configureList()
            {
                super.configureList();

                this.list.setForeground(UIManager.getColor("MenuItem.foreground"));
                this.list.setBackground(UIManager.getColor("MenuItem.background"));
            }

            /**
             * @see javax.swing.plaf.basic.BasicComboPopup#configureScroller()
             */
            @Override
            protected void configureScroller()
            {
                super.configureScroller();

                this.scroller.getVerticalScrollBar().putClientProperty(MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
            }
        }

        /**
         * @see javax.swing.plaf.metal.MetalComboBoxUI#createPopup()
         */
        @SuppressWarnings("unchecked")
        @Override
        protected ComboPopup createPopup()
        {
            BasicComboPopup popup = new AutowidthComboPopup((JComboBox<T>) this.comboBox);
            popup.getAccessibleContext().setAccessibleParent(this.comboBox);

            return popup;
        }
    }

    /**
     *
     */
    private boolean fireOnNull;

    /**
     * Erstellt ein neues {@link ExtComboBox} Objekt.
     */
    public ExtComboBox()
    {
        super();

        initialize();
    }

    /**
     * Erstellt ein neues {@link ExtComboBox} Objekt.
     *
     * @param aModel {@link ComboBoxModel}
     */
    public ExtComboBox(final ComboBoxModel<T> aModel)
    {
        super(aModel);

        initialize();
    }

    /**
     * Erstellt ein neues {@link ExtComboBox} Objekt.
     *
     * @param items Object[]
     */
    public ExtComboBox(final T[] items)
    {
        super(items);

        initialize();
    }

    /**
     * Erstellt ein neues {@link ExtComboBox} Objekt.
     *
     * @param items {@link Vector}
     */
    public ExtComboBox(final Vector<T> items)
    {
        super(items);

        initialize();
    }

    /**
     * true = SelectedEvent wird auch gefeuert wenn Object = NULL, false = Defaultverhalten
     *
     * @return boolean
     */
    public boolean isFireOnNull()
    {
        return this.fireOnNull;
    }

    /**
     * true = SelectedEvent soll auch gefeuert werden, wenn Object = NULL, false = Defaultverhalten
     *
     * @param fireOnNull boolean
     */
    public void setFireOnNull(final boolean fireOnNull)
    {
        this.fireOnNull = fireOnNull;
    }

    /**
     * @see javax.swing.JComboBox#selectedItemChanged()
     */
    @Override
    protected void selectedItemChanged()
    {
        if (this.selectedItemReminder != null)
        {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this.selectedItemReminder, ItemEvent.DESELECTED));
        }

        // set the new selected item.
        this.selectedItemReminder = this.dataModel.getSelectedItem();

        // Ausnahmeimplementierung, Event soll auch gefeuert werden, wenn Object
        // = NULL
        if ((this.selectedItemReminder != null) || ((this.selectedItemReminder == null) && isFireOnNull()))
        {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this.selectedItemReminder, ItemEvent.SELECTED));
        }
    }

    /**
     * Initialisiert die ComboBox.
     */
    private void initialize()
    {
        setUI(new AutowidthComboBoxUI<T>());
        setKeySelectionManager(new RendererKeySelectionManager(this));
    }
}
