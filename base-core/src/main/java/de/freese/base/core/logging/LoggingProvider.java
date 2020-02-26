/**
 * Created: 26.02.2020
 */

package de.freese.base.core.logging;

import org.slf4j.Logger;

/**
 * @author Thomas Freese
 */
public interface LoggingProvider
{
    /**
     * @return {@link Logger}
     */
    public Logger getLogger();
}
