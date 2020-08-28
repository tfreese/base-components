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
     * Siehe jdk.internal.util.ArraysSupport#MAX_ARRAY_LENGTH<br>
     * <br>
     * The maximum length of array to allocate (unless necessary).<br>
     * Some VMs reserve some header words in an array. Attempts to allocate larger<br>
     * arrays may result in {@code OutOfMemoryError: Requested array size exceeds VM limit}
     */
    private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

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
            throw new IllegalArgumentException("requestedCapacity must be > 0");
        }

        // ArraysSupport.MAX_ARRAY_LENGTH
        if (requestedCapacity > MAX_ARRAY_LENGTH)
        {
            throw new OutOfMemoryError("Required array length too large");
        }

        // Liefert den höchsten Wert (power of 2), der kleiner als requestedCapacity ist.
        int newCapacity = Integer.highestOneBit(requestedCapacity);

        // << 1: Bit-Shift nach links, vergrößert um power of 2; 1,2,4,8,16,32,...
        // >> 1: Bit-Shift nach rechts, verkleinert um power of 2; ...,32,16,8,4,2,
        if (newCapacity < requestedCapacity)
        {
            newCapacity <<= 1;
        }

        if (newCapacity > MAX_ARRAY_LENGTH)
        {
            throw new OutOfMemoryError("Required array length too large");
        }

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

        this.buffer = Objects.requireNonNull(buffer, "buffer required");
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
     * @param position int
     * @param expectedRemaining int
     */
    protected void autoExpand(final int position, final int expectedRemaining)
    {
        int newLimit = position + expectedRemaining;

        if (newLimit > capacity())
        {
            // Buffer muss erweitert werden.
            int newCapacity = normalizeCapacity(newLimit);

            B newBuffer = createNewBuffer(getBuffer(), newCapacity);

            // Alten Zustand wiederherstellen.
            newBuffer.limit(newCapacity);

            if (this.mark >= 0)
            {
                newBuffer.position(this.mark);
                newBuffer.mark();
            }

            newBuffer.position(position);

            this.buffer = newBuffer;
        }

        if (newLimit > limit())
        {
            // Limit setzen, um StackOverflowError zu vermeiden.
            this.buffer.limit(newLimit);
        }
    }

    /**
     * @param buffer {@link Buffer}
     * @param newCapacity int
     * @return {@link Buffer}
     */
    protected abstract B createNewBuffer(final B buffer, final int newCapacity);

}
