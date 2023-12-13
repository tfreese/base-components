// Created: 28.08.2020
package de.freese.base.core.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Thomas Freese
 */
public class SharedByteArrayInputStream extends ByteArrayInputStream {
    private int startIndex;

    public SharedByteArrayInputStream(final byte[] buf) {
        super(buf);
    }

    public SharedByteArrayInputStream(final byte[] buf, final int offset, final int length) {
        super(buf, offset, length);

        this.startIndex = offset;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(this.buf, 0, this.count);
    }

    public InputStream toStream(final long start, final long end) {
        if (start < 0) {
            throw new IllegalArgumentException("start < 0");
        }

        long to = end;

        if (to == -1) {
            to = this.count - (long) this.startIndex;
        }

        return new SharedByteArrayInputStream(this.buf, this.startIndex + (int) start, (int) (to - start));
    }
}
