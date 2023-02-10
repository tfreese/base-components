// Created: 16.09.2020
package de.freese.base.core.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class ByteBufferOutputStream extends OutputStream {
    private final ByteBuffer buffer;

    public ByteBufferOutputStream(final ByteBuffer buffer) {
        super();

        this.buffer = Objects.requireNonNull(buffer, "buffer required");
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.buffer.put(b, off, len);
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException {
        this.buffer.put((byte) b);
    }
}
