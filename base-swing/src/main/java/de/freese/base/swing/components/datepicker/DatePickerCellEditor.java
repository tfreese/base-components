package de.freese.base.swing.components.datepicker;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.plaf.basic.BasicDatePickerUI;

/**
 * @author Thomas Freese
 */
public class DatePickerCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    // /**
    // * Beendet die Eingabe mit Enter.
    // *
    // * @author Thomas Freese
    // */
    // private class EnterAction extends AbstractAction
    // {
    // /**
    // *
    // */
    // private static final long serialVersionUID = 1L;
    //
    // /**
    // *
    // */
    // public static final String ID = "EnterAction";
    //
    // /**
    // * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    // */
    // public void actionPerformed(ActionEvent e)
    // {
    // stopCellEditing();
    // }
    // }

    /**
     * Beendet die Eingabe bei Enter (Event kommt aus {@link BasicDatePickerUI}).
     *
     * @author Thomas Freese
     */
    public static class DatePickerCommitListener implements ActionListener
    {
        /**
         *
         */
        private final CellEditor cellEditor;

        /**
         * Erstellt ein neues {@link DatePickerCommitListener} Object.
         *
         * @param cellEditor {@link CellEditor}
         */
        public DatePickerCommitListener(final CellEditor cellEditor)
        {
            super();

            this.cellEditor = cellEditor;
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent e)
        {
            if (JXDatePicker.COMMIT_KEY.equals(e.getActionCommand()))
            {
                this.cellEditor.stopCellEditing();
            }
        }
    }

    /**
     *
     */
    private final DatePicker datePicker;

    /**
     * Creates a new {@link DatePickerCellEditor} object.
     *
     * @param datePicker {@link DatePicker}
     */
    public DatePickerCellEditor(final DatePicker datePicker)
    {
        super();

        this.datePicker = datePicker;
        this.datePicker.addActionListener(new DatePickerCommitListener(this));

        // datePicker.getActionMap().put(JXDatePicker.COMMIT_KEY, new EnterAction());
        // datePicker.getInputMap().put(
        // KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.VK_UNDEFINED), JXDatePicker.COMMIT_KEY
        // );
    }

    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    @Override
    public Object getCellEditorValue()
    {
        return this.datePicker.getDate();
    }

    /**
     * liefert den DatePicker.
     *
     * @return {@link DatePicker}
     */
    public DatePicker getDatePicker()
    {
        return this.datePicker;
    }

    /**
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column)
    {
        this.datePicker.setDate((Date) value);

        return this.datePicker;
    }

    /**
     * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
     */
    @Override
    public Component getTreeCellEditorComponent(final JTree tree, final Object value, final boolean isSelected, final boolean expanded, final boolean leaf,
                                                final int row)
    {
        Date date = null;

        if (value instanceof Calendar c)
        {
            date = c.getTime();
        }
        else if (value instanceof Date d)
        {
            date = d;
        }

        this.datePicker.setDate(date);

        return this.datePicker;
    }
}
