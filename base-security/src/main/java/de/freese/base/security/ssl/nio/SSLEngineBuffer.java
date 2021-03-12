/*
 * Copyright 2015 Corey Baswell Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package de.freese.base.security.ssl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
class SSLEngineBuffer
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SSLEngineBuffer.class);

    /**
     *
     */
    private final Executor executor;

    /**
     *
     */
    private final int minimumApplicationBufferSize;

    /**
     *
     */
    private final ByteBuffer networkInboundBuffer;

    /**
     *
     */
    private final ByteBuffer networkOutboundBuffer;

    /**
     *
     */
    private final SocketChannel socketChannel;

    /**
     *
     */
    private final SSLEngine sslEngine;

    /**
     *
     */
    private final ByteBuffer unwrapBuffer;

    /**
     *
     */
    private final ByteBuffer wrapBuffer;

    /**
     * Erstellt ein neues {@link SSLEngineBuffer} Object.
     *
     * @param socketChannel {@link SocketChannel}
     * @param sslEngine {@link SSLEngine}
     * @param executor {@link Executor}
     */
    SSLEngineBuffer(final SocketChannel socketChannel, final SSLEngine sslEngine, final Executor executor)
    {
        super();

        this.socketChannel = Objects.requireNonNull(socketChannel, "socketChannel required");
        this.sslEngine = Objects.requireNonNull(sslEngine, "sslEngine required");
        this.executor = Objects.requireNonNull(executor, "executor required");

        SSLSession session = sslEngine.getSession();

        int networkBufferSize = session.getPacketBufferSize();
        this.networkInboundBuffer = ByteBuffer.allocate(networkBufferSize);
        this.networkOutboundBuffer = ByteBuffer.allocate(networkBufferSize);
        this.networkOutboundBuffer.flip();

        this.minimumApplicationBufferSize = session.getApplicationBufferSize();
        this.unwrapBuffer = ByteBuffer.allocate(this.minimumApplicationBufferSize);
        this.wrapBuffer = ByteBuffer.allocate(this.minimumApplicationBufferSize);
        this.wrapBuffer.flip();
    }

    /**
     *
     */
    void close()
    {
        try
        {
            this.sslEngine.closeInbound();
        }
        catch (Exception ex)
        {
            // Empty
        }

        try
        {
            this.sslEngine.closeOutbound();
        }
        catch (Exception ex)
        {
            // Empty
        }
    }

    /**
     * @param applicationInputBuffer {@link ByteBuffer}
     * @return int
     * @throws IOException Falls was schief geht.
     */
    private int doUnwrap(final ByteBuffer applicationInputBuffer) throws IOException
    {
        getLogger().debug("unwrap:");

        int totalReadFromChannel = 0;

        // Keep looping until peer has no more data ready or the applicationInboundBuffer is full
        UNWRAP:
        do
        {
            // 1. Pull data from peer into networkInboundBuffer
            int readFromChannel = 0;

            while (this.networkInboundBuffer.hasRemaining())
            {
                int read = this.socketChannel.read(this.networkInboundBuffer);

                getLogger().debug("unwrap: socket read {} ({}, {})", read, readFromChannel, totalReadFromChannel);

                if (read <= 0)
                {
                    if ((read < 0) && (readFromChannel == 0) && (totalReadFromChannel == 0))
                    {
                        // No work done and we've reached the end of the channel from peer
                        getLogger().debug("unwrap: exit: end of channel");

                        return read;
                    }

                    break;
                }

                readFromChannel += read;
            }

            this.networkInboundBuffer.flip();

            if (!this.networkInboundBuffer.hasRemaining())
            {
                this.networkInboundBuffer.compact();
                // wrap(applicationOutputBuffer, applicationInputBuffer, false);
                return totalReadFromChannel;
            }

            totalReadFromChannel += readFromChannel;

            try
            {
                SSLEngineResult result = this.sslEngine.unwrap(this.networkInboundBuffer, applicationInputBuffer);

                getLogger().debug("unwrap: result: {}", result);

                switch (result.getStatus())
                {
                    case OK:
                        SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();

                        switch (handshakeStatus)
                        {
                            case NEED_UNWRAP:
                                break;

                            case NEED_WRAP:
                                break UNWRAP;

                            case NEED_TASK:
                                runHandshakeTasks();
                                break;

                            case NOT_HANDSHAKING:
                            default:
                                break;
                        }
                        break;

                    case BUFFER_OVERFLOW:
                        getLogger().debug("unwrap: buffer overflow");

                        break UNWRAP;

                    case CLOSED:
                        getLogger().debug("unwrap: exit: ssl closed");

                        return totalReadFromChannel == 0 ? -1 : totalReadFromChannel;

                    case BUFFER_UNDERFLOW:
                        getLogger().debug("unwrap: buffer underflow");

                        break;
                }
            }
            finally
            {
                this.networkInboundBuffer.compact();
            }
        }
        while (applicationInputBuffer.hasRemaining());

        return totalReadFromChannel;
    }

    /**
     * @param applicationOutboundBuffer {@link ByteBuffer}
     * @return int
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("incomplete-switch")
    private int doWrap(final ByteBuffer applicationOutboundBuffer) throws IOException
    {
        getLogger().debug("wrap:");

        int totalWritten = 0;

        // 1. Send any data already wrapped out channel

        if (this.networkOutboundBuffer.hasRemaining())
        {
            totalWritten = send(this.socketChannel, this.networkOutboundBuffer);

            if (totalWritten < 0)
            {
                return totalWritten;
            }
        }

        // 2. Any data in application buffer ? Wrap that and send it to peer.

        WRAP:
        while (true)
        {
            this.networkOutboundBuffer.compact();
            SSLEngineResult result = this.sslEngine.wrap(applicationOutboundBuffer, this.networkOutboundBuffer);

            getLogger().debug("wrap: result: " + result);

            this.networkOutboundBuffer.flip();

            if (this.networkOutboundBuffer.hasRemaining())
            {
                int written = send(this.socketChannel, this.networkOutboundBuffer);

                if (written < 0)
                {
                    return totalWritten == 0 ? written : totalWritten;
                }

                totalWritten += written;
            }

            switch (result.getStatus())
            {
                case OK:
                    switch (result.getHandshakeStatus())
                    {
                        case NEED_WRAP:
                            break;

                        case NEED_UNWRAP:
                            break WRAP;

                        case NEED_TASK:
                            runHandshakeTasks();

                            getLogger().debug("wrap: exit: need tasks");

                            break;

                        case NOT_HANDSHAKING:
                            if (applicationOutboundBuffer.hasRemaining())
                            {
                                break;
                            }

                            break WRAP;
                    }

                    break;

                case BUFFER_OVERFLOW:
                    getLogger().debug("wrap: exit: buffer overflow");

                    break WRAP;

                case CLOSED:
                    getLogger().debug("wrap: exit: closed");

                    break WRAP;

                case BUFFER_UNDERFLOW:
                    getLogger().debug("wrap: exit: buffer underflow");

                    break WRAP;
            }
        }

        getLogger().debug("wrap: return: {}", totalWritten);

        return totalWritten;
    }

    /**
     * @return int
     * @throws IOException Falls was schief geht.
     */
    int flushNetworkOutbound() throws IOException
    {
        return send(this.socketChannel, this.networkOutboundBuffer);
    }

    /**
     * @return {@link Logger}
     */
    private Logger getLogger()
    {
        return LOGGER;
    }

    /**
     *
     */
    private void runHandshakeTasks()
    {
        while (true)
        {
            final Runnable runnable = this.sslEngine.getDelegatedTask();

            if (runnable == null)
            {
                break;
            }

            this.executor.execute(runnable);
        }
    }

    /**
     * @param channel {@link SocketChannel}
     * @param buffer {@link ByteBuffer}
     * @return int
     * @throws IOException Falls was schief geht.
     */
    int send(final SocketChannel channel, final ByteBuffer buffer) throws IOException
    {
        int totalWritten = 0;

        while (buffer.hasRemaining())
        {
            int written = channel.write(buffer);

            if (written == 0)
            {
                break;
            }
            else if (written < 0)
            {
                return (totalWritten == 0) ? written : totalWritten;
            }

            totalWritten += written;
        }

        getLogger().debug("sent: {} out to socket", totalWritten);

        return totalWritten;
    }

    /**
     * @param applicationInputBuffer {@link ByteBuffer}
     * @return int
     * @throws IOException Falls was schief geht.
     */
    int unwrap(final ByteBuffer applicationInputBuffer) throws IOException
    {
        if (applicationInputBuffer.capacity() < this.minimumApplicationBufferSize)
        {
            throw new IllegalArgumentException("Application buffer size must be at least: " + this.minimumApplicationBufferSize);
        }

        if (this.unwrapBuffer.position() != 0)
        {
            this.unwrapBuffer.flip();

            while (this.unwrapBuffer.hasRemaining() && applicationInputBuffer.hasRemaining())
            {
                applicationInputBuffer.put(this.unwrapBuffer.get());
            }

            this.unwrapBuffer.compact();
        }

        int totalUnwrapped = 0;
        int unwrapped = 0;
        int wrapped = 0;

        do
        {
            totalUnwrapped += unwrapped = doUnwrap(applicationInputBuffer);
            wrapped = doWrap(this.wrapBuffer);
        }
        while ((unwrapped > 0) || ((wrapped > 0) && (this.networkOutboundBuffer.hasRemaining() && this.networkInboundBuffer.hasRemaining())));

        return totalUnwrapped;
    }

    /**
     * @param applicationOutboundBuffer {@link ByteBuffer}
     * @return int
     * @throws IOException Falls was schief geht.
     */
    int wrap(final ByteBuffer applicationOutboundBuffer) throws IOException
    {
        int wrapped = doWrap(applicationOutboundBuffer);
        doUnwrap(this.unwrapBuffer);

        return wrapped;
    }
}
