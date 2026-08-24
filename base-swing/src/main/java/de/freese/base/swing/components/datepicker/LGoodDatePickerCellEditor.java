package de.freese.base.swing.components.datepicker;

import java.awt.Component;
import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Objects;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

/**
 * <a href="https://github.com/LGoodDatePicker/LGoodDatePicker">LGoodDatePicker</a>
 *
 * @author Thomas Freese
 */
@SuppressWarnings({"java:S2143"})
public class LGoodDatePickerCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DatePicker datePicker;

    public LGoodDatePickerCellEditor(final DatePickerSettings datePickerSettings) {
        super();

        this.datePicker = new DatePicker(datePickerSettings);
        this.datePicker.addDateChangeListener(event -> {
            if (!Objects.equals(event.getOldDate(), event.getNewDate())) {
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
        datePicker.setDate((LocalDate) value);

        return datePicker;
    }

    @Override
    public Component getTreeCellEditorComponent(final JTree tree, final Object value, final boolean isSelected, final boolean expanded, final boolean leaf, final int row) {
        final LocalDate localDate = switch (value) {
            case final Calendar c -> LocalDate.ofInstant(c.getTime().toInstant(), ZoneId.systemDefault());
            case final java.sql.Date d -> d.toLocalDate();
            case final java.util.Date d -> LocalDate.ofInstant(d.toInstant(), ZoneId.systemDefault());
            case final LocalDateTime ldt -> ldt.toLocalDate();
            case final LocalDate ld -> ld;
            default -> throw new IllegalArgumentException("Unsupported value type: " + value);
        };

        datePicker.setDate(localDate);

        return datePicker;
    }
}
