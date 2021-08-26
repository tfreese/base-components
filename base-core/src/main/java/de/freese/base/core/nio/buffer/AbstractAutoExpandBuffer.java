// Created: 03.11.2016
package de.freese.base.core.nio.buffer;

import java.nio.Buffer;
import java.util.Objects;

/**
 * Adapter für den {@link Buffer} mit AutoExpand-Funktion.
 *
 * @param <B> Konkreter Buffer
 *
 * @author Thomas Freese
 *
 * @see "org.springframework.core.io.buffer.DataBuffer"
 */
public abstract class AbstractAutoExpandBuffer<B extends Buffer>
{
    /**
     * Default: 4 MB
     */
    private static final int CALCULATE_THRESHOLD = 1024 * 1024 * 4;

    /**
     * Siehe jdk.internal.util.ArraysSupport#MAX_ARRAY_LENGTH<br>
     * <br>
     * The maximum length of array to allocate (unless necessary).<br>
     * Some VMs reserve some header words in an array. Attempts to allocate larger<br>
     * arrays may result in {@code OutOfMemoryError: Requested array size exceeds VM limit}
     */
    private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

    /**
     * Berechnet die neue Größe des Buffers..<br>
     *
     * @param neededCapacity int
     *
     * @return int
     *
     * @see "io.netty.buffer.AbstractByteBufAllocator.calculateNewCapacity(int, int)"
     * @see "org.springframework.core.io.buffer.DefaultDataBuffer.calculateCapacity(int)"
     */
    public static int calculateNewCapacity(final int neededCapacity)
    {
        if (neededCapacity <= 0)
        {
            throw new IllegalArgumentException("neededCapacity must be > 0");
        }

        if (neededCapacity == CALCULATE_THRESHOLD)
        {
            return CALCULATE_THRESHOLD;
        }

        // Über dem Schwellenwert: die neue Größe nicht einfach verdoppeln, sondern um Schwellenwert vergrößern.
        if (neededCapacity > CALCULATE_THRESHOLD)
        {
            int newCapacity = (neededCapacity / CALCULATE_THRESHOLD) * CALCULATE_THRESHOLD;

            if (newCapacity > (MAX_CAPACITY - CALCULATE_THRESHOLD))
            {
                newCapacity = MAX_CAPACITY;
            }
            else
            {
                newCapacity += CALCULATE_THRESHOLD;
            }

            return newCapacity;
        }

        // Nicht über dem Schwellenwert: bis auf Schwellenwert vergrößern in "power of 2" Schritten, angefangen bei 64.
        // << 1: Bit-Shift nach links, vergrößert um power of 2; 1,2,4,8,16,32,...
        // >> 1: Bit-Shift nach rechts, verkleinert um power of 2; ...,32,16,8,4,2,1

        // Liefert den höchsten Wert (power of 2), der kleiner als neededCapacity ist.
        // int newCapacity = Integer.highestOneBit(neededCapacity);

        int newCapacity = 64;

        while (newCapacity < neededCapacity)
        {
            newCapacity <<= 1;
        }

        return Math.min(newCapacity, MAX_CAPACITY);
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
            int newCapacity = calculateNewCapacity(newLimit);

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
     * @return int
     *
     * @see Buffer#capacity()
     */
    public final int capacity()
    {
        return getBuffer().capacity();
    }

    /**
     * @return {@link AbstractAutoExpandBuffer}
     *
     * @see Buffer#clear()
     */
    public final AbstractAutoExpandBuffer<B> clear()
    {
        getBuffer().clear();

        this.mark = -1;

        return this;
    }

    /**
     * @param buffer {@link Buffer}
     * @param newCapacity int
     *
     * @return {@link Buffer}
     */
    protected abstract B createNewBuffer(final B buffer, final int newCapacity);

    /**
     * @return {@link AbstractAutoExpandBuffer}
     *
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
     *
     * @see Buffer#hasRemaining()
     */
    public final boolean hasRemaining()
    {
        return getBuffer().hasRemaining();
    }

    /**
     * @return boolean
     *
     * @see Buffer#isDirect()
     */
    public final boolean isDirect()
    {
        return getBuffer().isDirect();
    }

    /**
     * @return boolean
     *
     * @see Buffer#isReadOnly()
     */
    public final boolean isReadOnly()
    {
        return getBuffer().isReadOnly();
    }

    /**
     * @return int
     *
     * @see Buffer#limit()
     */
    public final int limit()
    {
        return getBuffer().limit();
    }

    /**
     * @param newLimit int
     *
     * @return {@link AbstractAutoExpandBuffer}
     *
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
     *
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
     *
     * @see Buffer#position()
     */
    public final int position()
    {
        return getBuffer().position();
    }

    /**
     * @param newPosition int
     *
     * @return {@link AbstractAutoExpandBuffer}
     *
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
     *
     * @see Buffer#remaining()
     */
    public final int remaining()
    {
        return limit() - position();
    }

    /**
     * @return {@link AbstractAutoExpandBuffer}
     *
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
     *
     * @return {@link AbstractAutoExpandBuffer}
     */
    public final AbstractAutoExpandBuffer<B> skip(final int size)
    {
        autoExpand(size);

        return position(position() + size);
    }

}
