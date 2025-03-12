package de.freese.base.swing.components.datepicker;

import java.awt.Component;
import java.io.Serial;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

import org.jdesktop.swingx.JXDatePicker;

/**
 * Alternative: <a href="https://github.com/LGoodDatePicker/LGoodDatePicker">LGoodDatePicker</a>
 *
 * @author Thomas Freese
 */
public class DatePickerCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DatePicker datePicker;

    public DatePickerCellEditor(final DatePicker datePicker) {
        super();

        this.datePicker = datePicker;
        this.datePicker.addActionListener(event -> {
            if (JXDatePicker.COMMIT_KEY.equals(event.getActionCommand())) {
                stopCellEditing();
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return datePicker.getDate();
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
        datePicker.setDate((Date) value);

        return datePicker;
    }

    @Override
    public Component getTreeCellEditorComponent(final JTree tree, final Object value, final boolean isSelected, final boolean expanded, final boolean leaf, final int row) {
        Date date = null;

        if (value instanceof Calendar c) {
            date = c.getTime();
        }
        else if (value instanceof Date d) {
            date = d;
        }

        datePicker.setDate(date);

        return datePicker;
    }
}
