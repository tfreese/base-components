/**
 * Created: 01.07.2020
 */

package de.freese.base.core.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * Schreibt jede Zeile die mit '\n' endet in den Logger.
 *
 * @author Thomas Freese
 */
public class LoggingOutputStream extends OutputStream
{
    /**
     *
     */
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

    /**
     *
     */
    private final Level level;

    /**
     *
     */
    private final Logger logger;

    /**
     * Erstellt ein neues {@link LoggingOutputStream} Object.
     *
     * @param logger {@link Logger}
     * @param level {@link Level}
     */
    public LoggingOutputStream(final Logger logger, final Level level)
    {
        super();

        this.logger = Objects.requireNonNull(logger, "logger requried");
        this.level = Objects.requireNonNull(level, "level requried");
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException
    {
        if (b == '\n')
        {
            String line = this.baos.toString(StandardCharsets.UTF_8);
            this.baos.reset();

            switch (this.level)
            {
                case TRACE:
                    this.logger.trace(line);
                    break;
                case DEBUG:
                    this.logger.debug(line);
                    break;
                case ERROR:
                    this.logger.error(line);
                    break;
                case INFO:
                    this.logger.info(line);
                    break;
                case WARN:
                    this.logger.warn(line);
                    break;
                default:
                    throw new UnsupportedOperationException("Level not supported:" + this.level);
            }
        }
        else
        {
            this.baos.write(b);
        }
    }
}
