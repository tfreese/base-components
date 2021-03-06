package de.freese.base.security.ssl.nio.demo3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Iterator;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

/**
 * An SSL/TLS server, that will listen to a specific address and port and serve SSL/TLS connections compatible with the protocol it applies.
 * <p/>
 * After initialization {@link NioSslServer#start()} should be called so the server starts to listen to new connection requests. At this point, start is
 * blocking, so, in order to be able to gracefully stop the server, a {@link Runnable} containing a server object should be created. This runnable should start
 * the server in its run method and also provide a stop method, which will call {@link NioSslServer#stop()}.
 * </p>
 * NioSslServer makes use of Java NIO, and specifically listens to new connection requests with a {@link ServerSocketChannel}, which will create new
 * {@link SocketChannel}s and a {@link Selector} which serves all the connections in one thread.
 *
 * @author <a href="mailto:alex.a.karnezis@gmail.com">Alex Karnezis</a>
 */
public class NioSslServer extends AbstractNioSslPeer
{
    /**
     * Declares if the server is active to serve and create new connections.
     */
    private boolean active;

    /**
     * The context will be initialized with a specific SSL/TLS protocol and will then be used to create {@link SSLEngine} classes for each new connection that
     * arrives to the server.
     */
    private SSLContext context;

    /**
     * A part of Java NIO that will be used to serve all connections to the server in one thread.
     */
    private Selector selector;

    /**
     * Server is designed to apply an SSL/TLS protocol and listen to an IP address and port.
     *
     * @param protocol - the SSL/TLS protocol that this server will be configured to apply.
     * @param hostAddress - the IP address this server will listen to.
     * @param port - the port this server will listen to.
     * @throws Exception Falls was schief geht.
     */
    public NioSslServer(final String protocol, final String hostAddress, final int port) throws Exception
    {

        this.context = SSLContext.getInstance(protocol);
        this.context.init(createKeyManagers("./src/test/resources/server_keystore.p12", "password", "password"),
                createTrustManagers("./src/test/resources/server_truststore.p12", "password"), new SecureRandom());

        SSLSession dummySession = this.context.createSSLEngine().getSession();
        this.myAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
        this.myNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
        this.peerAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
        this.peerNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
        dummySession.invalidate();

        this.selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(hostAddress, port));
        serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        this.active = true;
    }

    /**
     * Will be called after a new connection request arrives to the server. Creates the {@link SocketChannel} that will be used as the network layer link, and
     * the {@link SSLEngine} that will encrypt and decrypt all the data that will be exchanged during the session with this specific client.
     *
     * @param key - the key dedicated to the {@link ServerSocketChannel} used by the server to listen to new connection requests.
     * @throws Exception Falls was schief geht.
     */
    private void accept(final SelectionKey key) throws Exception
    {
        getLogger().debug("New connection request!");

        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        socketChannel.configureBlocking(false);

        SSLEngine engine = this.context.createSSLEngine();
        engine.setUseClientMode(false);
        engine.beginHandshake();

        if (doHandshake(socketChannel, engine))
        {
            socketChannel.register(this.selector, SelectionKey.OP_READ, engine);
        }
        else
        {
            socketChannel.close();
            getLogger().debug("Connection closed due to handshake failure.");
        }
    }

    /**
     * Determines if the the server is active or not.
     *
     * @return if the server is active or not.
     */
    private boolean isActive()
    {
        return this.active;
    }

    /**
     * Will be called by the selector when the specific socket channel has data to be read. As soon as the server reads these data, it will call
     * {@link NioSslServer#write(SocketChannel, SSLEngine, String)} to send back a trivial response.
     *
     * @param socketChannel - the transport link used between the two peers.
     * @param engine - the engine used for encryption/decryption of the data exchanged between the two peers.
     * @throws IOException if an I/O error occurs to the socket channel.
     */
    @Override
    protected void read(final SocketChannel socketChannel, final SSLEngine engine) throws IOException
    {
        getLogger().debug("About to read from a client...");

        this.peerNetData.clear();
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
                        getLogger().debug("Incoming message: {}", new String(this.peerAppData.array(), StandardCharsets.UTF_8));
                        break;
                    case BUFFER_OVERFLOW:
                        this.peerAppData = enlargeApplicationBuffer(engine, this.peerAppData);
                        break;
                    case BUFFER_UNDERFLOW:
                        this.peerNetData = handleBufferUnderflow(engine, this.peerNetData);
                        break;
                    case CLOSED:
                        getLogger().debug("Client wants to close connection...");
                        closeConnection(socketChannel, engine);
                        getLogger().debug("Goodbye client!");
                        return;
                    default:
                        throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                }
            }

            write(socketChannel, engine, "Hello! I am your server!");
        }
        else if (bytesRead < 0)
        {
            getLogger().error("Received end of stream. Will try to close connection with client...");
            handleEndOfStream(socketChannel, engine);
            getLogger().debug("Goodbye client!");
        }
    }

    /**
     * Should be called in order the server to start listening to new connections. This method will run in a loop as long as the server is active. In order to
     * stop the server you should use {@link NioSslServer#stop()} which will set it to inactive state and also wake up the listener, which may be in blocking
     * select() state.
     *
     * @throws Exception Falls was schief geht.
     */
    public void start() throws Exception
    {
        getLogger().debug("Initialized and waiting for new connections...");

        while (isActive())
        {
            this.selector.select();
            Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();

            while (selectedKeys.hasNext())
            {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();

                if (!key.isValid())
                {
                    continue;
                }
                if (key.isAcceptable())
                {
                    accept(key);
                }
                else if (key.isReadable())
                {
                    read((SocketChannel) key.channel(), (SSLEngine) key.attachment());
                }
            }
        }

        getLogger().debug("Goodbye!");
    }

    /**
     * Sets the server to an inactive state, in order to exit the reading loop in {@link NioSslServer#start()} and also wakes up the selector, which may be in
     * select() blocking state.
     */
    public void stop()
    {
        getLogger().debug("Will now close server...");
        this.active = false;
        this.executor.shutdown();
        this.selector.wakeup();
    }

    /**
     * Will send a message back to a client.
     *
     * @param message - the message to be sent.
     * @throws IOException if an I/O error occurs to the socket channel.
     */
    @Override
    protected void write(final SocketChannel socketChannel, final SSLEngine engine, final String message) throws IOException
    {
        getLogger().debug("About to write to a client...");

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

                    getLogger().debug("Message sent to the client: {}", message);
                    break;
                case BUFFER_OVERFLOW:
                    this.myNetData = enlargePacketBuffer(engine, this.myNetData);
                    break;
                case BUFFER_UNDERFLOW:
                    throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
                case CLOSED:
                    closeConnection(socketChannel, engine);
                    return;
                default:
                    throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
        }
    }
}
