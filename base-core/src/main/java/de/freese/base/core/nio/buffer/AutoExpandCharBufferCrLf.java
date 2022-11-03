// Created: 03.11.2016
package de.freese.base.core.nio.buffer;

import java.nio.CharBuffer;
import java.util.Objects;

/**
 * Adapter für den {@link CharBuffer} mit AutoExpand-Funktion.<br>
 * Der carriage return line feed (crlf) wird automatisch bei jeder put-Methode angefügt.
 *
 * @author Thomas Freese
 * @see "org.springframework.core.io.buffer.DataBuffer"
 */
public final class AutoExpandCharBufferCrLf extends AutoExpandCharBuffer
{
    /**
     * Default: CRLF = "\r\n"
     */
    public static AutoExpandCharBufferCrLf of(final int capacity)
    {
        return of(capacity, "\r\n");
    }

    public static AutoExpandCharBufferCrLf of(final int capacity, final String crlf)
    {
        CharBuffer charBuffer = CharBuffer.allocate(capacity);

        return new AutoExpandCharBufferCrLf(charBuffer, crlf);
    }

    private final String crlf;

    /**
     * <pre>
     * CharBuffer charBuffer = CharBuffer.allocate(capacity);
     * return new AutoExpandCharBuffer(charBuffer);
     * </pre>
     */
    private AutoExpandCharBufferCrLf(final CharBuffer buffer, final String crlf)
    {
        super(buffer);

        this.crlf = Objects.requireNonNull(crlf, "crlf required");
    }

    /**
     * Fügt eine Leerzeile hinzu.<br>
     * Default: "\r\n"
     */
    public AutoExpandCharBufferCrLf putLn()
    {
        appendCrlf();

        return this;
    }

    public AutoExpandCharBufferCrLf putLn(final char c)
    {
        super.put(c);

        appendCrlf();

        return this;
    }

    public AutoExpandCharBufferCrLf putLn(final CharSequence src)
    {
        super.put(src);

        appendCrlf();

        return this;
    }

    public AutoExpandCharBufferCrLf putLn(final CharSequence src, final int start, final int end)
    {
        super.put(src, start, end);

        appendCrlf();

        return this;
    }

    /**
     * @see AbstractAutoExpandBuffer#createNewBuffer(java.nio.Buffer, int)
     */
    @Override
    protected CharBuffer createNewBuffer(final CharBuffer buffer, final int newCapacity)
    {
        CharBuffer newBuffer = CharBuffer.allocate(newCapacity);

        buffer.flip();
        newBuffer.put(buffer);

        return newBuffer;
    }

    /**
     * Fügt CRLF an, wenn dieser != null.
     */
    private void appendCrlf()
    {
        autoExpand(getCrlf().length());
        getBuffer().put(getCrlf());
    }

    /**
     * carriage return line feed (NETASCII_EOL)
     */
    private String getCrlf()
    {
        return this.crlf;
    }
}
