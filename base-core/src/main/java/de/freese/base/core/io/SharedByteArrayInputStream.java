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

        startIndex = offset;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(buf, 0, count);
    }

    public InputStream toStream(final long start, final long end) {
        if (start < 0) {
            throw new IllegalArgumentException("start < 0");
        }

        long to = end;

        if (to == -1) {
            to = count - (long) startIndex;
        }

        return new SharedByteArrayInputStream(buf, startIndex + (int) start, (int) (to - start));
    }
}
