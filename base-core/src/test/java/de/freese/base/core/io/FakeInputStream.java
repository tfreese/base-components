package de.freese.base.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Thomas Freese
 */
public class FakeInputStream extends InputStream {
    private final int size;

    private volatile boolean closed;

    private int position;

    public FakeInputStream(final int size) {
        super();

        this.size = size;
    }

    @Override
    public int available() {
        return size - position;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public int read() throws IOException {
        ensureOpen();

        if (available() > 0) {
            position++;

            return 1;
        }

        return -1;
    }

    private void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }
    }
}
