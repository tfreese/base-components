package de.freese.base.core.model;

/**
 * Interface fuer alle Objekte, die eine ID haben koennen.
 *
 * @author Thomas Freese
 */
public interface Identifiable extends IdentifierProvider
{
    /**
     * Setzt die ID des Objektes.
     *
     * @param value Long
     */
    public void setOid(Long value);
}
