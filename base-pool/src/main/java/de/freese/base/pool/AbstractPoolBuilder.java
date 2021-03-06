/**
 * Created: 17.10.2017
 */

package de.freese.base.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.core.model.builder.Builder;

/**
 * Basisklasse eines PoolBuilders.
 *
 * @author Thomas Freese
 * @param <B> Typ des Builders
 * @param <T> Typ des Objektes
 */
public abstract class AbstractPoolBuilder<B, T> implements Builder<T>
{
    /**
     *
     */
    public static final int DEFAULT_CORE_SIZE = 1;

    /**
     *
     */
    public static final int DEFAULT_MAX_SIZE = 10;

    /**
     *
     */
    private int coreSize = -1;

    /**
     *
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private int maxSize = -1;

    /**
     * Milliseconds
     */
    private int maxWait = -1;

    /**
     *
     */
    private boolean registerShutdownHook;

    /**
     *
     */
    private boolean validateOnGet;

    /**
     *
     */
    private boolean validateOnReturn;

    /**
     * Default: 1
     *
     * @param coreSize int
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public B coreSize(final int coreSize)
    {
        this.coreSize = coreSize;

        if ((this.maxSize > 0) && (this.coreSize > this.maxSize))
        {
            maxSize(this.coreSize);
        }

        return (B) this;
    }

    /**
     * @return int
     */
    protected int getCoreSize()
    {
        return this.coreSize;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return int
     */
    protected int getMaxSize()
    {
        return this.maxSize;
    }

    /**
     * Milliseconds
     *
     * @return int
     */
    protected int getMaxWait()
    {
        return this.maxWait;
    }

    /**
     * @return boolean
     */
    protected boolean isRegisterShutdownHook()
    {
        return this.registerShutdownHook;
    }

    /**
     * @return boolean
     */
    protected boolean isValidateOnGet()
    {
        return this.validateOnGet;
    }

    /**
     * @return boolean
     */
    protected boolean isValidateOnReturn()
    {
        return this.validateOnReturn;
    }

    /**
     * Default: 10
     *
     * @param maxSize int
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public B maxSize(final int maxSize)
    {
        this.maxSize = maxSize;

        if ((this.maxSize > 0) && (this.maxSize < this.coreSize))
        {
            coreSize(this.maxSize);
        }

        return (B) this;
    }

    /**
     * Milliseconds
     *
     * @param maxWait int
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public B maxWait(final int maxWait)
    {
        this.maxWait = maxWait;

        return (B) this;
    }

    /**
     * @param register boolean
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public B registerShutdownHook(final boolean register)
    {
        this.registerShutdownHook = register;

        return (B) this;
    }

    /**
     * @param validateOnGet boolean
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public B validateOnGet(final boolean validateOnGet)
    {
        this.validateOnGet = validateOnGet;

        return (B) this;
    }

    /**
     * @param validateOnReturn boolean
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public B validateOnReturn(final boolean validateOnReturn)
    {
        this.validateOnReturn = validateOnReturn;

        return (B) this;
    }
}
