package de.freese.base.swing.filter.editor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import de.freese.base.swing.filter.Filter;
import de.freese.base.swing.fontchange.SwingFontSizeChanger;
import de.freese.base.utils.ComboBoxUtils;

/**
 * Klasse stellt eine {@link JComboBox} als Filter bereit.
 *
 * @author Thomas Freese
 */
public class ComboBoxFilterEditor extends JComboBox<Object> implements FilterEditor, ItemListener
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 8226362008750444885L;
    /**
     *
     */
    private final int column;

    /**
     * Erstellt ein neues {@link ComboBoxFilterEditor} Object.
     *
     * @param column int
     */
    public ComboBoxFilterEditor(final int column)
    {
        super();

        this.column = column;
        setModel(new DefaultComboBoxModel<>());

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
        if (getSelectedItem() == null)
        {
            return;
        }

        if (Filter.ALL.equals(getSelectedItem()))
        {
            firePropertyChange(getFilterPropertyName(), null, null);
        }
        else if (Filter.EMPTY.equals(getSelectedItem()))
        {
            firePropertyChange(getFilterPropertyName(), null, Filter.EMPTY);
        }
        else if (Filter.NOT_EMPTY.equals(getSelectedItem()))
        {
            firePropertyChange(getFilterPropertyName(), null, Filter.NOT_EMPTY);
        }
        else
        {
            firePropertyChange(getFilterPropertyName(), null, getSelectedItem());
        }
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#setValue(java.lang.Object)
     */
    @SuppressWarnings(
    {
            "unchecked", "rawtypes"
    })
    @Override
    public void setValue(final Object value)
    {
        if (getItemCount() > 0)
        {
            setSelectedIndex(0);
        }

        List<Object> temp = new ArrayList<>();
        temp.add(Filter.ALL);
        temp.add(Filter.EMPTY);
        temp.add(Filter.NOT_EMPTY);

        if (value instanceof Collection)
        {
            List temp2 = new ArrayList((Collection) value);
            Collections.sort(temp2);

            temp.addAll(temp2);
        }

        ComboBoxUtils.fillComboBox(this, temp);
    }
}
