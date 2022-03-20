package de.freese.base.security.ssl.nio.demo3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

/**
 * An SSL/TLS client that connects to a server using its IP address and port.
 * <p/>
 * After initialization of a {@link NioSslClient} object, {@link NioSslClient#connect()} should be called, in order to establish connection with the server.
 * <p/>
 * When the connection between the client and the object is established, {@link NioSslClient} provides a public write and read method, in order to communicate
 * with its peer.
 *
 * @author <a href="mailto:alex.a.karnezis@gmail.com">Alex Karnezis</a>
 */
public class NioSslClient extends AbstractNioSslPeer
{
    /**
     * The engine that will be used to encrypt/decrypt data between this client and the server.
     */
    private final SSLEngine engine;
    /**
     * The port of the server this client is configured to connect to.
     */
    private final int port;
    /**
     * The remote address of the server this client is configured to connect to.
     */
    private final String remoteAddress;
    /**
     * The socket channel that will be used as the transport link between this client and the server.
     */
    private SocketChannel socketChannel;

    /**
     * Initiates the engine to run as a client using peer information, and allocates space for the buffers that will be used by the engine.
     *
     * @param protocol The SSL/TLS protocol to be used. Java 1.6 will only run with up to TLSv1 protocol. Java 1.7 or higher also supports TLSv1.1 and TLSv1.2
     * protocols.
     * @param remoteAddress The IP address of the peer.
     * @param port The peer's port that will be used.
     *
     * @throws Exception Falls was schief geht.
     */
    public NioSslClient(final String protocol, final String remoteAddress, final int port) throws Exception
    {
        this.remoteAddress = remoteAddress;
        this.port = port;

        SSLContext context = SSLContext.getInstance(protocol);
        context.init(createKeyManagers("./src/test/resources/client_keystore.p12", "password", "password"),
                createTrustManagers("./src/test/resources/client_keystore.p12", "password"), new SecureRandom());
        this.engine = context.createSSLEngine(remoteAddress, port);
        this.engine.setUseClientMode(true);

        SSLSession session = this.engine.getSession();
        this.myAppData = ByteBuffer.allocate(1024);
        this.myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
        this.peerAppData = ByteBuffer.allocate(1024);
        this.peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());
    }

    /**
     * Opens a socket channel to communicate with the configured server and tries to complete the handshake protocol.
     *
     * @return True if client established a connection with the server, false otherwise.
     *
     * @throws Exception Falls was schief geht.
     */
    public boolean connect() throws Exception
    {
        this.socketChannel = SocketChannel.open();
        this.socketChannel.configureBlocking(false);
        this.socketChannel.connect(new InetSocketAddress(this.remoteAddress, this.port));

        while (!this.socketChannel.finishConnect())
        {
            // can do something here...
        }

        this.engine.beginHandshake();
        return doHandshake(this.socketChannel, this.engine);
    }

    /**
     * Public method to try to read from the server.
     *
     * @throws Exception Falls was schief geht.
     */
    public void read() throws Exception
    {
        read(this.socketChannel, this.engine);
    }

    /**
     * Should be called when the client wants to explicitly close the connection to the server.
     *
     * @throws IOException if an I/O error occurs to the socket channel.
     */
    public void shutdown() throws IOException
    {
        getLogger().debug("About to close connection with the server...");

        closeConnection(this.socketChannel, this.engine);
        this.executor.shutdown();

        getLogger().debug("Goodbye!");
    }

    /**
     * Public method to send a message to the server.
     *
     * @param message - message to be sent to the server.
     *
     * @throws IOException if an I/O error occurs to the socket channel.
     */
    public void write(final String message) throws IOException
    {
        write(this.socketChannel, this.engine, message);
    }

    /**
     * Will wait for response from the remote peer, until it actually gets something. Uses {@link SocketChannel#read(ByteBuffer)}, which is non-blocking, and if
     * it gets nothing from the peer, waits for {@code waitToReadMillis} and tries again.
     * <p/>
     * Just like {@link NioSslClient#read(SocketChannel, SSLEngine)} it uses inner class' socket channel and engine and should not be used by the client.
     * {@link NioSslClient#read()} should be called instead.
     *
     * @param socketChannel {@link SocketChannel}
     * @param engine - the engine used for encryption/decryption of the data exchanged between the two peers.
     *
     * @throws Exception Falls was schief geht.
     */
    @Override
    protected void read(final SocketChannel socketChannel, final SSLEngine engine) throws Exception
    {
        getLogger().debug("About to read from the server...");

        this.peerNetData.clear();
        int waitToReadMillis = 50;
        boolean exitReadLoop = false;

        while (!exitReadLoop)
        {
            int bytesRead = socketChannel.read(this.peerNetData);

            if (bytesRead > 0)
            {
                this.peerNetData.flip();

                while (this.peerNetData.hasRemaining())
                {
                    this.peerAppData.clear();
                    SSLEngineResult result = engine.unwrap(this.peerNetData, this.peerAppData);

                    switch (result.getStatus())
                    {
                        case OK:
                            this.peerAppData.flip();

                            if (getLogger().isDebugEnabled())
                            {
                                getLogger().debug("Server response: {}", new String(this.peerAppData.array(), StandardCharsets.UTF_8));
                            }

                            exitReadLoop = true;
                            break;
                        case BUFFER_OVERFLOW:
                            this.peerAppData = enlargeApplicationBuffer(engine, this.peerAppData);
                            break;
                        case BUFFER_UNDERFLOW:
                            this.peerNetData = handleBufferUnderflow(engine, this.peerNetData);
                            break;
                        case CLOSED:
                            closeConnection(socketChannel, engine);
                            return;
                        default:
                            throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                    }
                }
            }
            else if (bytesRead < 0)
            {
                handleEndOfStream(socketChannel, engine);

                return;
            }

            Thread.sleep(waitToReadMillis);
        }
    }

    /**
     * Implements the write method that sends a message to the server the client is connected to, but should not be called by the user, since socket channel and
     * engine are inner class' variables. {@link NioSslClient#write(String)} should be called instead.
     *
     * @param socketChannel {@link SocketChannel}
     * @param engine - the engine used for encryption/decryption of the data exchanged between the two peers.
     *
     * @throws IOException if an I/O error occurs to the socket channel.
     */
    @Override
    protected void write(final SocketChannel socketChannel, final SSLEngine engine, final String message) throws IOException
    {
        getLogger().debug("About to write to the server...");

        this.myAppData.clear();
        this.myAppData.put(message.getBytes(StandardCharsets.UTF_8));
        this.myAppData.flip();

        while (this.myAppData.hasRemaining())
        {
            // The loop has a meaning for (outgoing) messages larger than 16KB.
            // Every wrap call will remove 16KB from the original message and send it to the remote peer.
            this.myNetData.clear();
            SSLEngineResult result = engine.wrap(this.myAppData, this.myNetData);

            switch (result.getStatus())
            {
                case OK:
                    this.myNetData.flip();

                    while (this.myNetData.hasRemaining())
                    {
                        socketChannel.write(this.myNetData);
                    }

                    getLogger().debug("Message sent to the server: {}", message);
                    break;
                case BUFFER_OVERFLOW:
                    this.myNetData = enlargePacketBuffer(engine, this.myNetData);
                    break;
                case BUFFER_UNDERFLOW:
                    throw new SSLException("Buffer underflow occurred after a wrap. I don't think we should ever get here.");
                case CLOSED:
                    closeConnection(socketChannel, engine);
                    return;
                default:
                    throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
        }
    }
}
