/*
 * Copyright 2015 Corey Baswell Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package de.freese.base.security.ssl.nio;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper around a real {@link SocketChannel} that adds SSL support.
 */
public class SSLSocketChannel extends SocketChannel
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SSLSocketChannel.class);
    /**
     *
     */
    private final SocketChannel socketChannel;
    /**
     *
     */
    private final SSLEngineBuffer sslEngineBuffer;

    /**
     * @param socketChannel The real SocketChannel.
     * @param sslEngine The SSL engine to use for traffic back and forth on the given SocketChannel.
     * @param executor Used to execute long running, blocking SSL operations such as certificate validation with a CA
     *            (<a href="http://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLEngineResult.HandshakeStatus.html#NEED_TASK">NEED_TASK</a>)
     */
    public SSLSocketChannel(final SocketChannel socketChannel, final SSLEngine sslEngine, final Executor executor)
    {
        super(socketChannel.provider());

        this.socketChannel = Objects.requireNonNull(socketChannel, "socketChannel required");

        this.sslEngineBuffer = new SSLEngineBuffer(socketChannel, sslEngine, executor);
    }

    /**
     * @see java.nio.channels.SocketChannel#bind(java.net.SocketAddress)
     */
    @Override
    public SocketChannel bind(final SocketAddress local) throws IOException
    {
        this.socketChannel.bind(local);

        return this;
    }

    /**
     * @see java.nio.channels.SocketChannel#connect(java.net.SocketAddress)
     */
    @Override
    public boolean connect(final SocketAddress socketAddress) throws IOException
    {
        return this.socketChannel.connect(socketAddress);
    }

    /**
     * @see java.nio.channels.SocketChannel#finishConnect()
     */
    @Override
    public boolean finishConnect() throws IOException
    {
        return this.socketChannel.finishConnect();
    }

    /**
     * @see java.nio.channels.SocketChannel#getLocalAddress()
     */
    @Override
    public SocketAddress getLocalAddress() throws IOException
    {
        return this.socketChannel.getLocalAddress();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return SSLSocketChannel.LOGGER;
    }

    /**
     * @see java.nio.channels.NetworkChannel#getOption(java.net.SocketOption)
     */
    @Override
    public <T> T getOption(final SocketOption<T> name) throws IOException
    {
        return this.socketChannel.getOption(name);
    }

    /**
     * @see java.nio.channels.SocketChannel#getRemoteAddress()
     */
    @Override
    public SocketAddress getRemoteAddress() throws IOException
    {
        return this.socketChannel.getRemoteAddress();
    }

    /**
     * @return {@link SocketChannel}
     */
    public SocketChannel getWrappedSocketChannel()
    {
        return this.socketChannel;
    }

    /**
     * @see java.nio.channels.spi.AbstractSelectableChannel#implCloseSelectableChannel()
     */
    @Override
    protected void implCloseSelectableChannel() throws IOException
    {
        try
        {
            this.sslEngineBuffer.flushNetworkOutbound();
        }
        catch (Exception ex)
        {
            // Empty
        }

        this.socketChannel.close();
        this.sslEngineBuffer.close();
    }

    /**
     * @see java.nio.channels.spi.AbstractSelectableChannel#implConfigureBlocking(boolean)
     */
    @Override
    protected void implConfigureBlocking(final boolean b) throws IOException
    {
        this.socketChannel.configureBlocking(b);
    }

    /**
     * @see java.nio.channels.SocketChannel#isConnected()
     */
    @Override
    public boolean isConnected()
    {
        return this.socketChannel.isConnected();
    }

    /**
     * @see java.nio.channels.SocketChannel#isConnectionPending()
     */
    @Override
    public boolean isConnectionPending()
    {
        return this.socketChannel.isConnectionPending();
    }

    /**
     * <p>
     * Reads a sequence of bytes from this channel into the given buffer.
     * </p>
     * <p>
     * An attempt is made to read up to r bytes from the channel, where r is the number of bytes remaining in the buffer, that is, dst.remaining(), at the
     * moment this method is invoked.
     * </p>
     * <p>
     * Suppose that a byte sequence of length n is read, where 0 <= n <= r. This byte sequence will be transferred into the buffer so that the first byte in the
     * sequence is at index p and the last byte is at index p + n - 1, where p is the buffer's position at the moment this method is invoked. Upon return the
     * buffer's position will be equal to p + n; its limit will not have changed.
     * </p>
     * <p>
     * A read operation might not fill the buffer, and in fact it might not read any bytes at all. Whether or not it does so depends upon the nature and state
     * of the channel. A socket channel in non-blocking mode, for example, cannot read any more bytes than are immediately available from the socket's input
     * buffer; similarly, a file channel cannot read any more bytes than remain in the file. It is guaranteed, however, that if a channel is in blocking mode
     * and there is at least one byte remaining in the buffer then this method will block until at least one byte is read.</
     * <p>
     * This method may be invoked at any time. If another thread has already initiated a read operation upon this channel, however, then an invocation of this
     * method will block until the first operation is complete.
     * </p>
     *
     * @param applicationBuffer The buffer into which bytes are to be transferred
     *
     * @return The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream
     *
     * @throws java.nio.channels.NotYetConnectedException If this channel is not yet connected
     * @throws java.nio.channels.ClosedChannelException If this channel is closed
     * @throws java.nio.channels.AsynchronousCloseException If another thread closes this channel while the read operation is in progress
     * @throws java.nio.channels.ClosedByInterruptException If another thread interrupts the current thread while the read operation is in progress, thereby
     *             closing the channel and setting the current thread's interrupt status
     * @throws IOException If some other I/O error occurs
     * @throws IllegalArgumentException If the given applicationBuffer capacity ({@link ByteBuffer#capacity()} is less then the application buffer size of the
     *             {@link SSLEngine} session application buffer size ({@link SSLSession#getApplicationBufferSize()} this channel was constructed was.
     */
    @Override
    public synchronized int read(final ByteBuffer applicationBuffer) throws IOException, IllegalArgumentException
    {
        getLogger().debug("read: {} {}", applicationBuffer.position(), applicationBuffer.limit());

        int intialPosition = applicationBuffer.position();
        int readFromChannel = this.sslEngineBuffer.unwrap(applicationBuffer);

        getLogger().debug("read: from channel: {}", readFromChannel);

        if (readFromChannel < 0)
        {
            getLogger().debug("read: channel closed.");

            return readFromChannel;
        }

        int totalRead = applicationBuffer.position() - intialPosition;

        getLogger().debug("read: total read: {}", totalRead);

        return totalRead;
    }

    /**
     * <p>
     * Reads a sequence of bytes from this channel into a subsequence of the given buffers.
     * </p>
     * <p>
     * An invocation of this method attempts to read up to r bytes from this channel, where r is the total number of bytes remaining the specified subsequence
     * of the given buffer array, that is,
     *
     * <pre>
     * {@code
     * dsts[offset].remaining()
     *   + dsts[offset+1].remaining()
     *   + ... + dsts[offset+length-1].remaining()
     * }
     * </pre>
     * <p>
     * at the moment that this method is invoked.
     * </p>
     * <p>
     * Suppose that a byte sequence of length n is read, where 0 <= n <= r. Up to the first dsts[offset].remaining() bytes of this sequence are transferred into
     * buffer dsts[offset], up to the next dsts[offset+1].remaining() bytes are transferred into buffer dsts[offset+1], and so forth, until the entire byte
     * sequence is transferred into the given buffers. As many bytes as possible are transferred into each buffer, hence the final position of each updated
     * buffer, except the last updated buffer, is guaranteed to be equal to that buffer's limit.
     * </p>
     * <p>
     * This method may be invoked at any time. If another thread has already initiated a read operation upon this channel, however, then an invocation of this
     * method will block until the first operation is complete.
     * </p>
     *
     * @param applicationByteBuffers The buffers into which bytes are to be transferred
     * @param offset The offset within the buffer array of the first buffer into which bytes are to be transferred; must be non-negative and no larger than
     *            dsts.length
     * @param length The maximum number of buffers to be accessed; must be non-negative and no larger than <code>dsts.length - offset</code>
     *
     * @return The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream
     *
     * @throws java.nio.channels.NotYetConnectedException If this channel is not yet connected
     * @throws java.nio.channels.ClosedChannelException If this channel is closed
     * @throws java.nio.channels.AsynchronousCloseException If another thread closes this channel while the read operation is in progress
     * @throws java.nio.channels.ClosedByInterruptException If another thread interrupts the current thread while the read operation is in progress, thereby
     *             closing the channel and setting the current thread's interrupt status
     * @throws IOException If some other I/O error occurs
     * @throws IllegalArgumentException If one of the given applicationBuffers capacity ({@link ByteBuffer#capacity()} is less then the application buffer size
     *             of the {@link SSLEngine} session application buffer size ({@link SSLSession#getApplicationBufferSize()} this channel was constructed was.
     */
    @Override
    public long read(final ByteBuffer[] applicationByteBuffers, final int offset, final int length) throws IOException, IllegalArgumentException
    {
        long totalRead = 0;

        for (int i = offset; i < length; i++)
        {
            ByteBuffer applicationByteBuffer = applicationByteBuffers[i];

            if (applicationByteBuffer.hasRemaining())
            {
                int read = read(applicationByteBuffer);

                if (read > 0)
                {
                    totalRead += read;

                    if (applicationByteBuffer.hasRemaining())
                    {
                        break;
                    }
                }
                else
                {
                    if ((read < 0) && (totalRead == 0))
                    {
                        totalRead = -1;
                    }

                    break;
                }
            }
        }

        return totalRead;
    }

    /**
     * @see java.nio.channels.SocketChannel#setOption(java.net.SocketOption, java.lang.Object)
     */
    @Override
    public <T> SocketChannel setOption(final SocketOption<T> name, final T value) throws IOException
    {
        return this.socketChannel.setOption(name, value);
    }

    /**
     * @see java.nio.channels.SocketChannel#shutdownInput()
     */
    @Override
    public SocketChannel shutdownInput() throws IOException
    {
        return this.socketChannel.shutdownInput();
    }

    /**
     * @see java.nio.channels.SocketChannel#shutdownOutput()
     */
    @Override
    public SocketChannel shutdownOutput() throws IOException
    {
        return this.socketChannel.shutdownOutput();
    }

    /**
     * @see java.nio.channels.SocketChannel#socket()
     */
    @Override
    public Socket socket()
    {
        return this.socketChannel.socket();
    }

    /**
     * @see java.nio.channels.NetworkChannel#supportedOptions()
     */
    @Override
    public Set<SocketOption<?>> supportedOptions()
    {
        return this.socketChannel.supportedOptions();
    }

    /**
     * <p>
     * Writes a sequence of bytes to this channel from the given buffer.
     * </p>
     * <p>
     * An attempt is made to write up to r bytes to the channel, where r is the number of bytes remaining in the buffer, that is, src.remaining(), at the moment
     * this method is invoked.
     * </p>
     * <p>
     * Suppose that a byte sequence of length n is written, where 0 <= n <= r. This byte sequence will be transferred from the buffer starting at index p, where
     * p is the buffer's position at the moment this method is invoked; the index of the last byte written will be p + n - 1. Upon return the buffer's position
     * will be equal to p + n; its limit will not have changed.
     * </p>
     * <p>
     * Unless otherwise specified, a write operation will return only after writing all of the r requested bytes. Some types of channels, depending upon their
     * state, may write only some of the bytes or possibly none at all. A socket channel in non-blocking mode, for example, cannot write any more bytes than are
     * free in the socket's output buffer.
     * </p>
     * <p>
     * This method may be invoked at any time. If another thread has already initiated a write operation upon this channel, however, then an invocation of this
     * method will block until the first operation is complete.
     * </p>
     *
     * @param applicationBuffer The buffer from which bytes are to be retrieved
     *
     * @return The number of bytes written, possibly zero
     *
     * @throws java.nio.channels.NotYetConnectedException If this channel is not yet connected
     * @throws java.nio.channels.ClosedChannelException If this channel is closed
     * @throws java.nio.channels.AsynchronousCloseException If another thread closes this channel while the read operation is in progress
     * @throws java.nio.channels.ClosedByInterruptException If another thread interrupts the current thread while the read operation is in progress, thereby
     *             closing the channel and setting the current thread's interrupt status
     * @throws IOException If some other I/O error occurs
     * @throws IllegalArgumentException If the given applicationBuffer capacity ({@link ByteBuffer#capacity()} is less then the application buffer size of the
     *             {@link SSLEngine} session application buffer size ({@link SSLSession#getApplicationBufferSize()} this channel was constructed was.
     */
    @Override
    public synchronized int write(final ByteBuffer applicationBuffer) throws IOException, IllegalArgumentException
    {
        getLogger().debug("write:");

        int intialPosition = applicationBuffer.position();
        int writtenToChannel = this.sslEngineBuffer.wrap(applicationBuffer);

        if (writtenToChannel < 0)
        {
            getLogger().debug("write: channel closed");

            return writtenToChannel;
        }

        int totalWritten = applicationBuffer.position() - intialPosition;

        getLogger().debug("write: total written: {} amount available in network outbound: {}", totalWritten, applicationBuffer.remaining());

        return totalWritten;
    }

    /**
     * <p>
     * Writes a sequence of bytes to this channel from a subsequence of the given buffers.
     * </p>
     * <p>
     * An attempt is made to write up to r bytes to this channel, where r is the total number of bytes remaining in the specified subsequence of the given
     * buffer array, that is,
     * </p>
     *
     * <pre>
     * {@code
     * srcs[offset].remaining()
     *   + srcs[offset+1].remaining()
     *   + ... + srcs[offset+length-1].remaining()
     * }
     * </pre>
     * <p>
     * at the moment that this method is invoked.
     * </p>
     * <p>
     * Suppose that a byte sequence of length n is written, where 0 <= n <= r. Up to the first srcs[offset].remaining() bytes of this sequence are written from
     * buffer srcs[offset], up to the next srcs[offset+1].remaining() bytes are written from buffer srcs[offset+1], and so forth, until the entire byte sequence
     * is written. As many bytes as possible are written from each buffer, hence the final position of each updated buffer, except the last updated buffer, is
     * guaranteed to be equal to that buffer's limit.
     * </p>
     * <p>
     * Unless otherwise specified, a write operation will return only after writing all of the r requested bytes. Some types of channels, depending upon their
     * state, may write only some of the bytes or possibly none at all. A socket channel in non-blocking mode, for example, cannot write any more bytes than are
     * free in the socket's output buffer.
     * </p>
     * <p>
     * This method may be invoked at any time. If another thread has already initiated a write operation upon this channel, however, then an invocation of this
     * method will block until the first operation is complete.
     * </p>
     *
     * @param applicationByteBuffers The buffers from which bytes are to be retrieved
     * @param offset offset - The offset within the buffer array of the first buffer from which bytes are to be retrieved; must be non-negative and no larger
     *            than <code>srcs.length</code>
     * @param length The maximum number of buffers to be accessed; must be non-negative and no larger than <code>srcs.length - offset</code>
     *
     * @return The number of bytes written, possibly zero
     *
     * @throws java.nio.channels.NotYetConnectedException If this channel is not yet connected
     * @throws java.nio.channels.ClosedChannelException If this channel is closed
     * @throws java.nio.channels.AsynchronousCloseException If another thread closes this channel while the read operation is in progress
     * @throws java.nio.channels.ClosedByInterruptException If another thread interrupts the current thread while the read operation is in progress, thereby
     *             closing the channel and setting the current thread's interrupt status
     * @throws IOException If some other I/O error occurs
     * @throws IllegalArgumentException If one of the given applicationBuffers capacity ({@link ByteBuffer#capacity()} is less then the application buffer size
     *             of the {@link SSLEngine} session application buffer size ({@link SSLSession#getApplicationBufferSize()} this channel was constructed was.
     */
    @Override
    public long write(final ByteBuffer[] applicationByteBuffers, final int offset, final int length) throws IOException, IllegalArgumentException
    {
        long totalWritten = 0;

        for (int i = offset; i < length; i++)
        {
            ByteBuffer byteBuffer = applicationByteBuffers[i];

            if (byteBuffer.hasRemaining())
            {
                int written = write(byteBuffer);

                if (written > 0)
                {
                    totalWritten += written;

                    if (byteBuffer.hasRemaining())
                    {
                        break;
                    }
                }
                else
                {
                    if ((written < 0) && (totalWritten == 0))
                    {
                        totalWritten = -1;
                    }

                    break;
                }
            }
        }

        return totalWritten;
    }
}
