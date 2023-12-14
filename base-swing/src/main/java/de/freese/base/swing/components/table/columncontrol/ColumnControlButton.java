package de.freese.base.swing.components.table.columncontrol;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import de.freese.base.swing.components.table.ExtTable;
import de.freese.base.swing.components.table.column.ExtTableColumnModel;
import de.freese.base.swing.icon.ColumnControlIcon;

/**
 * Button für die Spaltenkontrolle.
 *
 * @author Thomas Freese
 */
public class ColumnControlButton extends JButton {
    @Serial
    private static final long serialVersionUID = -1209957795127294654L;

    private final transient List<ExtTable> tables = new ArrayList<>();

    private transient ColumnControlWindow columnControlWindow;

    public ColumnControlButton(final ExtTable table) {
        super();

        if (!(table.getColumnModel() instanceof ExtTableColumnModel)) {
            throw new IllegalArgumentException("TableColumnModel muss vom Typ IExtTableColumnModel sein !");
        }

        this.tables.add(table);
        setIcon(new ColumnControlIcon());
        addActionListener(event -> {
            getColumnControlWindow().clear();
            getColumnControlWindow().fill();
            getColumnControlWindow().show();
        });
    }

    public List<ExtTable> getTables() {
        return this.tables;
    }

    protected ColumnControlWindow createColumnControlWindow() {
        return new ColumnControlWindow(this);
    }

    private ColumnControlWindow getColumnControlWindow() {
        if (this.columnControlWindow == null) {
            this.columnControlWindow = createColumnControlWindow();
        }

        return this.columnControlWindow;
    }
}
