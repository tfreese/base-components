package de.freese.base.swing.filter.editor;

import java.io.Serial;
import java.util.Date;

import javax.swing.JComponent;

import de.freese.base.swing.components.datepicker.DatePicker;

/**
 * Editor für das Filtern von Tabellen für {@link Date}s.
 *
 * @author Thomas Freese
 */
public class DatePickerFilterEditor extends DatePicker implements FilterEditor
{
    @Serial
    private static final long serialVersionUID = -3308667881098631844L;

    private final int column;

    public DatePickerFilterEditor(final int column)
    {
        super();

        this.column = column;
        setDate(null);
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
        // return getClass().getSimpleName();

        // "date" triggert das commit im DatePicker an
        return "date";
    }

    /**
     * @see de.freese.base.swing.filter.editor.FilterEditor#getValue()
     */
    @Override
    public Object getValue()
    {
        return getDate();
    }

    // /**
    // * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
    // */
    // public void propertyChange(final PropertyChangeEvent evt)
    // {
    // if (evt.getPropertyName().equals("date"))
    // {
    // firePropertyChange(getFilterPropertyName(), null, evt.getNewValue());
    // }
    // }

    @Override
    public void setValue(final Object value)
    {
        setDate((Date) value);
    }
}
