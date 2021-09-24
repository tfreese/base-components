package de.freese.base.swing.components.table.header;

import java.awt.Component;
import java.awt.Dimension;
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
 * ColumnGroup.
 *
 * @author Nobuo Tamemasa
 *
 * @version 1.0 10/20/98
 */
public class GroupableColumn
{
    /**
     *
     */
    protected List<Object> columns = Collections.synchronizedList(new ArrayList<>());
    /**
     *
     */
    protected int margin;
    /**
     *
     */
    protected TableCellRenderer renderer;
    /**
     *
     */
    protected String text;

    /**
     * Creates a new {@link GroupableColumn} object.
     *
     * @param text String
     */
    public GroupableColumn(final String text)
    {
        this(null, text);
    }

    /**
     * Creates a new ColumnGroup object.
     *
     * @param renderer {@link TableCellRenderer}
     * @param text String
     */
    public GroupableColumn(final TableCellRenderer renderer, final String text)
    {
        if (renderer == null)
        {
            this.renderer = new DefaultTableCellRenderer()
            {
                private static final long serialVersionUID = -7722809265471063718L;

                @Override
                public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
                                                               final int row, final int column)
                {
                    JTableHeader header = table.getTableHeader();

                    if (header != null)
                    {
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
        else
        {
            this.renderer = renderer;
        }

        this.text = text;
    }

    /**
     * @param tableColumn {@link GroupableColumn}
     */
    public void add(final GroupableColumn tableColumn)
    {
        if (tableColumn == null)
        {
            return;
        }

        this.columns.add(tableColumn);
    }

    /**
     * @param tableColumn {@link TableColumn}
     */
    public void add(final TableColumn tableColumn)
    {
        if (tableColumn == null)
        {
            return;
        }

        this.columns.add(tableColumn);
    }

    /**
     * @param tableColumn {@link TableColumn}
     * @param columns {@link List}
     *
     * @return {@link List}
     */
    List<Object> getColumnGroups(final TableColumn tableColumn, final List<Object> columns)
    {
        columns.add(this);

        if (this.columns.contains(tableColumn))
        {
            return columns;
        }

        for (Object column : this.columns)
        {
            if (column instanceof GroupableColumn c)
            {
                List<Object> groups = c.getColumnGroups(tableColumn, new ArrayList<>(columns));

                if (groups != null)
                {
                    return groups;
                }
            }
        }

        return null;
    }

    /**
     * @return {@link TableCellRenderer}
     */
    public TableCellRenderer getHeaderRenderer()
    {
        return this.renderer;
    }

    /**
     * @return Object
     */
    public Object getHeaderValue()
    {
        return this.text;
    }

    /**
     * @param table {@link JTable}
     *
     * @return {@link Dimension}
     */
    public Dimension getSize(final JTable table)
    {
        Component comp = this.renderer.getTableCellRendererComponent(table, getHeaderValue(), false, false, -1, -1);
        int height = comp.getPreferredSize().height;
        int width = 0;

        for (Object column : this.columns)
        {
            if (column instanceof TableColumn aColumn)
            {
                width += aColumn.getWidth();
                width += this.margin;
            }
            else
            {
                width += ((GroupableColumn) column).getSize(table).width;
            }
        }

        return new Dimension(width, height);
    }

    /**
     * @param margin int
     */
    public void setColumnMargin(final int margin)
    {
        this.margin = margin;

        for (Object column : this.columns)
        {
            if (column instanceof GroupableColumn c)
            {
                c.setColumnMargin(margin);
            }
        }
    }

    /**
     * @param renderer {@link TableCellRenderer}
     */
    public void setHeaderRenderer(final TableCellRenderer renderer)
    {
        if (renderer != null)
        {
            this.renderer = renderer;
        }
    }

    /**
     * @param text String
     */
    public void setText(final String text)
    {
        this.text = text;
    }
}
