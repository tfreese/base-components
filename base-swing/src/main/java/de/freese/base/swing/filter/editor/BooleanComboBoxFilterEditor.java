package de.freese.base.swing.filter.editor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import de.freese.base.swing.components.list.renderer.ToggleButtonListCellRenderer;
import de.freese.base.swing.fontchange.SwingFontSizeChanger;

/**
 * Klasse stellt eine {@link JComboBox} als Filter bereit.
 *
 * @author Thomas Freese
 */
public class BooleanComboBoxFilterEditor extends JComboBox<Object> implements FilterEditor, ItemListener
{
    /**
     * Konstante für keinen Filter.
     */
    private static final String KEIN_FILTER = " ";
    /**
     *
     */
    private static final long serialVersionUID = 8226362008750444885L;
    /**
     *
     */
    private final int column;

    /**
     * Erstellt ein neues {@link BooleanComboBoxFilterEditor} Objekt.
     *
     * @param column int
     */
    public BooleanComboBoxFilterEditor(final int column)
    {
        this(column, ToggleButtonListCellRenderer.createCheckBoxRenderer());
    }

    /**
     * Erstellt ein neues {@link BooleanComboBoxFilterEditor} Objekt.
     *
     * @param column int
     * @param renderer {@link ListCellRenderer}
     */
    public BooleanComboBoxFilterEditor(final int column, final ListCellRenderer<Object> renderer)
    {
        super();

        setRenderer(renderer);
        setEditable(false);
        this.column = column;

        Object[] values =
                {
                        KEIN_FILTER, Boolean.FALSE, Boolean.TRUE
                };

        setModel(new DefaultComboBoxModel<>(values));
        addItemListener(this);

        SwingFontSizeChanger.getInstance().register(this);
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#getColumn()
     */
    @Override
    public int getColumn()
    {
        return this.column;
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#getComponent()
     */
    @Override
    public JComponent getComponent()
    {
        return this;
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#getFilterPropertyName()
     */
    @Override
    public String getFilterPropertyName()
    {
        return getClass().getSimpleName();
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#getValue()
     */
    @Override
    public Object getValue()
    {
        return getSelectedItem();
    }

    /**
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(final ItemEvent e)
    {
        if (getSelectedItem() == BooleanComboBoxFilterEditor.KEIN_FILTER)
        {
            firePropertyChange(getFilterPropertyName(), null, null);
        }
        else
        {
            firePropertyChange(getFilterPropertyName(), null, getSelectedItem());
        }
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final Object value)
    {
        Boolean newBoolean = value == null ? null : (Boolean) value;

        setSelectedItem(newBoolean);
    }
}
