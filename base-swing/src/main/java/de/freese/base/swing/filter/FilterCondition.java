package de.freese.base.swing.filter;

import de.freese.base.swing.SupportsPropertyChange;

/**
 * Interface fuer eine Filterbedingung.
 *
 * @author Thomas Freese
 */
public interface FilterCondition extends SupportsPropertyChange
{
    /**
     * Liefert true, wenn die Bedingung passt.
     * 
     * @param object Object
     * @return boolean
     */
    public boolean test(Object object);
}
