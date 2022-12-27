package de.freese.base.swing.components.list.model;

import java.io.Serial;

import de.freese.base.swing.eventlist.EventList;

/**
 * @author Thomas Freese
 */
public class DefaultEventListListModel<T> extends AbstractEventListListModel<T>
{
    @Serial
    private static final long serialVersionUID = 6807392049340392193L;

    public DefaultEventListListModel(final EventList<T> list)
    {
        super(list);
    }
}
