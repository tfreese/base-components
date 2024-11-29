// Created: 01.07.2020
package de.freese.base.core.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * @author Thomas Freese
 */
public final class LoggingOutputStream extends OutputStream {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
    private final Level level;
    private final Logger logger;

    public LoggingOutputStream(final Logger logger, final Level level) {
        super();

        this.logger = Objects.requireNonNull(logger, "logger required");
        this.level = Objects.requireNonNull(level, "level required");
    }

    @Override
    public void close() throws IOException {
        if (baos.size() > 0) {
            logLine();
        }
    }

    @Override
    public void flush() throws IOException {
        logLine();
    }

    @Override
    public void write(final byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);

        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }

    @Override
    public void write(final int b) throws IOException {
        if (b == '\n') {
            logLine();
        }
        else {
            baos.write(b);
        }
    }

    private void logLine() {
        final String line = baos.toString(StandardCharsets.UTF_8);
        baos.reset();

        if (line.isBlank()) {
            return;
        }

        switch (level) {
            case TRACE -> logger.trace(line);
            case DEBUG -> logger.debug(line);
            case ERROR -> logger.error(line);
            case INFO -> logger.info(line);
            case WARN -> logger.warn(line);
            default -> throw new UnsupportedOperationException("Level not supported: " + level);
        }
    }
}
