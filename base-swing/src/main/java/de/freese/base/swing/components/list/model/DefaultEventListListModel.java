package de.freese.base.swing.components.list.model;

import javax.swing.ListModel;

import de.freese.base.swing.eventlist.EventList;

/**
 * Defaultimplementierung eines {@link ListModel} für die {@link EventList}.
 *
 * @param <T> <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public class DefaultEventListListModel<T> extends AbstractEventListListModel<T>
{
    /**
     *
     */
    private static final long serialVersionUID = 6807392049340392193L;

    /**
     * Erstellt ein neues {@link DefaultEventListListModel} Object.
     *
     * @param list {@link EventList}
     */
    public DefaultEventListListModel(final EventList<T> list)
    {
        super(list);
    }
}
