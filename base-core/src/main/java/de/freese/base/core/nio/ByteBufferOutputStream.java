package de.freese.base.core.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

/**
 * @author Thomas Freese
 * @since 16.09.2020
 */
public class ByteBufferOutputStream extends OutputStream {
    private final ByteBuffer buffer;

    public ByteBufferOutputStream(final ByteBuffer buffer) {
        super();

        this.buffer = Objects.requireNonNull(buffer, "buffer required");
    }

    @Override
    public void write(final byte @NonNull [] b, final int off, final int len) throws IOException {
        buffer.put(b, off, len);
    }

    @Override
    public void write(final int b) throws IOException {
        buffer.put((byte) b);
    }
}
