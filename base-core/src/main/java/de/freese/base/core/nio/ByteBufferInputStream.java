// Created: 15.09.2020
package de.freese.base.core.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class ByteBufferInputStream extends InputStream {
    private final ByteBuffer buffer;

    public ByteBufferInputStream(final ByteBuffer buffer) {
        super();

        this.buffer = Objects.requireNonNull(buffer, "buffer required");
    }

    @Override
    public int available() throws IOException {
        return buffer.remaining();
    }

    @Override
    public synchronized void mark(final int readLimit) {
        buffer.mark();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        if (buffer.hasRemaining()) {
            return buffer.get() & 0xff;
        }

        return -1;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int remaining = buffer.remaining();

        if (remaining > 0) {
            final int readBytes = Math.min(remaining, len);
            buffer.get(b, off, readBytes);

            return readBytes;
        }

        return -1;
    }

    @Override
    public synchronized void reset() throws IOException {
        buffer.reset();
    }

    @Override
    public long skip(final long n) {
        final int bytes;

        if (n > Integer.MAX_VALUE) {
            bytes = buffer.remaining();
        }
        else {
            bytes = Math.min(buffer.remaining(), (int) n);
        }

        buffer.position(buffer.position() + bytes);

        return bytes;
    }
}
