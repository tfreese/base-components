package de.freese.base.swing.components.table.header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * GroupableTableHeader
 *
 * @author Nobuo Tamemasa
 *
 * @version 1.0 10/20/98
 */
public class GroupableTableHeader extends JTableHeader
{
    /**
     *
     */
    private static final long serialVersionUID = -1642321287557836367L;
    /**
     *
     */
    @SuppressWarnings("unused")
    private static final String UI_CLASS_ID = "GroupableTableHeaderUI";
    /**
     *
     */
    protected final List<GroupableColumn> columnGroups = Collections.synchronizedList(new ArrayList<GroupableColumn>());

    /**
     * Creates a new {@link GroupableTableHeader} object.
     *
     * @param model {@link TableColumnModel}
     */
    public GroupableTableHeader(final TableColumnModel model)
    {
        super(model);

        setUI(new GroupableTableHeaderUI());
        setReorderingAllowed(false);
    }

    /**
     * @param gc {@link GroupableColumn}
     */
    public void addColumnGroup(final GroupableColumn gc)
    {
        this.columnGroups.add(gc);
    }

    /**
     * Entfernt die bereits vorhandenen
     */
    public void clearColumnGroups()
    {
        this.columnGroups.clear();
    }

    /**
     * @param tableColumn {@link TableColumn}
     *
     * @return {@link List}
     */
    public List<Object> getColumnGroups(final TableColumn tableColumn)
    {
        if (this.columnGroups == null)
        {
            return null;
        }

        for (GroupableColumn groupableColumn : this.columnGroups)
        {
            if (groupableColumn != null)
            {
                List<Object> groups = groupableColumn.getColumnGroups(tableColumn, new ArrayList<>());

                if (groups != null)
                {
                    return groups;
                }
            }
        }

        return null;
    }

    /**
     *
     */
    public void setColumnMargin()
    {
        if (this.columnGroups == null)
        {
            return;
        }

        int columnMargin = getColumnModel().getColumnMargin();

        for (GroupableColumn groupableColumn : this.columnGroups)
        {
            groupableColumn.setColumnMargin(columnMargin);
        }
    }

    /**
     * @see javax.swing.table.JTableHeader#setReorderingAllowed(boolean)
     */
    @Override
    public void setReorderingAllowed(final boolean b)
    {
        this.reorderingAllowed = false;
    }
}
