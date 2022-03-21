package de.freese.base.swing.components.combobox;

import javax.swing.ComboBoxModel;

import de.freese.base.swing.eventlist.IEventList;

/**
 * Defaultimplementierung eines {@link ComboBoxModel} für die {@link IEventList}.
 *
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public class DefaultEventListComboBoxModel<T> extends AbstractEventListComboBoxModel<T>
{
    /**
     *
     */
    private static final long serialVersionUID = -5052543281193053775L;

    /**
     * Erstellt ein neues {@link DefaultEventListComboBoxModel} Object.
     *
     * @param list {@link IEventList}
     */
    public DefaultEventListComboBoxModel(final IEventList<T> list)
    {
        super(list);
    }
}
