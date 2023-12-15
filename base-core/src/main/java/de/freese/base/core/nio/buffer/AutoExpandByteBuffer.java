// Created: 03.11.2016
package de.freese.base.core.nio.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;

/**
 * Adapter for the {@link Buffer} with AutoExpand-Function.
 *
 * @author Thomas Freese
 * @see "org.springframework.core.io.buffer.DataBuffer"
 */
public final class AutoExpandByteBuffer extends AbstractAutoExpandBuffer<ByteBuffer> {
    /**
     * Default:<br>
     * - direct = true<br>
     * - crlf = [0x0D, 0x0A]
     */
    public static AutoExpandByteBuffer of(final int capacity) {
        return of(capacity, true);
    }

    public static AutoExpandByteBuffer of(final int capacity, final boolean direct) {
        final ByteBuffer byteBuffer = direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);

        return new AutoExpandByteBuffer(byteBuffer);
    }

    /**
     * <pre>
     * ByteBuffer byteBuffer = idDirect ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
     * return new AutoExpandByteBuffer(byteBuffer);
     * </pre>
     */
    private AutoExpandByteBuffer(final ByteBuffer buffer) {
        super(buffer);
    }

    public InputStream asInputStream() {
        return new InputStream() {
            @Override
            public int available() throws IOException {
                return AutoExpandByteBuffer.this.remaining();
            }

            @Override
            public synchronized void mark(final int readLimit) {
                AutoExpandByteBuffer.this.mark();
            }

            @Override
            public boolean markSupported() {
                return true;
            }

            @Override
            public int read() throws IOException {
                if (AutoExpandByteBuffer.this.hasRemaining()) {
                    return AutoExpandByteBuffer.this.get() & 0xff;
                }

                return -1;
            }

            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException {
                final int remaining = AutoExpandByteBuffer.this.remaining();

                if (remaining > 0) {
                    final int readBytes = Math.min(remaining, len);
                    AutoExpandByteBuffer.this.get(b, off, readBytes);

                    return readBytes;
                }

                return -1;
            }

            @Override
            public synchronized void reset() throws IOException {
                AutoExpandByteBuffer.this.reset();
            }

            @Override
            public long skip(final long n) throws IOException {
                final int bytes;

                if (n > Integer.MAX_VALUE) {
                    bytes = AutoExpandByteBuffer.this.remaining();
                }
                else {
                    bytes = Math.min(AutoExpandByteBuffer.this.remaining(), (int) n);
                }

                AutoExpandByteBuffer.this.skip(bytes);

                return bytes;
            }
        };
    }

    public OutputStream asOutputStream() {
        return new OutputStream() {
            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException {
                AutoExpandByteBuffer.this.put(b, off, len);
            }

            @Override
            public void write(final int b) throws IOException {
                AutoExpandByteBuffer.this.put((byte) b);
            }
        };
    }

    public CharBuffer decode(final CharsetDecoder decoder) throws CharacterCodingException {
        return decoder.reset().decode(getBuffer());
    }

    public byte get() {
        return getBuffer().get();
    }

    public void get(final byte[] dst) {
        getBuffer().get(dst);
    }

    public void get(final byte[] dst, final int offset, final int length) {
        getBuffer().get(dst, offset, length);
    }

    public byte get(final int index) {
        return getBuffer().get(index);
    }

    public char getChar() {
        return getBuffer().getChar();
    }

    public char getChar(final int index) {
        return getBuffer().getChar(index);
    }

    public double getDouble() {
        return getBuffer().getDouble();
    }

    public double getDouble(final int index) {
        return getBuffer().getDouble(index);
    }

    public float getFloat() {
        return getBuffer().getFloat();
    }

    public float getFloat(final int index) {
        return getBuffer().getFloat(index);
    }

    public String getHexDump() {
        return getHexDump(Integer.MAX_VALUE);
    }

    public String getHexDump(final int lengthLimit) {
        if (lengthLimit == 0) {
            throw new IllegalArgumentException("lengthLimit: " + lengthLimit + " (expected: 1+)");
        }

        final ByteBuffer buffer = getBuffer();

        final boolean truncate = buffer.remaining() > lengthLimit;
        int size = 0;

        if (truncate) {
            size = lengthLimit;
        }
        else {
            size = buffer.remaining();
        }

        if (size == 0) {
            return "empty";
        }

        final int position = buffer.position();

        final char[] hexCode = "0123456789ABCDEF".toCharArray();

        final StringBuilder sb = new StringBuilder(size * 2);

        for (; size > 0; size--) {
            final int byteValue = buffer.get() & 0xFF;

            sb.append(hexCode[byteValue >> 4]);
            sb.append(hexCode[byteValue & 0xF]);

            // sb.append(Integer.toString((buffer.get() & 0xFF) + 0x100, 16).substring(1));
            // DatatypeConverter.printHexBinary(checksum);
            // String hex = String.format("%02x", buffer.get());

            // String hex = Integer.toHexString(byteValue);
            // if (hex.length() == 1) {
            // sb.append('0');
            // }
            // sb.append(hex);
        }

        buffer.position(position);

        if (truncate) {
            sb.append("...");
        }

        return sb.toString();
    }

    public int getInt() {
        return getBuffer().getInt();
    }

    public int getInt(final int index) {
        return getBuffer().getInt(index);
    }

    public long getLong() {
        return getBuffer().getLong();
    }

    public long getLong(final int index) {
        return getBuffer().getLong(index);
    }

    public short getShort() {
        return getBuffer().getShort();
    }

    public short getShort(final int index) {
        return getBuffer().getShort(index);
    }

    public AutoExpandByteBuffer put(final byte b) {
        autoExpand(1);

        getBuffer().put(b);

        return this;
    }

    public AutoExpandByteBuffer put(final byte[] src) {
        return put(src, 0, src.length);
    }

    public AutoExpandByteBuffer put(final byte[] src, final int offset, final int length) {
        autoExpand(length - offset);

        getBuffer().put(src, offset, length);

        return this;
    }

    public AutoExpandByteBuffer put(final ByteBuffer src) {
        autoExpand(src.remaining());

        getBuffer().put(src);

        return this;
    }

    public AutoExpandByteBuffer putChar(final char value) {
        autoExpand(2);

        getBuffer().putChar(value);

        return this;
    }

    public AutoExpandByteBuffer putDouble(final double value) {
        autoExpand(8);

        getBuffer().putDouble(value);

        return this;
    }

    public AutoExpandByteBuffer putFloat(final float value) {
        autoExpand(4);

        getBuffer().putFloat(value);

        return this;
    }

    public AutoExpandByteBuffer putInt(final int value) {
        autoExpand(4);

        getBuffer().putInt(value);

        return this;
    }

    public AutoExpandByteBuffer putLong(final long value) {
        autoExpand(8);

        getBuffer().putLong(value);

        return this;
    }

    public AutoExpandByteBuffer putShort(final short value) {
        autoExpand(2);

        getBuffer().putShort(value);

        return this;
    }

    @Override
    protected ByteBuffer createNewBuffer(final ByteBuffer buffer, final int newCapacity) {
        final ByteOrder bo = buffer.order();

        final ByteBuffer newBuffer = buffer.isDirect() ? ByteBuffer.allocateDirect(newCapacity) : ByteBuffer.allocate(newCapacity);

        buffer.flip();
        newBuffer.put(buffer);

        newBuffer.order(bo);

        return newBuffer;
    }
}
