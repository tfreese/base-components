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
    public static final int DEFAULT_MAX = 10;

    /**
     *
     */
    public static final int DEFAULT_MIN = 1;

    /**
     *
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private int max = -1;

    /**
     * Milliseconds
     */
    private int maxWait = -1;

    /**
     *
     */
    private int min = -1;

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
     * @param max int
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public T max(final int max)
    {
        this.max = max;

        if ((this.max > 0) && (this.max < this.min))
        {
            min(this.max);
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
     * @param min int
     * @return {@link AbstractPoolBuilder}
     */
    @SuppressWarnings("unchecked")
    public T min(final int min)
    {
        this.min = min;

        if ((this.max > 0) && (this.min > this.max))
        {
            max(this.min);
        }

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
    protected int getMax()
    {
        return this.max;
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
     * @return int
     */
    protected int getMin()
    {
        return this.min;
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
}
