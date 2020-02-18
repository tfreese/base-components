package de.freese.base.core.model;

import java.util.Date;

/**
 * Interface fuer alle Objekte, die ein {@link Date} haben koennen.
 *
 * @author Thomas Freese
 */
public interface Dateable extends DateProvider
{
    /**
     * Setzt das Date des Objektes.
     * 
     * @param value {@link Date}
     */
    public void setDate(Date value);
}
