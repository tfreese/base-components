// Created: 03.11.2016
package de.freese.base.core.nio.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;

/**
 * Adapter für den {@link ByteBuffer} mit AutoExpand-Funktion.<br>
 *
 * @author Thomas Freese
 * @see "org.springframework.core.io.buffer.DataBuffer"
 */
public final class AutoExpandByteBuffer extends AbstractAutoExpandBuffer<ByteBuffer>
{
    /**
     * Default:<br>
     * - direct = true<br>
     * - crlf = [0x0D, 0x0A]
     *
     * @param capacity int
     *
     * @return {@link AutoExpandByteBuffer}
     */
    public static AutoExpandByteBuffer of(final int capacity)
    {
        return of(capacity, true);
    }

    /**
     * @param capacity int
     * @param direct boolean
     *
     * @return {@link AutoExpandByteBuffer}
     */
    public static AutoExpandByteBuffer of(final int capacity, final boolean direct)
    {
        ByteBuffer byteBuffer = direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);

        return new AutoExpandByteBuffer(byteBuffer);
    }

    /**
     * Erzeugt eine neue Instanz von {@link AutoExpandByteBuffer}.<br>
     *
     * <pre>
     * ByteBuffer byteBuffer = idDirect ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
     * return new AutoExpandByteBuffer(byteBuffer);
     * </pre>
     *
     * @param buffer {@link ByteBuffer}
     */
    private AutoExpandByteBuffer(final ByteBuffer buffer)
    {
        super(buffer);
    }

    /**
     * Liefert einen {@link InputStream}, der aus dem Buffer liest.<br>
     * {@link InputStream#read()} liefert <tt>-1</tt>, wenn der Buffer sein Limit erreicht.
     *
     * @return {@link InputStream}
     */
    public InputStream asInputStream()
    {
        return new InputStream()
        {
            /**
             * @see java.io.InputStream#available()
             */
            @Override
            public int available() throws IOException
            {
                return AutoExpandByteBuffer.this.remaining();
            }

            /**
             * @see java.io.InputStream#mark(int)
             */
            @Override
            public synchronized void mark(final int readLimit)
            {
                AutoExpandByteBuffer.this.mark();
            }

            /**
             * @see java.io.InputStream#markSupported()
             */
            @Override
            public boolean markSupported()
            {
                return true;
            }

            /**
             * @see java.io.InputStream#read()
             */
            @Override
            public int read() throws IOException
            {
                if (AutoExpandByteBuffer.this.hasRemaining())
                {
                    return AutoExpandByteBuffer.this.get() & 0xff;
                }

                return -1;
            }

            /**
             * @see java.io.InputStream#read(byte[], int, int)
             */
            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException
            {
                int remaining = AutoExpandByteBuffer.this.remaining();

                if (remaining > 0)
                {
                    int readBytes = Math.min(remaining, len);
                    AutoExpandByteBuffer.this.get(b, off, readBytes);

                    return readBytes;
                }

                return -1;
            }

            /**
             * @see java.io.InputStream#reset()
             */
            @Override
            public synchronized void reset() throws IOException
            {
                AutoExpandByteBuffer.this.reset();
            }

            /**
             * @see java.io.InputStream#skip(long)
             */
            @Override
            public long skip(final long n) throws IOException
            {
                int bytes;

                if (n > Integer.MAX_VALUE)
                {
                    bytes = AutoExpandByteBuffer.this.remaining();
                }
                else
                {
                    bytes = Math.min(AutoExpandByteBuffer.this.remaining(), (int) n);
                }

                AutoExpandByteBuffer.this.skip(bytes);

                return bytes;
            }
        };
    }

    /**
     * Liefert einen {@link OutputStream}, der in den Buffer schreibt.<br>
     *
     * @return {@link OutputStream}
     */
    public OutputStream asOutputStream()
    {
        return new OutputStream()
        {
            /**
             * @see java.io.OutputStream#write(byte[], int, int)
             */
            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException
            {
                AutoExpandByteBuffer.this.put(b, off, len);
            }

            /**
             * @see java.io.OutputStream#write(int)
             */
            @Override
            public void write(final int b) throws IOException
            {
                AutoExpandByteBuffer.this.put((byte) b);
            }
        };
    }

    /**
     * @param decoder {@link CharsetDecoder}
     *
     * @return {@link CharBuffer}
     *
     * @throws CharacterCodingException Falls was schiefgeht.
     */
    public CharBuffer decode(final CharsetDecoder decoder) throws CharacterCodingException
    {
        return decoder.reset().decode(getBuffer());
    }

    /**
     * @return byte
     *
     * @see ByteBuffer#get()
     */
    public byte get()
    {
        return getBuffer().get();
    }

    /**
     * @param dst byte[]
     *
     * @see ByteBuffer#get(byte[])
     */
    public void get(final byte[] dst)
    {
        getBuffer().get(dst);
    }

    /**
     * @param dst byte[]
     * @param offset int
     * @param length int
     *
     * @see ByteBuffer#get(byte[], int, int)
     */
    public void get(final byte[] dst, final int offset, final int length)
    {
        getBuffer().get(dst, offset, length);
    }

    /**
     * @param index int
     *
     * @return byte
     *
     * @see ByteBuffer#get(int)
     */
    public byte get(final int index)
    {
        return getBuffer().get(index);
    }

    /**
     * @return char
     *
     * @see ByteBuffer#getChar()
     */
    public char getChar()
    {
        return getBuffer().getChar();
    }

    /**
     * @param index int
     *
     * @return char
     *
     * @see ByteBuffer#getChar(int)
     */
    public char getChar(final int index)
    {
        return getBuffer().getChar(index);
    }

    /**
     * @return double
     *
     * @see ByteBuffer#getDouble()
     */
    public double getDouble()
    {
        return getBuffer().getDouble();
    }

    /**
     * @param index int
     *
     * @return double
     *
     * @see ByteBuffer#getDouble(int)
     */
    public double getDouble(final int index)
    {
        return getBuffer().getDouble(index);
    }

    /**
     * @return float
     *
     * @see ByteBuffer#getFloat()
     */
    public float getFloat()
    {
        return getBuffer().getFloat();
    }

    /**
     * @param index int
     *
     * @return float
     *
     * @see ByteBuffer#getFloat(int)
     */
    public float getFloat(final int index)
    {
        return getBuffer().getFloat(index);
    }

    /**
     * Liefert die Hexadezimal Darstellung des {@link ByteBuffer}.
     *
     * @return String
     */
    public String getHexDump()
    {
        return getHexDump(Integer.MAX_VALUE);
    }

    /**
     * Liefert die Hexadezimal Darstellung des {@link ByteBuffer}.
     *
     * @param lengthLimit int
     *
     * @return String
     */
    public String getHexDump(final int lengthLimit)
    {
        if (lengthLimit == 0)
        {
            throw new IllegalArgumentException("lengthLimit: " + lengthLimit + " (expected: 1+)");
        }

        ByteBuffer buffer = getBuffer();

        boolean truncate = buffer.remaining() > lengthLimit;
        int size = 0;

        if (truncate)
        {
            size = lengthLimit;
        }
        else
        {
            size = buffer.remaining();
        }

        if (size == 0)
        {
            return "empty";
        }

        int position = buffer.position();

        char[] hexCode = "0123456789ABCDEF".toCharArray();

        StringBuilder sb = new StringBuilder(size * 2);

        for (; size > 0; size--)
        {
            int byteValue = buffer.get() & 0xFF;

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

        if (truncate)
        {
            sb.append("...");
        }

        return sb.toString();
    }

    /**
     * @return int
     *
     * @see ByteBuffer#getInt()
     */
    public int getInt()
    {
        return getBuffer().getInt();
    }

    /**
     * @param index int
     *
     * @return int
     *
     * @see ByteBuffer#getInt(int)
     */
    public int getInt(final int index)
    {
        return getBuffer().getInt(index);
    }

    /**
     * @return long
     *
     * @see ByteBuffer#getLong()
     */
    public long getLong()
    {
        return getBuffer().getLong();
    }

    /**
     * @param index int
     *
     * @return long
     *
     * @see ByteBuffer#getLong(int)
     */
    public long getLong(final int index)
    {
        return getBuffer().getLong(index);
    }

    /**
     * @return short
     *
     * @see ByteBuffer#getShort()
     */
    public short getShort()
    {
        return getBuffer().getShort();
    }

    /**
     * @param index int
     *
     * @return short
     *
     * @see ByteBuffer#getShort(int)
     */
    public short getShort(final int index)
    {
        return getBuffer().getShort(index);
    }

    /**
     * @param b byte
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#put(byte)
     */
    public AutoExpandByteBuffer put(final byte b)
    {
        autoExpand(1);

        getBuffer().put(b);

        return this;
    }

    /**
     * @param src byte[]
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#put(byte[])
     */
    public AutoExpandByteBuffer put(final byte[] src)
    {
        return put(src, 0, src.length);
    }

    /**
     * @param src byte[]
     * @param offset int
     * @param length int
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#put(byte[], int, int)
     */
    public AutoExpandByteBuffer put(final byte[] src, final int offset, final int length)
    {
        autoExpand(length - offset);

        getBuffer().put(src, offset, length);

        return this;
    }

    /**
     * @param src {@link ByteBuffer}
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#put(ByteBuffer)
     */
    public AutoExpandByteBuffer put(final ByteBuffer src)
    {
        autoExpand(src.remaining());

        getBuffer().put(src);

        return this;
    }

    /**
     * @param value char
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#putChar(char)
     */
    public AutoExpandByteBuffer putChar(final char value)
    {
        autoExpand(2);

        getBuffer().putChar(value);

        return this;
    }

    /**
     * @param value double
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#putDouble(double)
     */
    public AutoExpandByteBuffer putDouble(final double value)
    {
        autoExpand(8);

        getBuffer().putDouble(value);

        return this;
    }

    /**
     * @param value float
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#putFloat(float)
     */
    public AutoExpandByteBuffer putFloat(final float value)
    {
        autoExpand(4);

        getBuffer().putFloat(value);

        return this;
    }

    /**
     * @param value int
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#putInt(int)
     */
    public AutoExpandByteBuffer putInt(final int value)
    {
        autoExpand(4);

        getBuffer().putInt(value);

        return this;
    }

    /**
     * @param value long
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#putLong(long)
     */
    public AutoExpandByteBuffer putLong(final long value)
    {
        autoExpand(8);

        getBuffer().putLong(value);

        return this;
    }

    /**
     * @param value short
     *
     * @return {@link AutoExpandByteBuffer}
     *
     * @see ByteBuffer#putShort(short)
     */
    public AutoExpandByteBuffer putShort(final short value)
    {
        autoExpand(2);

        getBuffer().putShort(value);

        return this;
    }

    /**
     * @see de.freese.base.core.nio.buffer.AbstractAutoExpandBuffer#createNewBuffer(java.nio.Buffer, int)
     */
    @Override
    protected ByteBuffer createNewBuffer(final ByteBuffer buffer, final int newCapacity)
    {
        ByteOrder bo = buffer.order();

        ByteBuffer newBuffer = buffer.isDirect() ? ByteBuffer.allocateDirect(newCapacity) : ByteBuffer.allocate(newCapacity);

        buffer.flip();
        newBuffer.put(buffer);

        newBuffer.order(bo);

        return newBuffer;
    }
}
