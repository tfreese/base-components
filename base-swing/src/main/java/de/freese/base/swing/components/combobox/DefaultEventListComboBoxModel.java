package de.freese.base.swing.components.combobox;

import java.io.Serial;

import javax.swing.ComboBoxModel;

import de.freese.base.swing.eventlist.EventList;

/**
 * Defaultimplementierung eines {@link ComboBoxModel} für die {@link EventList}.
 *
 * @param <T> Konkreter Typ
 *
 * @author Thomas Freese
 */
public class DefaultEventListComboBoxModel<T> extends AbstractEventListComboBoxModel<T>
{
    @Serial
    private static final long serialVersionUID = -5052543281193053775L;

    public DefaultEventListComboBoxModel(final EventList<T> list)
    {
        super(list);
    }
}
