package de.freese.base.core.model;

/**
 * Interface fuer alle Objekte, die einen Namen liefern koennen.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface NameProvider
{
    /**
     * Liefert den Namen des Objektes.
     *
     * @return String
     */
    public String getName();
}
