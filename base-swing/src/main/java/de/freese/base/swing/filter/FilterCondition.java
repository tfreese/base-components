package de.freese.base.swing.filter;

import de.freese.base.swing.SupportsPropertyChange;

/**
 * Interface für eine Filterbedingung.
 *
 * @author Thomas Freese
 */
public interface FilterCondition extends SupportsPropertyChange
{
    boolean test(Object object);
}
