package de.freese.base.core.model;

/**
 * Interface fuer alle Objekte, die eine ID liefern koennen.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface IdentifierProvider
{
    /**
     * Liefert die ID eines Objektes.
     *
     * @return Long
     */
    public Long getOid();
}
