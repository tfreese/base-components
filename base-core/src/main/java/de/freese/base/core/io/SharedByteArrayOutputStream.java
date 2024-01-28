// Created: 28.08.2020
package de.freese.base.core.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * ByteArrayOutputStream mit direktem Zugriff auf das ByteArray über einen {@link ByteBuffer} ohne es zu kopieren.
 *
 * @author Thomas Freese
 */
public class SharedByteArrayOutputStream extends ByteArrayOutputStream {
    public SharedByteArrayOutputStream() {
        super();
    }

    public SharedByteArrayOutputStream(final int size) {
        super(size);
    }

    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(this.buf, 0, this.count);
    }

    public InputStream toStream() {
        return new SharedByteArrayInputStream(this.buf, 0, this.count);
    }

    public void write(final ByteBuffer buffer) {
        write(buffer, buffer.remaining());
    }

    public void write(final ByteBuffer buffer, final int length) {
        final byte[] data = new byte[length];

        buffer.get(data);

        writeBytes(data);
    }
}
