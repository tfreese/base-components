// Created: 18.10.2005
package de.freese.base.security.ssl;

import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class DateClientMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateClientMain.class);

    public static void main(final String[] args) {
        try {
            final boolean useSSL = false;
            final SocketFactory socketFactory;

            if (useSSL) {
                // final SSLContext sslContext = SslContextFactory.createDefault();
                Path basePath = Path.of(System.getProperty("user.dir"));

                while (basePath != null && !basePath.endsWith("base-components")) {
                    basePath = basePath.getParent();
                }

                basePath = basePath.resolve("base-security").resolve("CA").resolve("keytool");

                final SSLContext sslContext = new SslContextBuilder()
                        .keyStorePath(basePath.resolve("client_keystore.p12"))
                        .keyStorePassword("password"::toCharArray)
                        .trustStorePath(basePath.resolve("server_truststore.p12"))
                        .trustStorePassword("password"::toCharArray)
                        .certPassword("password"::toCharArray)
                        .trustLocalHost(true)
                        .build();

                socketFactory = sslContext.getSocketFactory();
            }
            else {
                socketFactory = SocketFactory.getDefault();
            }

            try (Socket socket = socketFactory.createSocket("localhost", 3333)) {
                if (socket instanceof SSLSocket sslSocket) {
                    sslSocket.startHandshake();

                    final SSLSession session = sslSocket.getSession();
                    LOGGER.info("Cipher suite in use is {}", session.getCipherSuite());
                    LOGGER.info("Protocol is {}", session.getProtocol());
                }

                // get the input and output streams from the SSL connection
                try (InputStream inputStream = socket.getInputStream()) {
                    final String response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    LOGGER.info("The response is: {}", response);
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(-1);
        }

        System.exit(0);
    }

    private DateClientMain() {
        super();
    }
}
