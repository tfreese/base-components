package de.freese.base.core.model;

import java.util.Date;

/**
 * Interface fuer alle Objekte, die ein {@link Date} liefern koennen.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface DateProvider
{
    /**
     * Liefert des Datum des Objektes.
     *
     * @return Date
     */
    public Date getDate();
}
