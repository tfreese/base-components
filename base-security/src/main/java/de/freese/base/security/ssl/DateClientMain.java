// Created: 18.10.2005
package de.freese.base.security.ssl;

import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * @author Thomas Freese
 */
public final class DateClientMain {
    public static void main(final String[] argv) {
        try {
            final boolean isSSL = true;
            final SocketFactory socketFactory;

            if (isSSL) {
                // final SSLContext sslContext = SslContextFactory.createDefault();
                final SSLContext sslContext =
                        SslContextFactory.createSslContext("CA/openssl/client_keystore.p12", "password".toCharArray(), "CA/openssl/server_truststore.p12", "password".toCharArray(),
                                "password".toCharArray());

                socketFactory = sslContext.getSocketFactory();
            }
            else {
                socketFactory = SocketFactory.getDefault();
            }

            try (Socket socket = socketFactory.createSocket("localhost", 3000)) {
                if (socket instanceof SSLSocket sslSocket) {
                    sslSocket.startHandshake();

                    final SSLSession session = sslSocket.getSession();
                    System.out.println("Cipher suite in use is " + session.getCipherSuite());
                    System.out.println("Protocol is " + session.getProtocol());
                }

                // get the input and output streams from the SSL connection
                try (InputStream inputStream = socket.getInputStream()) {
                    final String response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.print("The response is: " + response);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        System.exit(0);
    }

    private DateClientMain() {
        super();
    }
}
