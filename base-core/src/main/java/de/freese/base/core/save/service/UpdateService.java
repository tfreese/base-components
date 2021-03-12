package de.freese.base.core.save.service;

import java.io.Serializable;
import java.util.Collection;
import de.freese.base.core.save.SaveContext;

/**
 * Interface eines Services der Updates ausfuehren kann.
 *
 * @author Thomas Freese
 */
public interface UpdateService
{
    /**
     * Fuehrt ein Update auf einem Objekt aus.
     * 
     * @param object {@link Serializable}
     * @param context {@link SaveContext}
     * @throws Exception Falls was schief geht.
     */
    public void update(Serializable object, SaveContext context) throws Exception;

    /**
     * Fuehrt ein Update pro vorhandenen Objekt aus.
     * 
     * @param toInsert {@link Collection}
     * @param context {@link SaveContext}
     * @throws Exception Falls was schief geht.
     */
    public void updateAll(Collection<? extends Serializable> toInsert, SaveContext context) throws Exception;
}
