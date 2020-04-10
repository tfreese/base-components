/**
 * Created: 17.10.2017
 */
package de.freese.base.pool;

/**
 * Enum für die Implementierungen des {@link ObjectPool}.
 *
 * @author Thomas Freese
 */
public enum ObjectPoolType
{
    /**
     *
     */
    COMMONS,

    /**
     *
     */
    ERASOFT,

    /**
    *
    */
    ROUND_ROBIN,

    /**
     *
     */
    SIMPLE,

    /**
    *
    */
    UNBOUNDED;
}
