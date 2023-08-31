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
        return this.buffer.remaining();
    }

    @Override
    public synchronized void mark(final int readLimit) {
        this.buffer.mark();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        if (this.buffer.hasRemaining()) {
            return this.buffer.get() & 0xff;
        }

        return -1;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int remaining = this.buffer.remaining();

        if (remaining > 0) {
            int readBytes = Math.min(remaining, len);
            this.buffer.get(b, off, readBytes);

            return readBytes;
        }

        return -1;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.buffer.reset();
    }

    @Override
    public long skip(final long n) throws IOException {
        int bytes;

        if (n > Integer.MAX_VALUE) {
            bytes = this.buffer.remaining();
        }
        else {
            bytes = Math.min(this.buffer.remaining(), (int) n);
        }

        this.buffer.position(this.buffer.position() + bytes);

        return bytes;
    }
}
