package de.freese.base.swing.components.list.model;

import javax.swing.ListModel;
import de.freese.base.swing.eventlist.IEventList;

/**
 * Defaultimplementierung eines {@link ListModel} fuer die {@link IEventList}.
 *
 * @author Thomas Freese
 * @param <T> <T> Konkreter Typ
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
     * @param list {@link IEventList}
     */
    public DefaultEventListListModel(final IEventList<T> list)
    {
        super(list);
    }
}
