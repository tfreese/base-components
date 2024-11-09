package de.freese.base.swing.components.table.header;

import java.awt.Component;
import java.awt.Dimension;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * @author Nobuo Tamemasa
 * @version 1.0 10/20/98
 */
public class GroupableColumn {
    private final List<Object> columns = Collections.synchronizedList(new ArrayList<>());

    private int margin;
    private TableCellRenderer renderer;
    private String text;

    public GroupableColumn(final String text) {
        this(null, text);
    }

    public GroupableColumn(final TableCellRenderer renderer, final String text) {
        super();
        
        if (renderer == null) {
            this.renderer = new DefaultTableCellRenderer() {
                @Serial
                private static final long serialVersionUID = -7722809265471063718L;

                @Override
                public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
                                                               final int column) {
                    final JTableHeader header = table.getTableHeader();

                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());
                    }

                    setHorizontalAlignment(SwingConstants.CENTER);
                    setText((value == null) ? "" : value.toString());
                    setBorder(UIManager.getBorder("TableHeader.cellBorder"));

                    return this;
                }
            };
        }
        else {
            this.renderer = renderer;
        }

        this.text = text;
    }

    public void add(final GroupableColumn tableColumn) {
        if (tableColumn == null) {
            return;
        }

        this.columns.add(tableColumn);
    }

    public void add(final TableColumn tableColumn) {
        if (tableColumn == null) {
            return;
        }

        this.columns.add(tableColumn);
    }

    public TableCellRenderer getHeaderRenderer() {
        return this.renderer;
    }

    public Object getHeaderValue() {
        return this.text;
    }

    public Dimension getSize(final JTable table) {
        final Component comp = this.renderer.getTableCellRendererComponent(table, getHeaderValue(), false, false, -1, -1);
        final int height = comp.getPreferredSize().height;
        int width = 0;

        for (Object column : this.columns) {
            if (column instanceof TableColumn aColumn) {
                width += aColumn.getWidth();
                width += this.margin;
            }
            else {
                width += ((GroupableColumn) column).getSize(table).width;
            }
        }

        return new Dimension(width, height);
    }

    public void setColumnMargin(final int margin) {
        this.margin = margin;

        for (Object column : this.columns) {
            if (column instanceof GroupableColumn c) {
                c.setColumnMargin(margin);
            }
        }
    }

    public void setHeaderRenderer(final TableCellRenderer renderer) {
        if (renderer != null) {
            this.renderer = renderer;
        }
    }

    public void setText(final String text) {
        this.text = text;
    }

    List<Object> getColumnGroups(final TableColumn tableColumn, final List<Object> columns) {
        columns.add(this);

        if (this.columns.contains(tableColumn)) {
            return columns;
        }

        for (Object column : this.columns) {
            if (column instanceof GroupableColumn c) {
                final List<Object> groups = c.getColumnGroups(tableColumn, new ArrayList<>(columns));

                if (groups != null) {
                    return groups;
                }
            }
        }

        return null;
    }
}
