/**
 * Created: 17.10.2017
 */

package de.freese.base.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisklasse eines PoolBuilders.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ des Builders
 */
public abstract class AbstractPoolBuilder<T extends AbstractPoolBuilder<?>>
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
    private boolean registerShutdownHook = false;

    /**
     *
     */
    private boolean validateOnGet = false;

    /**
     *
     */
    private boolean validateOnReturn = false;

    /**
     * Erstellt ein neues {@link AbstractPoolBuilder} Object.
     */
    public AbstractPoolBuilder()
    {
        super();
    }

    /**
     * @param coreSize int
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public T coreSize(final int coreSize)
    {
        this.coreSize = coreSize;

        if ((this.maxSize > 0) && (this.coreSize > this.maxSize))
        {
            maxSize(this.coreSize);
        }

        return (T) this;
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
     * @param maxSize int
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public T maxSize(final int maxSize)
    {
        this.maxSize = maxSize;

        if ((this.maxSize > 0) && (this.maxSize < this.coreSize))
        {
            coreSize(this.maxSize);
        }

        return (T) this;
    }

    /**
     * Milliseconds
     *
     * @param maxWait int
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public T maxWait(final int maxWait)
    {
        this.maxWait = maxWait;

        return (T) this;
    }

    /**
     * @param register boolean
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public T registerShutdownHook(final boolean register)
    {
        this.registerShutdownHook = register;

        return (T) this;
    }

    /**
     * @param validateOnGet boolean
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public T validateOnGet(final boolean validateOnGet)
    {
        this.validateOnGet = validateOnGet;

        return (T) this;
    }

    /**
     * @param validateOnReturn boolean
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public T validateOnReturn(final boolean validateOnReturn)
    {
        this.validateOnReturn = validateOnReturn;

        return (T) this;
    }
}
