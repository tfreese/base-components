// Created: 03.11.2016
package de.freese.base.core.nio.buffer;

import java.nio.Buffer;
import java.util.Objects;

/**
 * Adapter für den {@link Buffer} mit AutoExpand-Funktion.
 *
 * @param <B> Konkreter Buffer
 * @author Thomas Freese
 */
public abstract class AbstractAutoExpandBuffer<B extends Buffer>
{
    /**
     * Liefert den nächst größen Wert, der ein Vielfaches von "power of 2" ist.<br>
     * Ist der neue Wert = 0, wird 1024 geliefert.
     *
     * @param requestedCapacity int
     * @return int
     */
    public static int normalizeCapacity(final int requestedCapacity)
    {
        if (requestedCapacity <= 0)
        {
            return 1024;
        }

        int newCapacity = Integer.highestOneBit(requestedCapacity);
        newCapacity <<= (newCapacity < requestedCapacity ? 1 : 0);

        // return newCapacity < 0 ? Integer.MAX_VALUE : newCapacity;
        return newCapacity;
    }

    /**
     *
     */
    private B buffer;

    /**
     * Eigene Variable, da kein direkter Zugriff auf Buffer.markValue().
     */
    private int mark = -1;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractAutoExpandBuffer}
     *
     * @param buffer {@link Buffer}
     */
    protected AbstractAutoExpandBuffer(final B buffer)
    {
        super();

        setBuffer(buffer);
    }

    /**
     * @return int
     * @see Buffer#capacity()
     */
    public final int capacity()
    {
        return getBuffer().capacity();
    }

    /**
     * @return {@link AbstractAutoExpandBuffer}
     * @see Buffer#clear()
     */
    public final AbstractAutoExpandBuffer<B> clear()
    {
        getBuffer().clear();

        this.mark = -1;

        return this;
    }

    /**
     * @return {@link AbstractAutoExpandBuffer}
     * @see Buffer#flip()
     */
    public final AbstractAutoExpandBuffer<B> flip()
    {
        getBuffer().flip();

        this.mark = -1;

        return this;
    }

    /**
     * @return {@link Buffer}
     */
    public final B getBuffer()
    {
        return this.buffer;
    }

    /**
     * @return boolean
     * @see Buffer#hasRemaining()
     */
    public final boolean hasRemaining()
    {
        return getBuffer().hasRemaining();
    }

    /**
     * @return boolean
     * @see Buffer#isDirect()
     */
    public final boolean isDirect()
    {
        return getBuffer().isDirect();
    }

    /**
     * @return boolean
     * @see Buffer#isReadOnly()
     */
    public final boolean isReadOnly()
    {
        return getBuffer().isReadOnly();
    }

    /**
     * @return int
     * @see Buffer#limit()
     */
    public final int limit()
    {
        return getBuffer().limit();
    }

    /**
     * @param newLimit int
     * @return {@link AbstractAutoExpandBuffer}
     * @see Buffer#limit(int)
     */
    public final AbstractAutoExpandBuffer<B> limit(final int newLimit)
    {
        autoExpand(newLimit, 0);
        getBuffer().limit(newLimit);

        if (this.mark > newLimit)
        {
            this.mark = -1;
        }

        return this;
    }

    /**
     * @return {@link AbstractAutoExpandBuffer}
     * @see Buffer#mark()
     */
    public final AbstractAutoExpandBuffer<B> mark()
    {
        getBuffer().mark();
        this.mark = position();

        return this;
    }

    /**
     * @return int
     * @see Buffer#position()
     */
    public final int position()
    {
        return getBuffer().position();
    }

    /**
     * @param newPosition int
     * @return {@link AbstractAutoExpandBuffer}
     * @see Buffer#position(int)
     */
    public final AbstractAutoExpandBuffer<B> position(final int newPosition)
    {
        autoExpand(newPosition, 0);
        getBuffer().position(newPosition);

        if (this.mark > newPosition)
        {
            this.mark = -1;
        }

        return this;
    }

    /**
     * @return int
     * @see Buffer#remaining()
     */
    public final int remaining()
    {
        return limit() - position();
    }

    /**
     * @return {@link AbstractAutoExpandBuffer}
     * @see Buffer#reset()
     */
    public final AbstractAutoExpandBuffer<B> reset()
    {
        getBuffer().reset();

        return this;
    }

    /**
     * Forwards the position of this buffer as the specified <code>size</code> bytes.
     *
     * @param size int
     * @return {@link AbstractAutoExpandBuffer}
     */
    public final AbstractAutoExpandBuffer<B> skip(final int size)
    {
        autoExpand(size);

        return position(position() + size);
    }

    /**
     * Erweitert den Buffer soweit, wenn nötig, um die angegebene Größe aufnehmen zu können.
     *
     * @param expectedRemaining int
     */
    protected void autoExpand(final int expectedRemaining)
    {
        autoExpand(position(), expectedRemaining);
    }

    /**
     * Erweitert den Buffer soweit, wenn nötig, um die angegebene Größe aufnehmen zu können.
     *
     * @param pos int
     * @param expectedRemaining int
     */
    protected void autoExpand(final int pos, final int expectedRemaining)
    {
        // TODO Optimierung
        int end = pos + expectedRemaining;
        int newCapacity = normalizeCapacity(end);

        if (newCapacity > capacity())
        {
            // Buffer muss erweitert werden.
            setBuffer(createNewBuffer(getBuffer(), newCapacity, this.mark));
        }

        if (end > limit())
        {
            // Limit setzen, um StackOverflowError zu vermeiden.
            getBuffer().limit(end);
        }
    }

    /**
     * @param buffer {@link Buffer}
     * @param newCapacity int
     * @param mark int
     * @return {@link Buffer}
     */
    protected abstract B createNewBuffer(final B buffer, final int newCapacity, final int mark);

    /**
     * @param buffer {@link Buffer}
     */
    protected void setBuffer(final B buffer)
    {
        this.buffer = Objects.requireNonNull(buffer, "buffer required");
    }
}
