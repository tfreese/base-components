package de.freese.base.security.ssl.nio.demo2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class NioSSLExample
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NioSSLExample.class);

    public static void main(final String[] args) throws Exception
    {
        InetSocketAddress address = new InetSocketAddress("www.amazon.com", 443);
        Selector selector = Selector.open();
        SocketChannel channel = SocketChannel.open();
        channel.connect(address);
        channel.configureBlocking(false);
        int ops = SelectionKey.OP_CONNECT | SelectionKey.OP_READ;

        SelectionKey key = channel.register(selector, ops);

        // create the worker threads
        final Executor ioWorker = Executors.newSingleThreadExecutor();
        final Executor taskWorkers = Executors.newFixedThreadPool(2);

        // create the SSLEngine
        final SSLEngine engine = SSLContext.getDefault().createSSLEngine();
        engine.setUseClientMode(true);
        engine.beginHandshake();

        final int ioBufferSize = 32 * 1024;
        final AbstractNioSSLProvider ssl = new AbstractNioSSLProvider(key, engine, ioBufferSize, ioWorker, taskWorkers)
        {
            /**
             * @see de.freese.base.security.ssl.nio.demo2.AbstractSSLProvider#onClosed()
             */
            @Override
            public void onClosed()
            {
                LOGGER.info("ssl session closed");
            }

            /**
             * @see de.freese.base.security.ssl.nio.demo2.AbstractSSLProvider#onFailure(java.lang.Exception)
             */
            @Override
            public void onFailure(final Exception ex)
            {
                LOGGER.error("handshake failure", ex);
            }

            /**
             * @see de.freese.base.security.ssl.nio.demo2.AbstractSSLProvider#onInput(java.nio.ByteBuffer)
             */
            @Override
            public void onInput(final ByteBuffer decrypted)
            {
                // HTTP response
                byte[] dst = new byte[decrypted.remaining()];
                decrypted.get(dst);
                String response = new String(dst, StandardCharsets.UTF_8);

                LOGGER.info(response);
            }

            /**
             * @see de.freese.base.security.ssl.nio.demo2.AbstractSSLProvider#onSuccess()
             */
            @Override
            public void onSuccess()
            {
                LOGGER.info("handshake success");
                SSLSession session = this.engine.getSession();

                try
                {
                    LOGGER.info("local principal: {}", session.getLocalPrincipal());
                    LOGGER.info("remote principal: {}", session.getPeerPrincipal());
                    LOGGER.info("cipher: {}", session.getCipherSuite());
                }
                catch (Exception ex)
                {
                    LOGGER.error("handshake failure", ex);
                }

                // HTTP request
                StringBuilder http = new StringBuilder();
                http.append("GET / HTTP/1.0\r\n");
                http.append("Connection: close\r\n");
                http.append("\r\n");
                byte[] data = http.toString().getBytes(StandardCharsets.UTF_8);
                ByteBuffer send = ByteBuffer.wrap(data);
                sendAsync(send);
            }
        };

        // NIO selector
        while (true)
        {
            key.selector().select();
            Iterator<SelectionKey> keys = key.selector().selectedKeys().iterator();

            while (keys.hasNext())
            {
                keys.next();
                keys.remove();
                ssl.processInput();
            }
        }
    }
}
