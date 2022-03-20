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
     *
     * @param capacity int
     *
     * @return {@link AutoExpandCharBufferCrLf}
     */
    public static AutoExpandCharBufferCrLf of(final int capacity)
    {
        return of(capacity, "\r\n");
    }

    /**
     * @param capacity int
     * @param crlf String
     *
     * @return {@link AutoExpandCharBufferCrLf}
     */
    public static AutoExpandCharBufferCrLf of(final int capacity, final String crlf)
    {
        CharBuffer charBuffer = CharBuffer.allocate(capacity);

        return new AutoExpandCharBufferCrLf(charBuffer, crlf);
    }

    /**
     *
     */
    private final String crlf;

    /**
     * Erzeugt eine neue Instanz von {@link AutoExpandCharBufferCrLf}.<br>
     *
     * <pre>
     * CharBuffer charBuffer = CharBuffer.allocate(capacity);
     * return new AutoExpandCharBuffer(charBuffer);
     * </pre>
     *
     * @param buffer {@link CharBuffer}
     * @param crlf String
     */
    private AutoExpandCharBufferCrLf(final CharBuffer buffer, final String crlf)
    {
        super(buffer);

        this.crlf = Objects.requireNonNull(crlf, "crlf required");
    }

    /**
     * Fügt eine Leerzeile hinzu.<br>
     * Default: "\r\n"
     *
     * @return {@link AutoExpandCharBufferCrLf}
     */
    public AutoExpandCharBufferCrLf putLn()
    {
        appendCrlf();

        return this;
    }

    /**
     * @param c char
     *
     * @return {@link AutoExpandCharBufferCrLf}
     *
     * @see AutoExpandCharBuffer#put(char)
     */
    public AutoExpandCharBufferCrLf putLn(final char c)
    {
        super.put(c);

        appendCrlf();

        return this;
    }

    /**
     * @param src {@link CharSequence}
     *
     * @return {@link AutoExpandCharBufferCrLf}
     *
     * @see AutoExpandCharBuffer#put(CharSequence)
     */
    public AutoExpandCharBufferCrLf putLn(final CharSequence src)
    {
        super.put(src);

        appendCrlf();

        return this;
    }

    /**
     * @param src {@link CharSequence}
     * @param start int
     * @param end int
     *
     * @return {@link AutoExpandCharBufferCrLf}
     *
     * @see AutoExpandCharBuffer#put(CharSequence, int, int)
     */
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
     *
     * @return String
     */
    private String getCrlf()
    {
        return this.crlf;
    }
}
