// Created: 29 März 2025
package de.freese.base.net.retry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import de.freese.base.core.concurrent.NamedThreadFactory;

/**
 * @author Thomas Freese
 */
public final class RetryTestUtil {
    // https://httpstat.us/500"
    static HttpServer startHttpServer(final int port) throws IOException {
        final HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(Executors.newSingleThreadExecutor(new NamedThreadFactory("jre-httpserver")));

        httpServer.createContext("/", httpExchange -> {
            // await().pollDelay(sleepDuration).until(() -> true);

            final byte[] bytes = "ERROR".getBytes(StandardCharsets.UTF_8);

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAVAILABLE, bytes.length);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(bytes);

                os.flush();
            }
        });

        httpServer.start();

        return httpServer;
    }

    private RetryTestUtil() {
        super();
    }
}
