/**
 * Created: 27.01.2014
 */

package de.freese.base.pool.erasoft;

import java.util.NoSuchElementException;
import de.freese.base.pool.AbstractObjectPool;
import de.freese.base.pool.ObjectPool;
import nf.fr.eraasoft.pool.PoolException;
import nf.fr.eraasoft.pool.PoolSettings;
import nf.fr.eraasoft.pool.impl.AbstractPool;

/**
 * {@link AbstractPool}-Adapter für {@link ObjectPool}.
 *
 * @author Thomas Freese
 * @param <T> Konkreter ObjectTyp
 */
public class ErasoftObjectPoolAdapter<T> extends AbstractObjectPool<T>
{
    /**
     *
     */
    private final AbstractPool<T> objectPool;

    // /**
    // * Erstellt ein neues {@link ErasoftObjectPoolAdapter} Object.
    // *
    // * @param poolableObject {@link PoolableObject}
    // */
    // public ErasoftObjectPoolAdapter(final PoolableObject<T> poolableObject)
    // {
    // this(new PoolSettings<>(poolableObject).min(0).max(0).maxWait(0).validateWhenReturn(false));
    // }

    /**
     * Erstellt ein neues {@link ErasoftObjectPoolAdapter} Object.
     *
     * @param poolSettings {@link PoolSettings}
     */
    public ErasoftObjectPoolAdapter(final PoolSettings<T> poolSettings)
    {
        super();

        this.objectPool = (AbstractPool<T>) poolSettings.pool();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#borrowObject()
     */
    @Override
    public synchronized T borrowObject()
    {
        T object = null;

        try
        {
            object = this.objectPool.getObj();
        }
        catch (PoolException ex)
        {
            throw new RuntimeException(ex);
        }

        if (object == null)
        {
            throw new NoSuchElementException("Pool exhausted");
        }

        return object;
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumActive()
     */
    @Override
    public int getNumActive()
    {
        return this.objectPool.actives();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#getNumIdle()
     */
    @Override
    public int getNumIdle()
    {
        return this.objectPool.idles();
    }

    /**
     * @see de.freese.base.pool.ObjectPool#returnObject(java.lang.Object)
     */
    @Override
    public void returnObject(final T object)
    {
        this.objectPool.returnObj(object);
    }

    /**
     * @see de.freese.base.pool.ObjectPool#shutdown()
     */
    @Override
    public void shutdown()
    {
        super.shutdown();

        ErasoftObjectPoolAdapter.this.objectPool.clear();
        ErasoftObjectPoolAdapter.this.objectPool.destroy();

        try
        {
            PoolSettings.shutdown();
        }
        catch (Exception ex)
        {
            // Ignore
        }
    }
}
