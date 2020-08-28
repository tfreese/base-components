// Created: 03.11.2016
package de.freese.base.core.nio.buffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;

/**
 * Adapter für den {@link CharBuffer} mit AutoExpand-Funktion.<br>
 *
 * @author Thomas Freese
 */
public class AutoExpandCharBuffer extends AbstractAutoExpandBuffer<CharBuffer>
{
    /**
     * @param capacity int
     * @return {@link AutoExpandCharBuffer}
     */
    public static AutoExpandCharBuffer of(final int capacity)
    {
        CharBuffer charBuffer = CharBuffer.allocate(capacity);

        return new AutoExpandCharBuffer(charBuffer);
    }

    /**
     * Erzeugt eine neue Instanz von {@link AutoExpandCharBuffer}.<br>
     *
     * <pre>
     * CharBuffer charBuffer = CharBuffer.allocate(capacity);
     * return new AutoExpandCharBuffer(charBuffer);
     * </pre>
     *
     * @param buffer {@link CharBuffer}
     */
    AutoExpandCharBuffer(final CharBuffer buffer)
    {
        super(buffer);
    }

    /**
     * @param encoder {@link CharsetEncoder}
     * @return {@link ByteBuffer}
     * @throws CharacterCodingException Falls was schief geht.
     */
    public ByteBuffer encode(final CharsetEncoder encoder) throws CharacterCodingException
    {
        return encoder.reset().encode(getBuffer());
    }

    /**
     * @return char
     * @see CharBuffer#get()
     */
    public char get()
    {
        return getBuffer().get();
    }

    /**
     * @param dst char[]
     * @return {@link AutoExpandCharBuffer}
     * @see CharBuffer#get(char[])
     */
    public AutoExpandCharBuffer get(final char[] dst)
    {
        getBuffer().get(dst);

        return this;
    }

    /**
     * @param dst char[]
     * @param offset int
     * @param length int
     * @return {@link AutoExpandCharBuffer}
     * @see CharBuffer#get(char[], int, int)
     */
    public AutoExpandCharBuffer get(final char[] dst, final int offset, final int length)
    {
        getBuffer().get(dst, offset, length);

        return this;
    }

    /**
     * @param index int
     * @return char
     * @see CharBuffer#get(int)
     */
    public char get(final int index)
    {
        return getBuffer().get(index);
    }

    /**
     * @param index int
     * @param dst char[]
     * @return {@link AutoExpandCharBuffer}
     * @see CharBuffer#get(int, char[])
     */
    public AutoExpandCharBuffer get(final int index, final char[] dst)
    {
        getBuffer().get(index, dst);

        return this;
    }

    /**
     * @param index int
     * @param length int
     * @return String
     */
    public String getString(final int index, final int length)
    {
        char[] dst = new char[length];

        get(index, dst);

        return String.valueOf(dst);
    }

    /**
     * @param c char
     * @return {@link AutoExpandCharBuffer}
     * @see CharBuffer#put(char)
     */
    public AutoExpandCharBuffer put(final char c)
    {
        autoExpand(1);

        getBuffer().put(c);

        return this;
    }

    /**
     * @param src {@link CharSequence}
     * @return {@link AutoExpandCharBuffer}
     * @see CharBuffer#put(String)
     */
    public AutoExpandCharBuffer put(final CharSequence src)
    {
        return put(src, 0, src.length());
    }

    /**
     * @param src {@link CharSequence}
     * @param start int
     * @param end int
     * @return {@link AutoExpandCharBuffer}
     * @see CharBuffer#put(String, int, int)
     */
    public AutoExpandCharBuffer put(final CharSequence src, final int start, final int end)
    {
        autoExpand(end - start);

        getBuffer().put(src.toString(), start, end);

        return this;
    }

    /**
     * @see de.freese.base.core.nio.buffer.AbstractAutoExpandBuffer#createNewBuffer(java.nio.Buffer, int)
     */
    @Override
    protected CharBuffer createNewBuffer(final CharBuffer buffer, final int newCapacity)
    {
        CharBuffer newBuffer = CharBuffer.allocate(newCapacity);

        buffer.flip();
        newBuffer.put(buffer);

        return newBuffer;
    }
}
