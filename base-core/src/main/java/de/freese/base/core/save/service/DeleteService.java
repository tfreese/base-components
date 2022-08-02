package de.freese.base.core.save.service;

import java.io.Serializable;
import java.util.Collection;

import de.freese.base.core.save.SaveContext;

/**
 * Interface eines Services der Deletes ausführen kann.
 *
 * @author Thomas Freese
 */
public interface DeleteService
{
    /**
     * Führt ein Delete auf einem Objekt aus.
     *
     * @param object {@link Serializable}
     * @param context {@link SaveContext}
     *
     * @throws Exception Falls was schiefgeht.
     */
    void delete(Serializable object, SaveContext context) throws Exception;

    /**
     * Entfernt alle Objekte.
     *
     * @param toDelete {@link Collection}
     * @param context {@link SaveContext}
     *
     * @throws Exception Falls was schiefgeht.
     */
    void deleteAll(Collection<? extends Serializable> toDelete, SaveContext context) throws Exception;
}
