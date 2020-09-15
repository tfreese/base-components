/*
 * Copyright 2015 Corey Baswell Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package de.freese.base.security.ssl.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * <p>
 * A wrapper around a real {@link ServerSocketChannel} that produces {@link SSLSocketChannel} on {@link #accept()}. The real ServerSocketChannel must be bound
 * externally (to this class) before calling the accept method.
 * </p>
 *
 * @see SSLSocketChannel
 */
public class SSLServerSocketChannel extends ServerSocketChannel
{
    /**
     * @param items String[]
     * @param includedItems List<String>
     * @param excludedItems List<String>
     * @return String[]
     */
    static String[] filterArray(final String[] items, final List<String> includedItems, final List<String> excludedItems)
    {
        List<String> filteredItems = items == null ? new ArrayList<>() : Arrays.asList(items);
        if (includedItems != null)
        {
            for (int i = filteredItems.size() - 1; i >= 0; i--)
            {
                if (!includedItems.contains(filteredItems.get(i)))
                {
                    filteredItems.remove(i);
                }
            }

            for (String includedProtocol : includedItems)
            {
                if (!filteredItems.contains(includedProtocol))
                {
                    filteredItems.add(includedProtocol);
                }
            }
        }

        if (excludedItems != null)
        {
            for (int i = filteredItems.size() - 1; i >= 0; i--)
            {
                if (excludedItems.contains(filteredItems.get(i)))
                {
                    filteredItems.remove(i);
                }
            }
        }

        return filteredItems.toArray(new String[filteredItems.size()]);
    }

    /**
     * Should the SSLSocketChannels created from the accept method be put in blocking mode. Default is {@code false}.
     */
    private boolean blockingMode;

    /**
     * A list of ciphers to explicitly exclude for the SSL exchange. Default is none.
     */
    public List<String> excludedCipherSuites;

    /**
     * A list of SSL protocols (SSLv2, SSLv3, etc.) to explicitly exclude for the SSL exchange. Default is none.
     */
    public List<String> excludedProtocols;

    /**
     *
     */
    private final Executor executor;

    /**
     * The list of ciphers allowed for the SSL exchange. Default is the JVM default.
     */
    public List<String> includedCipherSuites;

    /**
     * The list of SSL protocols (TLSv1, TLSv1.1, etc.) supported for the SSL exchange. Default is the JVM default.
     */
    public List<String> includedProtocols;

    /**
     * Should the SSL server require client certificate authentication? Default is {@code false}.
     */
    private boolean needClientAuthentication;

    /**
     *
     */
    private final ServerSocketChannel serverSocketChannel;

    /**
     *
     */
    private final SSLContext sslContext;

    /**
     * Should the SS server ask for client certificate authentication? Default is {@code false}.
     */
    private boolean wantClientAuthentication;

    /**
     * @param serverSocketChannel The real server socket channel that accepts network requests.
     * @param sslContext The SSL context used to create the {@link SSLEngine} for incoming requests.
     * @param executor The thread pool passed to SSLSocketChannel used to execute long running, blocking SSL operations such as certificate validation with a CA
     *            (<a href="http://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLEngineResult.HandshakeStatus.html#NEED_TASK">NEED_TASK</a>)
     */
    public SSLServerSocketChannel(final ServerSocketChannel serverSocketChannel, final SSLContext sslContext, final Executor executor)
    {
        super(serverSocketChannel.provider());

        this.serverSocketChannel = Objects.requireNonNull(serverSocketChannel, "serverSocketChannel required");
        this.sslContext = Objects.requireNonNull(sslContext, "sslContext required");
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    /**
     * <p>
     * Accepts a connection made to this channel's socket.
     * </p>
     * <p>
     * If this channel is in non-blocking mode then this method will immediately return null if there are no pending connections. Otherwise it will block
     * indefinitely until a new connection is available or an I/O error occurs.
     * </p>
     * <p>
     * The socket channel returned by this method, if any, will be in blocking mode regardless of the blocking mode of this channel.
     * </p>
     * <p>
     * This method performs exactly the same security checks as the accept method of the ServerSocket class. That is, if a security manager has been installed
     * then for each new connection this method verifies that the address and port number of the connection's remote endpoint are permitted by the security
     * manager's checkAccept method.
     * </p>
     *
     * @return An SSLSocketChannel or {@code null} if this channel is in non-blocking mode and no connection is available to be accepted.
     * @throws java.nio.channels.NotYetConnectedException If this channel is not yet connected
     * @throws java.nio.channels.ClosedChannelException If this channel is closed
     * @throws java.nio.channels.AsynchronousCloseException If another thread closes this channel while the read operation is in progress
     * @throws java.nio.channels.ClosedByInterruptException If another thread interrupts the current thread while the read operation is in progress, thereby
     *             closing the channel and setting the current thread's interrupt status
     * @throws IOException If some other I/O error occurs
     */
    @Override
    public SocketChannel accept() throws IOException
    {
        SocketChannel channel = this.serverSocketChannel.accept();

        if (channel == null)
        {
            return null;
        }

        channel.configureBlocking(this.blockingMode);

        SSLEngine sslEngine = this.sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        sslEngine.setWantClientAuth(this.wantClientAuthentication);
        sslEngine.setNeedClientAuth(this.needClientAuthentication);
        sslEngine.setEnabledProtocols(filterArray(sslEngine.getEnabledProtocols(), this.includedProtocols, this.excludedProtocols));
        sslEngine.setEnabledCipherSuites(filterArray(sslEngine.getEnabledCipherSuites(), this.includedCipherSuites, this.excludedCipherSuites));

        return new SSLSocketChannel(channel, sslEngine, this.executor);
    }

    /**
     * Convenience call to keep from having to cast {@code SocketChannel} into {@link SSLSocketChannel} when calling {@link #accept()}.
     *
     * @return An SSLSocketChannel or {@code null} if this channel is in non-blocking mode and no connection is available to be accepted.
     * @throws IOException Falls was schief geht.
     * @see #accept()
     */
    public SSLSocketChannel acceptOverSSL() throws IOException
    {
        return (SSLSocketChannel) accept();
    }

    /**
     * @see java.nio.channels.ServerSocketChannel#bind(java.net.SocketAddress, int)
     */
    @Override
    public ServerSocketChannel bind(final SocketAddress local, final int backlog) throws IOException
    {
        return this.serverSocketChannel.bind(local, backlog);
    }

    /**
     * @see java.nio.channels.ServerSocketChannel#getLocalAddress()
     */
    @Override
    public SocketAddress getLocalAddress() throws IOException
    {
        return this.serverSocketChannel.getLocalAddress();
    }

    /**
     * @see java.nio.channels.NetworkChannel#getOption(java.net.SocketOption)
     */
    @Override
    public <T> T getOption(final SocketOption<T> name) throws IOException
    {
        return this.serverSocketChannel.getOption(name);
    }

    /**
     * @see java.nio.channels.spi.AbstractSelectableChannel#implCloseSelectableChannel()
     */
    @Override
    protected void implCloseSelectableChannel() throws IOException
    {
        this.serverSocketChannel.close();
    }

    /**
     * @see java.nio.channels.spi.AbstractSelectableChannel#implConfigureBlocking(boolean)
     */
    @Override
    protected void implConfigureBlocking(final boolean b) throws IOException
    {
        this.serverSocketChannel.configureBlocking(b);
    }

    /**
     * Should the SSLSocketChannels created from the accept method be put in blocking mode. Default is {@code false}.
     *
     * @param blockingMode boolean
     */
    public void setBlockingMode(final boolean blockingMode)
    {
        this.blockingMode = blockingMode;
    }

    /**
     * Should the SSL server require client certificate authentication? Default is {@code false}.
     *
     * @param needClientAuthentication boolean
     */
    public void setNeedClientAuthentication(final boolean needClientAuthentication)
    {
        this.needClientAuthentication = needClientAuthentication;
    }

    /**
     * @see java.nio.channels.ServerSocketChannel#setOption(java.net.SocketOption, java.lang.Object)
     */
    @Override
    public <T> ServerSocketChannel setOption(final SocketOption<T> name, final T value) throws IOException
    {
        return this.serverSocketChannel.setOption(name, value);
    }

    /**
     * Should the SS server ask for client certificate authentication? Default is {@code false}.
     *
     * @param wantClientAuthentication boolean
     */
    public void setWantClientAuthentication(final boolean wantClientAuthentication)
    {
        this.wantClientAuthentication = wantClientAuthentication;
    }

    /**
     * @see java.nio.channels.ServerSocketChannel#socket()
     */
    @Override
    public ServerSocket socket()
    {
        return this.serverSocketChannel.socket();
    }

    /**
     * @see java.nio.channels.NetworkChannel#supportedOptions()
     */
    @Override
    public Set<SocketOption<?>> supportedOptions()
    {
        return this.serverSocketChannel.supportedOptions();
    }
}
