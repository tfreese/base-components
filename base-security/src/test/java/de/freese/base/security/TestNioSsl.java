// Created: 15.09.2020
package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import de.freese.base.security.ssl.nio.SSLServerSocketChannel;
import de.freese.base.security.ssl.nio.SSLSocketChannel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled
class TestNioSsl
{
    /**
     *
     */
    private static final boolean USE_SSL = true;
    /**
     *
     */
    public static boolean isShutdown;
    /**
     *
     */
    private static SSLContext clientSslContext;
    /**
     *
     */
    private static Selector selector;
    /**
     *
     */
    private static ServerSocketChannel serverSocketChannel;
    /**
     *
     */
    private static SSLContext serverSslContext;

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        isShutdown = true;
        selector.wakeup();

        selector.close();
        serverSocketChannel.close();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        if (USE_SSL)
        {
            setUpClientSslContext();
            setUpServerSslContext();
        }

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverSocketChannel.bind(new InetSocketAddress(9999), 50);

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        if (USE_SSL)
        {
            // Vor dem register wird ne Exception geworfen.
            serverSocketChannel = new SSLServerSocketChannel(serverSocketChannel, serverSslContext, Executors.newSingleThreadExecutor());
        }

        ForkJoinPool.commonPool().execute(() ->
        {
            while (!Thread.interrupted())
            {
                try
                {
                    int readyChannels = selector.select();

                    if (isShutdown || !selector.isOpen())
                    {
                        break;
                    }

                    if (readyChannels > 0)
                    {
                        Set<SelectionKey> selected = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selected.iterator();

                        try
                        {
                            while (iterator.hasNext())
                            {
                                SelectionKey selectionKey = iterator.next();
                                iterator.remove();

                                if (selectionKey.isAcceptable())
                                {
                                    SSLSocketChannel sslSocketChannel = null;
                                    SocketChannel socketChannel = null;

                                    if (USE_SSL)
                                    {
                                        sslSocketChannel = ((SSLServerSocketChannel) serverSocketChannel).acceptOverSSL();
                                        socketChannel = sslSocketChannel.getWrappedSocketChannel();
                                    }
                                    else
                                    {
                                        socketChannel = serverSocketChannel.accept();
                                    }

                                    socketChannel.configureBlocking(false);
                                    SelectionKey sk = socketChannel.register(selector, SelectionKey.OP_READ);

                                    if (sslSocketChannel != null)
                                    {
                                        sk.attach(sslSocketChannel);
                                    }
                                }
                                else if (selectionKey.isReadable())
                                {
                                    SocketChannel channel = (SocketChannel) selectionKey.channel();

                                    if (selectionKey.attachment() instanceof SSLSocketChannel)
                                    {
                                        channel = (SSLSocketChannel) selectionKey.attachment();
                                    }

                                    ByteBuffer buffer = ByteBuffer.allocate(16704);

                                    channel.read(buffer);

                                    while (buffer.position() == 0)
                                    {
                                        channel.read(buffer);
                                    }

                                    buffer.flip();
                                    System.out.printf("buffer: position=%d, limit=%d%n", buffer.position(), buffer.limit());
                                    byte[] bytes = new byte[buffer.getInt()];
                                    buffer.get(bytes);
                                    String text = new String(bytes, StandardCharsets.UTF_8);

                                    System.out.println("Server: got message '" + text + "'");

                                    // Response
                                    buffer.clear();
                                    buffer.putInt(bytes.length);
                                    buffer.put(bytes);
                                    buffer.flip();

                                    while (buffer.hasRemaining())
                                    {
                                        channel.write(buffer);
                                    }
                                }
                            }
                        }
                        finally
                        {
                            selected.clear();
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            System.out.println("shutdown server");
        });
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    private static void setUpClientSslContext() throws Exception
    {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (InputStream inputStream = TestNioSsl.class.getResourceAsStream("client_keystore.p12"))
        {
            keyStore.load(inputStream, "password".toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "password".toCharArray());

        KeyStore trustStore = KeyStore.getInstance("PKCS12");

        try (InputStream inputStream = TestNioSsl.class.getResourceAsStream("client_truststore.p12"))
        {
            trustStore.load(inputStream, "password".toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        clientSslContext = SSLContext.getInstance("TLSv1.3");
        clientSslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    private static void setUpServerSslContext() throws Exception
    {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (InputStream inputStream = TestNioSsl.class.getResourceAsStream("server_keystore.p12"))
        {
            keyStore.load(inputStream, "password".toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "password".toCharArray());

        KeyStore trustStore = KeyStore.getInstance("PKCS12");

        try (InputStream inputStream = TestNioSsl.class.getResourceAsStream("server_truststore.p12"))
        {
            trustStore.load(inputStream, "password".toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        serverSslContext = SSLContext.getInstance("TLSv1.3");
        serverSslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings("resource")
    @Test
    void testNio() throws Exception
    {
        String message = "Hello World!";

        try (SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9999)))
        {
            SocketChannel client = socketChannel;

            if (USE_SSL)
            {
                SSLEngine sslEngine = clientSslContext.createSSLEngine();
                sslEngine.setUseClientMode(true);
                // sslEngine.setWantClientAuth(this.wantClientAuthentication);
                // sslEngine.setNeedClientAuth(this.needClientAuthentication);

                client = new SSLSocketChannel(socketChannel, sslEngine, Executors.newSingleThreadExecutor());
            }

            while (!client.finishConnect())
            {
                // can do something here...
            }

            client.configureBlocking(true);

            ByteBuffer buffer = ByteBuffer.allocate(32);

            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            buffer.putInt(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            // while (buffer.hasRemaining())
            {
                client.write(buffer);
            }

            // Response
            buffer.clear();
            client.read(buffer);
            buffer.flip();

            bytes = new byte[buffer.getInt()];
            buffer.get(bytes);
            String response = new String(bytes, StandardCharsets.UTF_8);
            System.out.println("Client: got message '" + response + "'");

            assertEquals(message, response);
        }
    }
}
