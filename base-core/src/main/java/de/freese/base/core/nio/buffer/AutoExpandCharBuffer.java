// Created: 03.11.2016
package de.freese.base.core.nio.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;

/**
 * Adapter for the {@link Buffer} with AutoExpand-Function.
 *
 * @author Thomas Freese
 */
public class AutoExpandCharBuffer extends AbstractAutoExpandBuffer<CharBuffer> {
    public static AutoExpandCharBuffer of(final int capacity) {
        final CharBuffer charBuffer = CharBuffer.allocate(capacity);

        return new AutoExpandCharBuffer(charBuffer);
    }

    /**
     * <pre>
     * CharBuffer charBuffer = CharBuffer.allocate(capacity);
     * return new AutoExpandCharBuffer(charBuffer);
     * </pre>
     */
    AutoExpandCharBuffer(final CharBuffer buffer) {
        super(buffer);
    }

    public ByteBuffer encode(final CharsetEncoder encoder) throws CharacterCodingException {
        return encoder.reset().encode(getBuffer());
    }

    public char get() {
        return getBuffer().get();
    }

    public AutoExpandCharBuffer get(final char[] dst) {
        getBuffer().get(dst);

        return this;
    }

    public AutoExpandCharBuffer get(final char[] dst, final int offset, final int length) {
        getBuffer().get(dst, offset, length);

        return this;
    }

    public char get(final int index) {
        return getBuffer().get(index);
    }

    public AutoExpandCharBuffer get(final int index, final char[] dst) {
        getBuffer().get(index, dst);

        return this;
    }

    public String getString(final int index, final int length) {
        final char[] dst = new char[length];

        get(index, dst);

        return String.valueOf(dst);
    }

    public AutoExpandCharBuffer put(final char c) {
        autoExpand(1);

        getBuffer().put(c);

        return this;
    }

    public AutoExpandCharBuffer put(final CharSequence src) {
        return put(src, 0, src.length());
    }

    public AutoExpandCharBuffer put(final CharSequence src, final int start, final int end) {
        autoExpand(end - start);

        getBuffer().put(src.toString(), start, end);

        return this;
    }

    @Override
    protected CharBuffer createNewBuffer(final CharBuffer buffer, final int newCapacity) {
        final CharBuffer newBuffer = CharBuffer.allocate(newCapacity);

        buffer.flip();
        newBuffer.put(buffer);

        return newBuffer;
    }
}
