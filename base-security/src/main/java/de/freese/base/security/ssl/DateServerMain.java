// Created: 18.10.2005
package de.freese.base.security.ssl;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

/**
 * @author Thomas Freese
 */
public final class DateServerMain extends Thread {
    /**
     * @author Thomas Freese
     */
    private static final class Connect implements Runnable {
        private final Socket clientSocket;

        private OutputStream outputStream;

        private Connect(final Socket clientSocket) {
            super();

            this.clientSocket = clientSocket;

            try {
                this.outputStream = this.clientSocket.getOutputStream();
            }
            catch (Exception ex) {
                try {
                    this.clientSocket.close();
                }
                catch (Exception ex1) {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            if (this.clientSocket.isClosed() || this.outputStream == null) {
                return;
            }

            try {
                System.out.println("DateServerMain.Connect.run()");
                this.outputStream.write(LocalDateTime.now().toString().getBytes(StandardCharsets.UTF_8));
                this.outputStream.flush();

                // close streams and connections
                this.outputStream.close();
                this.clientSocket.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(final String[] argv) throws Exception {
        final DateServerMain server = new DateServerMain();
        server.start();
    }

    private final ServerSocket serverSocket;

    private DateServerMain() throws Exception {
        super();

        final boolean isSSL = true;
        final ServerSocketFactory serverSocketFactory;

        if (isSSL) {
            // final SSLContext sslContext = SslContextFactory.createDefault();
            final SSLContext sslContext =
                    SslContextFactory.createSslContext("CA/openssl/server_keystore.p12", "password".toCharArray(), "CA/openssl/client_truststore.p12", "password".toCharArray(),
                            "password".toCharArray());

            serverSocketFactory = sslContext.getServerSocketFactory();
        }
        else {
            serverSocketFactory = ServerSocketFactory.getDefault();
        }

        this.serverSocket = serverSocketFactory.createServerSocket(3000);

        if (this.serverSocket instanceof SSLServerSocket sslServerSocket) {
            sslServerSocket.setNeedClientAuth(true);
        }

        System.out.println("Server listening on port 3000.");
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Waiting for connections.");

            try {
                final Socket clientSocket = this.serverSocket.accept();
                System.out.println("Accepted a connection from: " + clientSocket.getInetAddress());

                final Runnable connect = new Connect(clientSocket);
                ForkJoinPool.commonPool().execute(connect);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
