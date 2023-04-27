// Created: 03.11.2016
package de.freese.base.core.nio.buffer;

import java.nio.Buffer;
import java.util.Objects;

/**
 * Adapter for the {@link Buffer} with AutoExpand-Function.
 *
 * @author Thomas Freese
 * @see "org.springframework.core.io.buffer.DataBuffer"
 */
public abstract class AbstractAutoExpandBuffer<B extends Buffer> {
    /**
     * Default: 4 MB
     */
    private static final int CALCULATE_THRESHOLD = 1024 * 1024 * 4;

    /**
     * See jdk.internal.util.ArraysSupport#MAX_ARRAY_LENGTH<br>
     * <br>
     * The maximum length of array to allocate (unless necessary).<br>
     * Some VMs reserve some header words in an array. Attempts to allocate larger<br>
     * arrays may result in {@code OutOfMemoryError: Requested array size exceeds VM limit}
     */
    private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

    /**
     * @see "io.netty.buffer.AbstractByteBufAllocator.calculateNewCapacity(int, int)"
     * @see "org.springframework.core.io.buffer.DefaultDataBuffer.calculateCapacity(int)"
     */
    public static int calculateNewCapacity(final int neededCapacity) {
        if (neededCapacity <= 0) {
            throw new IllegalArgumentException("neededCapacity must be > 0");
        }

        if (neededCapacity == CALCULATE_THRESHOLD) {
            return CALCULATE_THRESHOLD;
        }

        // Above the Threshold: do not duplicate the size, but increase by Threshold.
        if (neededCapacity > CALCULATE_THRESHOLD) {
            int newCapacity = (neededCapacity / CALCULATE_THRESHOLD) * CALCULATE_THRESHOLD;

            if (newCapacity > (MAX_CAPACITY - CALCULATE_THRESHOLD)) {
                newCapacity = MAX_CAPACITY;
            }
            else {
                newCapacity += CALCULATE_THRESHOLD;
            }

            return newCapacity;
        }

        // Not above the Threshold: increase to Threshold in "power of 2" Steps, started by 64.
        // << 1: Bit-Shift to left, increase by power of 2; 1,2,4,8,16,32,...
        // >> 1: Bit-Shift to right, decrease by power of 2; ...,32,16,8,4,2,1

        // Get the next higher Value (power of 2), which is smaller than neededCapacity.
        // int newCapacity = Integer.highestOneBit(neededCapacity);

        int newCapacity = 64;

        while (newCapacity < neededCapacity) {
            newCapacity <<= 1;
        }

        return Math.min(newCapacity, MAX_CAPACITY);
    }

    private B buffer;

    private int mark = -1;

    protected AbstractAutoExpandBuffer(final B buffer) {
        super();

        this.buffer = Objects.requireNonNull(buffer, "buffer required");
    }

    public final int capacity() {
        return getBuffer().capacity();
    }

    public final AbstractAutoExpandBuffer<B> clear() {
        getBuffer().clear();

        this.mark = -1;

        return this;
    }

    public final AbstractAutoExpandBuffer<B> flip() {
        getBuffer().flip();

        this.mark = -1;

        return this;
    }

    public final B getBuffer() {
        return this.buffer;
    }

    public final boolean hasRemaining() {
        return getBuffer().hasRemaining();
    }

    public final boolean isDirect() {
        return getBuffer().isDirect();
    }

    public final boolean isReadOnly() {
        return getBuffer().isReadOnly();
    }

    public final int limit() {
        return getBuffer().limit();
    }

    public final AbstractAutoExpandBuffer<B> limit(final int newLimit) {
        autoExpand(newLimit, 0);
        getBuffer().limit(newLimit);

        if (this.mark > newLimit) {
            this.mark = -1;
        }

        return this;
    }

    public final AbstractAutoExpandBuffer<B> mark() {
        getBuffer().mark();
        this.mark = position();

        return this;
    }

    public final int position() {
        return getBuffer().position();
    }

    public final AbstractAutoExpandBuffer<B> position(final int newPosition) {
        autoExpand(newPosition, 0);
        getBuffer().position(newPosition);

        if (this.mark > newPosition) {
            this.mark = -1;
        }

        return this;
    }

    public final int remaining() {
        return limit() - position();
    }

    public final AbstractAutoExpandBuffer<B> reset() {
        getBuffer().reset();

        return this;
    }

    /**
     * Forwards the position of this buffer as the specified <code>size</code> bytes.
     */
    public final AbstractAutoExpandBuffer<B> skip(final int size) {
        autoExpand(size);

        return position(position() + size);
    }

    protected void autoExpand(final int expectedRemaining) {
        autoExpand(position(), expectedRemaining);
    }

    protected void autoExpand(final int position, final int expectedRemaining) {
        int newLimit = position + expectedRemaining;

        if (newLimit > capacity()) {
            // Buffer muss erweitert werden.
            int newCapacity = calculateNewCapacity(newLimit);

            B newBuffer = createNewBuffer(getBuffer(), newCapacity);

            // Alten Zustand wiederherstellen.
            newBuffer.limit(newCapacity);

            if (this.mark >= 0) {
                newBuffer.position(this.mark);
                newBuffer.mark();
            }

            newBuffer.position(position);

            this.buffer = newBuffer;
        }

        if (newLimit > limit()) {
            // Set the Limit, to avoid StackOverflowError.
            this.buffer.limit(newLimit);
        }
    }

    protected abstract B createNewBuffer(B buffer, int newCapacity);

}
