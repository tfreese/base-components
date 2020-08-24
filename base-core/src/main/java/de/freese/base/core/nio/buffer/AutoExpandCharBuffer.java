// Created: 03.11.2016
package de.freese.base.core.nio.buffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;

/**
 * Adapter für den {@link CharBuffer} mit AutoExpand-Funktion.<br>
 * Der carriage return line feed (crlf) wird automatisch bei jeder put-Methode angefügt.
 *
 * @author Thomas Freese
 */
public final class AutoExpandCharBuffer extends AbstractAutoExpandBuffer<CharBuffer>
{
    /**
     * Default: CRLF = "\r\n"
     *
     * @param capacity int
     * @return {@link AutoExpandCharBuffer}
     */
    public static AutoExpandCharBuffer of(final int capacity)
    {
        return of(capacity, "\r\n");
    }

    /**
     * @param capacity int
     * @param crlf String
     * @return {@link AutoExpandCharBuffer}
     */
    public static AutoExpandCharBuffer of(final int capacity, final String crlf)
    {
        CharBuffer charBuffer = CharBuffer.allocate(capacity);

        return new AutoExpandCharBuffer(charBuffer, crlf);
    }

    /**
     *
     */
    private final String crlf;

    /**
     * Erzeugt eine neue Instanz von {@link AutoExpandCharBuffer}.<br>
     *
     * <pre>
     * CharBuffer charBuffer = CharBuffer.allocate(capacity);
     * return new AutoExpandCharBuffer(charBuffer);
     * </pre>
     *
     * @param buffer {@link CharBuffer}
     * @param crlf String
     */
    private AutoExpandCharBuffer(final CharBuffer buffer, final String crlf)
    {
        super(buffer);

        this.crlf = crlf;
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

        appendCRLF();

        return this;
    }

    /**
     * @param format String
     * @param args Object[]
     * @return {@link AutoExpandCharBuffer}
     * @see String#format(String, Object...)
     */
    public AutoExpandCharBuffer putf(final String format, final Object...args)
    {
        String s = String.format(format, args);

        return put(s, 0, s.length());
    }

    /**
     * Fügt eine Leerzeile hinzu.<br>
     * Default: "\r\n"
     *
     * @return {@link AutoExpandCharBuffer}
     */
    public AutoExpandCharBuffer putln()
    {
        appendCRLF();

        return this;
    }

    /**
     * Fügt CRLF an, wenn dieser != null.
     */
    private void appendCRLF()
    {
        if (getCRLF() != null)
        {
            autoExpand(getCRLF().length());
            getBuffer().put(getCRLF());
        }
    }

    /**
     * carriage return line feed (NETASCII_EOL)
     *
     * @return String
     */
    private String getCRLF()
    {
        return this.crlf;
    }

    /**
     * @see de.freese.base.core.nio.buffer.AbstractAutoExpandBuffer#createNewBuffer(java.nio.Buffer, int, int)
     */
    @Override
    protected CharBuffer createNewBuffer(final CharBuffer buffer, final int newCapacity, final int mark)
    {
        if (newCapacity > buffer.capacity())
        {
            // Alten Zustand speichern.
            int pos = buffer.position();

            // // Reallocate.
            CharBuffer newBuffer = CharBuffer.allocate(newCapacity);

            buffer.flip();
            newBuffer.put(buffer);

            // Alten Zustand wiederherstellen.
            newBuffer.limit(newCapacity);

            if (mark >= 0)
            {
                newBuffer.position(mark);
                newBuffer.mark();
            }

            newBuffer.position(pos);

            return newBuffer;
        }

        return buffer;
    }
}
