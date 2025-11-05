/**
 * MIT License
 *
 * Copyright (c) 2024 Jerônimo Nunes Rocha
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.freese.base.core.reactive;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;

/**
 * An {@link InputStream} wrapper for Spring Boot Flux of {@link DataBuffer}.
 *
 * @author Jerônimo Nunes Rocha
 * @see DataBufferUtils#subscriberInputStream(Publisher, int)
 */
public class DataBuffersInputStream extends InputStream {

    private static final class BufferSource implements Subscriber<DataBuffer>, AutoCloseable {
        private final Queue<DataBuffer> buffers = new LinkedList<>();

        private Throwable error;
        private boolean received;
        private Subscription subscription;

        @Override
        public synchronized void close() {
            if (subscription != null) {
                subscription.cancel();
                subscription = null;
            }

            DataBuffer buffer;

            while ((buffer = buffers.poll()) != null) {
                DataBufferUtils.release(buffer);
            }

            notifyAll();
        }

        @Override
        public synchronized void onComplete() {
            subscription = null;

            notifyAll();
        }

        @Override
        public synchronized void onError(final Throwable t) {
            error = t;
            subscription = null;

            notifyAll();
        }

        @Override
        public synchronized void onNext(final DataBuffer buffer) {
            if (subscription == null || buffer.readableByteCount() == 0) {
                DataBufferUtils.release(buffer);
            }
            else {
                buffers.add(buffer);
            }

            received = true;

            notifyAll();
        }

        @Override
        public synchronized void onSubscribe(final Subscription s) {
            subscription = s;
        }

        private synchronized DataBuffer take() throws Throwable {
            while (true) {
                final DataBuffer buffer = buffers.poll();

                if (buffer != null) {
                    return buffer;
                }

                if (error != null) {
                    throw error;
                }

                if (subscription == null) {
                    return null;
                }

                received = false;
                subscription.request(1);

                if (!received) {
                    wait();
                }
            }
        }
    }

    private DataBuffer currentBuffer;
    private BufferSource source = new BufferSource();

    public DataBuffersInputStream(final Publisher<DataBuffer> publisher) {
        super();

        publisher.subscribe(source);
    }

    @Override
    public void close() {
        if (source != null) {
            source.close();
            source = null;
        }

        if (currentBuffer != null) {
            DataBufferUtils.release(currentBuffer);
            currentBuffer = null;
        }
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final DataBuffer buffer = getCurrentBuffer();

        if (buffer == null) {
            return -1;
        }

        final int newLen = Math.min(len, buffer.readableByteCount());
        buffer.read(b, off, newLen);

        return newLen;
    }

    @Override
    public int read() throws IOException {
        final DataBuffer buffer = getCurrentBuffer();

        if (buffer == null) {
            return -1;
        }

        return buffer.read() & 0xFF;
    }

    @Override
    public long skip(final long n) throws IOException {
        final var buffer = getCurrentBuffer();

        if (buffer == null) {
            return 0;
        }

        final var skipped = (int) Math.min(n, buffer.readableByteCount());
        buffer.readPosition(buffer.readPosition() + skipped);

        return skipped;
    }

    private DataBuffer getCurrentBuffer() throws IOException {
        if (source == null) {
            return null;
        }

        if (currentBuffer != null) {
            if (currentBuffer.readableByteCount() > 0) {
                return currentBuffer;
            }
            else {
                DataBufferUtils.release(currentBuffer);
            }
        }

        try {
            currentBuffer = source.take();

            return currentBuffer;
        }
        catch (Throwable ex) {
            throw new IOException(ex);
        }
    }
}
