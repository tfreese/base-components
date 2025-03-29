// Created: 29 März 2025
package de.freese.base.net.retry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;

import com.sun.net.httpserver.HttpServer;
import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeException;
import dev.failsafe.FailsafeExecutor;
import dev.failsafe.RetryPolicy;
import dev.failsafe.function.CheckedSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.utils.SocketUtils;

/**
 * @author Thomas Freese
 */
class FailsafeRetryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FailsafeRetryTest.class);

    private static FailsafeExecutor<HttpResponse<String>> failsafeExecutor;
    private static HttpServer httpServer;
    private static URI uri;

    @AfterAll
    static void afterAll() {
        httpServer.stop(0);
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        final int port = SocketUtils.findAvailableTcpPort();
        uri = URI.create("http://localhost:" + port + "/");
        httpServer = RetryTestUtil.startHttpServer(port);

        final RetryPolicy<HttpResponse<String>> retryPolicy = RetryPolicy.<HttpResponse<String>>builder()
                .withMaxRetries(2)
                .withDelay(Duration.ofSeconds(1L))
                .onRetry(event -> {
                    final Throwable throwable = event.getLastException();

                    if (throwable != null) {
                        final String error = Optional.ofNullable(throwable.getMessage()).orElse(throwable.getClass().getSimpleName());
                        LOGGER.warn("retry: {} - {}", event.getExecutionCount(), error);
                    }
                    else {
                        LOGGER.warn("retry: {}", event.getExecutionCount());
                    }
                })
                .onFailure(event -> {
                    final Throwable throwable = event.getException();

                    if (throwable != null) {
                        LOGGER.error(throwable.getMessage(), throwable);
                    }
                    else {
                        LOGGER.error(event.toString());
                    }
                })
                .build();
        failsafeExecutor = Failsafe.with(retryPolicy);
    }

    @Test
    void testTimeout() {
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            final HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();

            final CheckedSupplier<HttpResponse<String>> checkedSupplier = () -> {
                final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException("status = " + response.statusCode());
                }

                return response;
            };
            final Callable<HttpResponse<String>> callable = () -> failsafeExecutor.get(checkedSupplier);
            final HttpResponse<String> httpResponse = callable.call();

            assertNotNull(httpResponse);
            assertEquals(HttpURLConnection.HTTP_UNAVAILABLE, httpResponse.statusCode());
            assertEquals("ERROR", httpResponse.body());
        }
        catch (FailsafeException ex) {
            LOGGER.error(ex.getMessage(), ex);

            assertEquals(IOException.class, ex.getCause().getClass());
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);

            assertEquals(IOException.class, ex.getClass());
        }
    }
}
