// Created: 13.09.2020
package de.freese.base.security.ssl.nio.demo1;

import java.nio.ByteBuffer;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

/**
 * A SSLEngine usage example which simplifies the presentation by removing the I/O and multi-threading concerns.<br>
 * The demo creates two SSLEngines, simulating a client and server.<br>
 * The "transport" layer consists two ByteBuffers: think of them as directly connected pipes.<br>
 * Note, this is a *very* simple example: real code will be much more involved.<br>
 * For example, different threading and I/O models could be used, transport mechanisms could close unexpectedly, and so on.<br>
 * When this application runs, notice that several messages (wrap/unwrap) pass before any application data is consumed or produced.<br>
 * (For more information, please see the SSL/TLS specifications.)<br>
 * There may several steps for a successful handshake, so it's typical to see the following series of operations:<br>
 *
 * <pre>
 *      client          server          message
 *      ======          ======          =======
 *      wrap()          ...             ClientHello
 *      ...             unwrap()        ClientHello
 *      ...             wrap()          ServerHello/Certificate
 *      unwrap()        ...             ServerHello/Certificate
 *      wrap()          ...             ClientKeyExchange
 *      wrap()          ...             ChangeCipherSpec
 *      wrap()          ...             Finished
 *      ...             unwrap()        ClientKeyExchange
 *      ...             unwrap()        ChangeCipherSpec
 *      ...             unwrap()        Finished
 *      ...             wrap()          ChangeCipherSpec
 *      ...             wrap()          Finished
 *      unwrap()        ...             ChangeCipherSpec
 *      unwrap()        ...             Finished
 * </pre>
 */
public class SSLEngineExample
{
    /*
     * Enables the JSSE system debugging system property: -Djavax.net.debug=all This gives a lot of low-level information about operations underway, including
     * specific handshake messages, and might be best examined after gaining some familiarity with this application.
     */
    private static final boolean DEBUG = false;

    /*
     * The following is to set up the keystores.
     */
    private static final String KEYSTORE_FILE = "testkeys.jks";

    /**
     *
     */
    private static final String PASSWD = "passphrase";

    /*
     * Logging code
     */
    private static boolean resultOnce = true;

    /**
     *
     */
    private static final String TRUSTSTORE_FILE = "testkeys.jks";

    /*
     * Simple check to make sure everything came across as expected.
     */
    /**
     * @param a {@link ByteBuffer}
     * @param b {@link ByteBuffer}
     * @throws Exception Falls was schief geht.
     */
    private static void checkTransfer(final ByteBuffer a, final ByteBuffer b) throws Exception
    {
        a.flip();
        b.flip();

        if (!a.equals(b))
        {
            throw new Exception("Data didn't transfer cleanly");
        }

        log("\tData transferred cleanly");

        a.position(a.limit());
        b.position(b.limit());
        a.limit(a.capacity());
        b.limit(b.capacity());
    }

    /**
     * @param engine {@link SSLEngine}
     * @return boolean
     */
    private static boolean isEngineClosed(final SSLEngine engine)
    {
        return (engine.isOutboundDone() && engine.isInboundDone());
    }

    /**
     * @param str String
     */
    private static void log(final String str)
    {
        System.out.println(str);
    }

    /**
     * @param str String
     * @param result {@link SSLEngineResult}
     */
    private static void log(final String str, final SSLEngineResult result)
    {
        if (resultOnce)
        {
            resultOnce = false;
            System.out.println(
                    "The format of the SSLEngineResult is: \n" + "\t\"getStatus() / getHandshakeStatus()\" +\n" + "\t\"bytesConsumed() / bytesProduced()\"\n");
        }

        HandshakeStatus hsStatus = result.getHandshakeStatus();

        log(str + result.getStatus() + "/" + hsStatus + ", " + result.bytesConsumed() + "/" + result.bytesProduced() + " bytes");

        if (hsStatus == HandshakeStatus.FINISHED)
        {
            log("\t...ready for application data");
        }
    }

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        if (DEBUG)
        {
            System.setProperty("javax.net.debug", "all");
        }

        SSLEngineExample demo = new SSLEngineExample();
        demo.runDemo();

        System.out.println("Demo Completed.");
    }

    /*
     * If the result indicates that we have outstanding tasks to do, go ahead and run them in this thread.
     */
    /**
     * @param result {@link SSLEngineResult}
     * @param engine {@link SSLEngine}
     * @throws Exception Falls was schief geht.
     */
    private static void runDelegatedTasks(final SSLEngineResult result, final SSLEngine engine) throws Exception
    {
        if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK)
        {
            Runnable runnable;

            while ((runnable = engine.getDelegatedTask()) != null)
            {
                log("\trunning delegated task...");
                runnable.run();
            }

            HandshakeStatus hsStatus = engine.getHandshakeStatus();

            if (hsStatus == HandshakeStatus.NEED_TASK)
            {
                throw new Exception("handshake shouldn't need additional tasks");
            }

            log("\tnew HandshakeStatus: " + hsStatus);
        }
    }

    /**
     *
     */
    private SSLEngine clientEngine;

    /**
     * read side of clientEngine
     */
    private ByteBuffer clientIn;

    /**
     * write side of clientEngine
     */
    private ByteBuffer clientOut;

    /**
     * For data transport, this example uses local ByteBuffers.<br>
     * This isn't really useful, but the purpose of this example is to show SSLEngine concepts,<br>
     * not how to do network transport.<br>
     * <br>
     * "reliable" transport client->server
     */
    private ByteBuffer cTOs;

    /**
     *
     */
    private SSLEngine serverEngine;

    /**
     * read side of serverEngine
     */
    private ByteBuffer serverIn;

    /**
     * write side of serverEngine
     */
    private ByteBuffer serverOut;

    /**
     *
     */
    private SSLContext sslc;

    /**
     * "reliable" transport server->client
     */
    private ByteBuffer sTOc;

    /**
     * Create an initialized SSLContext to use for this demo.
     *
     * @throws Exception Falls was schief geht.
     */
    public SSLEngineExample() throws Exception
    {
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");

        char[] passphrase = PASSWD.toCharArray();

        ks.load(getClass().getClassLoader().getResourceAsStream(KEYSTORE_FILE), passphrase);
        ts.load(getClass().getClassLoader().getResourceAsStream(TRUSTSTORE_FILE), passphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);

        SSLContext sslCtx = SSLContext.getInstance("TLS");

        sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        // TrustManager[] tm = new TrustManager[]
        // {
        // new X509TrustManager()
        // {
        // @Override
        // public void checkClientTrusted(final X509Certificate[] chain, final String authType)
        // {
        // log(" checkClientTrusted");
        // }
        //
        // @Override
        // public void checkServerTrusted(final X509Certificate[] chain, final String authType)
        // {
        // log(" checkServerTrusted");
        // }
        //
        // @Override
        // public X509Certificate[] getAcceptedIssuers()
        // {
        // return new X509Certificate[] {};
        // }
        // }
        // };
        //
        // SSLContext sslCtx = SSLContext.getInstance("TLS");
        // sslCtx.init(null, tm, new SecureRandom());

        this.sslc = sslCtx;
    }

    /**
     * Create and size the buffers appropriately.
     */
    private void createBuffers()
    {
        /*
         * We'll assume the buffer sizes are the same between client and server.
         */
        SSLSession session = this.clientEngine.getSession();
        int appBufferMax = session.getApplicationBufferSize();
        int netBufferMax = session.getPacketBufferSize();

        /*
         * We'll make the input buffers a bit bigger than the max needed size, so that unwrap()s following a successful data transfer won't generate
         * BUFFER_OVERFLOWS. We'll use a mix of direct and indirect ByteBuffers for tutorial purposes only. In reality, only use direct ByteBuffers when they
         * give a clear performance enhancement.
         */
        this.clientIn = ByteBuffer.allocate(appBufferMax + 50);
        this.serverIn = ByteBuffer.allocate(appBufferMax + 50);

        this.cTOs = ByteBuffer.allocateDirect(netBufferMax);
        this.sTOc = ByteBuffer.allocateDirect(netBufferMax);

        this.clientOut = ByteBuffer.wrap("Hi Server, I'm Client".getBytes());
        this.serverOut = ByteBuffer.wrap("Hello Client, I'm Server".getBytes());
    }

    /**
     * Using the SSLContext created during object creation, create/configure the SSLEngines we'll use for this demo.
     *
     * @throws Exception Falls was schief geht.
     */
    private void createSSLEngines() throws Exception
    {
        /*
         * Configure the serverEngine to act as a server in the SSL/TLS handshake. Also, require SSL client authentication.
         */
        this.serverEngine = this.sslc.createSSLEngine();
        this.serverEngine.setUseClientMode(false);
        this.serverEngine.setNeedClientAuth(true);

        /*
         * Similar to above, but using client mode instead.
         */
        this.clientEngine = this.sslc.createSSLEngine("client", 80);
        this.clientEngine.setUseClientMode(true);
    }

    /**
     * Run the demo. Sit in a tight loop, both engines calling wrap/unwrap regardless of whether data is available or not. We do this until both engines report
     * back they are closed. The main loop handles all of the I/O phases of the SSLEngine's lifetime: initial handshaking application data transfer engine
     * closing One could easily separate these phases into separate sections of code.
     *
     * @throws Exception Falls was schief geht.
     */
    private void runDemo() throws Exception
    {
        boolean dataDone = false;

        createSSLEngines();
        createBuffers();

        SSLEngineResult clientResult;   // results from client's last operation
        SSLEngineResult serverResult;   // results from server's last operation

        /*
         * Examining the SSLEngineResults could be much more involved, and may alter the overall flow of the application. For example, if we received a
         * BUFFER_OVERFLOW when trying to write to the output pipe, we could reallocate a larger pipe, but instead we wait for the peer to drain it.
         */
        for (int i = 0; i < 5000; i++)
        // while (!isEngineClosed(this.clientEngine) || !isEngineClosed(this.serverEngine))
        {
            if (isEngineClosed(this.clientEngine) || isEngineClosed(this.serverEngine))
            {
                break;
            }

            log("================");

            clientResult = this.clientEngine.wrap(this.clientOut, this.cTOs);
            log("client wrap: ", clientResult);
            runDelegatedTasks(clientResult, this.clientEngine);

            serverResult = this.serverEngine.wrap(this.serverOut, this.sTOc);
            log("server wrap: ", serverResult);
            runDelegatedTasks(serverResult, this.serverEngine);

            this.cTOs.flip();
            this.sTOc.flip();

            log("----");

            clientResult = this.clientEngine.unwrap(this.sTOc, this.clientIn);
            log("client unwrap: ", clientResult);
            runDelegatedTasks(clientResult, this.clientEngine);

            serverResult = this.serverEngine.unwrap(this.cTOs, this.serverIn);
            log("server unwrap: ", serverResult);
            runDelegatedTasks(serverResult, this.serverEngine);

            this.cTOs.compact();
            this.sTOc.compact();

            /*
             * After we've transfered all application data between the client and server, we close the clientEngine's outbound stream. This generates a
             * close_notify handshake message, which the server engine receives and responds by closing itself. In normal operation, each SSLEngine should call
             * closeOutbound(). To protect against truncation attacks, SSLEngine.closeInbound() should be called whenever it has determined that no more input
             * data will ever be available (say a closed input stream).
             */
            if (!dataDone && (this.clientOut.limit() == this.serverIn.position()) && (this.serverOut.limit() == this.clientIn.position()))
            {
                // A sanity check to ensure we got what was sent.
                checkTransfer(this.serverOut, this.clientIn);
                checkTransfer(this.clientOut, this.serverIn);

                log("\tClosing clientEngine's *OUTBOUND*...");
                this.clientEngine.closeOutbound();
                // serverEngine.closeOutbound();
                dataDone = true;
            }
        }
    }
}
