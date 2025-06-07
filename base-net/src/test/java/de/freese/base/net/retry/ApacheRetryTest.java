// Created: 29 März 2025
package de.freese.base.net.retry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Set;

import javax.net.ssl.SSLException;

import com.sun.net.httpserver.HttpServer;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.TimeValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.utils.SocketUtils;

/**
 * @author Thomas Freese
 */
class ApacheRetryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApacheRetryTest.class);

    private static CloseableHttpClient closeableHttpClient;
    private static HttpServer httpServer;
    private static URI uri;

    private static class MyDefaultHttpRequestRetryStrategy extends DefaultHttpRequestRetryStrategy {
        private static final Set<Class<? extends IOException>> NON_RETRIABLE_EXCEPTIONS = Set.of(
                // InterruptedIOException.class, (SocketTimeoutException)
                UnknownHostException.class,
                ConnectException.class,
                ConnectionClosedException.class,
                NoRouteToHostException.class,
                SSLException.class);

        private static final Set<Integer> RETRIABLE_CODES = Set.of(
                HttpStatus.SC_TOO_MANY_REQUESTS,
                HttpStatus.SC_SERVICE_UNAVAILABLE,
                HttpStatus.SC_INTERNAL_SERVER_ERROR
        );

        MyDefaultHttpRequestRetryStrategy(final int maxRetries, final TimeValue defaultRetryInterval) {
            super(maxRetries, defaultRetryInterval, NON_RETRIABLE_EXCEPTIONS, RETRIABLE_CODES);
        }

        @Override
        public TimeValue getRetryInterval(final HttpRequest request, final IOException exception, final int execCount, final HttpContext context) {
            LOGGER.info("{}: {}", request, exception.getMessage());

            return super.getRetryInterval(request, exception, execCount, context);
        }

        @Override
        public TimeValue getRetryInterval(final HttpResponse response, final int execCount, final HttpContext context) {
            if (context instanceof HttpClientContext hcc) {
                LOGGER.info("{}: {}", hcc.getRequest(), response);
            }
            else {
                LOGGER.info("{}", response);
            }

            return super.getRetryInterval(response, execCount, context);
        }

        @Override
        protected boolean handleAsIdempotent(final HttpRequest request) {
            // Retry if the request is considered idempotent.
            //
            // Before our retries customization, we need to elaborate a bit on the idempotency of requests.
            // It is important since the Apache HTTP client considers all HttpEntityEnclosingRequest implementations non-idempotent.
            // Common implementations of this interface are HttpPost, HttpPut, and HttpPatch classes.
            // So, our PATCH and PUT requests will not be, by default, retried!
            return super.handleAsIdempotent(request);
        }
    }

    @AfterAll
    static void afterAll() {
        closeableHttpClient.close(CloseMode.GRACEFUL);
        httpServer.stop(0);
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        final int port = SocketUtils.findAvailableTcpPort();
        uri = URI.create("http://localhost:" + port);
        httpServer = RetryTestUtil.startHttpServer(port);

        closeableHttpClient = HttpClients.custom()
                // .addExecInterceptorFirst("retry", (request, scope, chain) -> {
                //     LOGGER.info("{}", request);
                //     return chain.proceed(request, scope);
                // })
                // .addRequestInterceptorFirst((request, entity, context) -> LOGGER.info("{}", request))
                // .addResponseInterceptorFirst((response, entity, context) -> LOGGER.info("{}", response))
                // .disableAutomaticRetries() // Default active with 1 Retry.
                .setRetryStrategy(new MyDefaultHttpRequestRetryStrategy(2, TimeValue.ofSeconds(1)))
                .build();
    }

    @Test
    void testTimeout() {
        // HttpEntities
        final ClassicHttpRequest httpRequest = ClassicRequestBuilder
                .get(uri)
                .build();

        try {
            closeableHttpClient.execute(httpRequest, response -> {
                assertNotNull(response);
                assertEquals(HttpURLConnection.HTTP_UNAVAILABLE, response.getCode());
                assertEquals("ERROR", EntityUtils.toString(response.getEntity()));

                // Executed automatically in CloseableHttpClient.execute(HttpHost, ClassicHttpRequest, HttpContext, HttpClientResponseHandler<? extends T>) .
                // EntityUtils.consume(response.getEntity());

                return null;
            });
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);

            assertEquals(IOException.class, ex.getClass());
        }
    }
}
