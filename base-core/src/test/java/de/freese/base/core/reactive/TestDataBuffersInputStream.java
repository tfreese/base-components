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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * @see DataBufferUtils#subscriberInputStream(Publisher, int)
 */
class TestDataBuffersInputStream {
    @Test
    void testAvailableShouldReturn0IfNoBufferWereLoaded() throws IOException {
        final byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        final DataBufferFactory factory = new DefaultDataBufferFactory();
        final DataBuffer buffer = factory.wrap(data);

        try (InputStream is = new DataBuffersInputStream(Flux.just(buffer))) {
            assertEquals(0, is.available());
        }
    }

    @Test
    void testAvailableShouldReturnTheRemainingDataOnLastLoadedBuffer() throws IOException {
        final byte[] data = new byte[]{1, 2, 3, 4, 5, 6};
        final DataBufferFactory factory = new DefaultDataBufferFactory();
        final DataBuffer buffer = factory.wrap(data);

        try (var is = new DataBuffersInputStream(Flux.just(buffer))) {
            assertEquals(1, is.read());
            assertEquals(0, is.available());
        }
    }

    @Test
    void testCloseShouldReleaseAllBuffers() throws IOException {
        final DataBufferFactory factory = new DefaultDataBufferFactory();
        final Flux<DataBuffer> buffers = mock();

        doAnswer(args -> {
            final Subscriber<DataBuffer> subscriber = args.getArgument(0);
            subscriber.onSubscribe(mock(Subscription.class));
            subscriber.onNext(factory.wrap(new byte[]{1}));
            subscriber.onNext(factory.wrap(new byte[]{2}));
            subscriber.onNext(factory.wrap(new byte[]{3}));
            subscriber.onComplete();
            return null;
        })
                .when(buffers)
                .subscribe(Mockito.<Subscriber<DataBuffer>>any());

        try (MockedStatic<DataBufferUtils> dataBufferUtilsMock = mockStatic(DataBufferUtils.class)) {
            try (InputStream is = new DataBuffersInputStream(buffers)) {
                is.markSupported();
            }

            dataBufferUtilsMock.verify(() -> DataBufferUtils.release(any(DataBuffer.class)), times(3));
        }
    }

    @Test
    void testCloseShouldReleaseTakenAllBuffers() throws IOException {
        final DataBufferFactory factory = new DefaultDataBufferFactory();
        final DataBuffer b1 = factory.wrap(new byte[]{1});
        final DataBuffer b2 = factory.wrap(new byte[]{4, 5, 6});
        final DataBuffer b3 = factory.wrap(new byte[]{7, 8, 9});
        final Flux<DataBuffer> buffers = Flux.just(b1, b2, b3).subscribeOn(Schedulers.boundedElastic());

        try (MockedStatic<DataBufferUtils> dataBufferUtilsMock = mockStatic(DataBufferUtils.class)) {
            try (InputStream is = new DataBuffersInputStream(buffers)) {
                assertEquals(1, is.read());
                assertEquals(4, is.read());
            }

            dataBufferUtilsMock.verify(() -> DataBufferUtils.release(b1));
            dataBufferUtilsMock.verify(() -> DataBufferUtils.release(b2));
            dataBufferUtilsMock.verify(() -> DataBufferUtils.release(b3), times(0));
            dataBufferUtilsMock.verifyNoMoreInteractions();
        }
    }

    @Test
    void testEmptyStream() throws IOException {
        final DataBuffer b1 = mock(DataBuffer.class);
        final DataBuffer b2 = mock(DataBuffer.class);
        final Flux<DataBuffer> src = Flux.just(b1, b2);

        try (InputStream is = new DataBuffersInputStream(src)) {
            assertEquals(0, is.readAllBytes().length);
        }
    }

    @Test
    void testReadAfterCloseShouldThrowException() throws IOException {
        final DataBufferFactory factory = new DefaultDataBufferFactory();
        final DataBuffer b1 = factory.wrap(new byte[]{1, 2, 3, 4, 5, 6});

        final InputStream inputStream;

        try (InputStream is = new DataBuffersInputStream(Flux.just(b1))) {
            assertEquals(1, is.read());

            inputStream = is;
        }

        assertEquals(-1, inputStream.read());
    }

    @Test
    void testReadAllBytes() throws IOException {
        final DataBufferFactory factory = new DefaultDataBufferFactory();
        final DataBuffer b1 = factory.wrap(new byte[]{1, 2, -3});
        final DataBuffer b2 = factory.wrap(new byte[]{127, 126, -125});

        try (InputStream is = new DataBuffersInputStream(Flux.just(b1, b2))) {
            assertArrayEquals(new byte[]{1, 2, -3, 127, 126, -125}, is.readAllBytes());
        }
    }

    @Test
    void testReadAllBytesOneByOne() throws IOException {
        final DataBufferFactory factory = new DefaultDataBufferFactory();
        final DataBuffer b1 = factory.wrap(new byte[]{-1, -2});
        final DataBuffer b2 = factory.wrap(new byte[]{-128, 5});

        try (InputStream is = new DataBuffersInputStream(Flux.just(b1, b2))) {
            assertEquals(255, is.read());
            assertEquals(-2, (byte) is.read());
            assertEquals(-128, (byte) is.read());
            assertEquals(5, is.read());
            assertEquals(-1, is.read());
        }
    }

    @Test
    void testReadAllBytesWithEmptyBufferInTheMiddle() throws IOException {
        final DataBufferFactory factory = new DefaultDataBufferFactory();
        final DataBuffer b1 = factory.wrap(new byte[]{1, 2, -3});
        final DataBuffer b2 = factory.wrap(new byte[]{});
        final DataBuffer b3 = factory.wrap(new byte[]{127, 126, -125});

        try (InputStream is = new DataBuffersInputStream(Flux.just(b1, b2, b3))) {
            assertArrayEquals(new byte[]{1, 2, -3, 127, 126, -125}, is.readAllBytes());
        }
    }

    @Test
    void testSkip() throws IOException {
        final DataBufferFactory factory = new DefaultDataBufferFactory();
        final DataBuffer b1 = factory.wrap(new byte[]{1, 2, 3});
        final DataBuffer b2 = factory.wrap(new byte[]{4, 5, 6});

        try (InputStream is = new DataBuffersInputStream(Flux.just(b1, b2))) {
            long skipped = is.skip(4);
            assertEquals(3, skipped);

            assertArrayEquals(new byte[]{4, 5, 6}, is.readAllBytes());

            skipped = is.skip(1);
            assertEquals(0, skipped);
        }
    }

    @Test
    void testStreamErrorsShouldBePropagatedThroughIOExceptions() throws IOException {
        final RuntimeException exception = new RuntimeException("Test");
        final Flux<DataBuffer> src = Flux.error(exception);

        try (InputStream is = new DataBuffersInputStream(src)) {
            final IOException result = assertThrows(IOException.class, is::read);
            assertEquals(exception, result.getCause());
        }
    }
}
