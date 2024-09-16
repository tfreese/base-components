// Created: 18.10.2005
package de.freese.base.security.ssl;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

                // Close streams and connections.
                this.outputStream.close();
                this.clientSocket.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        final DateServerMain server = new DateServerMain();
        server.start();
    }

    private final ServerSocket serverSocket;

    private DateServerMain() throws Exception {
        super();

        final boolean useSSL = false;
        final ServerSocketFactory serverSocketFactory;

        if (useSSL) {
            // final SSLContext sslContext = SslContextFactory.createDefault();
            Path basePath = Path.of(System.getProperty("user.dir"));

            while (basePath != null && !basePath.endsWith("base-components")) {
                basePath = basePath.getParent();
            }

            basePath = basePath.resolve("base-security").resolve("CA").resolve("keytool");

            final SSLContext sslContext = new SslContextBuilder()
                    .keyStorePath(basePath.resolve("server_keystore.p12"))
                    .keyStorePassword("password".toCharArray())
                    .trustStorePath(basePath.resolve("client_truststore.p12"))
                    .trustStorePassword("password".toCharArray())
                    .certPassword("password".toCharArray())
                    .trustLocalHost(true)
                    .build();

            serverSocketFactory = sslContext.getServerSocketFactory();
        }
        else {
            serverSocketFactory = ServerSocketFactory.getDefault();
        }

        this.serverSocket = serverSocketFactory.createServerSocket(3333);

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
