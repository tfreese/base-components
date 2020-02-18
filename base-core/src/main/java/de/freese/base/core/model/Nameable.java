package de.freese.base.core.model;

/**
 * Interface fuer alle Objekte, die einen Namen haben koennen.
 *
 * @author Thomas Freese
 */
public interface Nameable extends NameProvider
{
    /**
     * Setzt den Namen des Objektes.
     * 
     * @param value String
     */
    public void setName(String value);
}
