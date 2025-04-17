// Created: 16 Sept. 2024
package de.freese.base.security.ssl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.function.Supplier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Thomas Freese
 */
public final class SslContextBuilder {

    /**
     * HttpsURLConnection.setDefaultHostnameVerifier<br>
     * ClientBuilder.newBuilder().sslContext(createSslContext()).hostnameVerifier(createHostnameVerifier())
     */
    private static HostnameVerifier createHostnameVerifier(final boolean trustLocalHost) {
        final HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

        return (hostname, session) -> {
            if (trustLocalHost && hostname.contains("localhost") || hostname.contains("remotehost")) {
                return true;
            }

            return defaultHostnameVerifier.verify(hostname, session);
        };
    }

    private Supplier<char[]> certPasswordSupplier;
    private String clientAlias;
    private Supplier<char[]> keyStorePasswordSupplier;
    private Path keyStorePath;
    private boolean trustLocalHost;
    private Supplier<char[]> trustStorePasswordSupplier;
    private Path trustStorePath;

    /**
     * KeyStore = Chain of the Root-Authority and the own Certificate with Private-Key.<br>
     * TrustStore = Public-Keys trusted Systems<br>
     * <br>
     * SSLContext.setDefault(sslContext);<br>
     * HttpsURLConnection.setDefaultSSLSocketFactory(SSLContext.getDefault().getSocketFactory());<br>
     * ClientBuilder.newBuilder().sslContext(createSslContext()).hostnameVerifier(createHostnameVerifier())<br>
     */
    public SSLContext build() throws Exception {
        final KeyStore keyStore;

        if (keyStorePath.endsWith(".p12")) {
            keyStore = KeyStore.getInstance("PKCS12");
        }
        else {
            keyStore = KeyStore.getInstance("JKS");
        }

        try (InputStream inputStream = Files.newInputStream(keyStorePath)) {
            keyStore.load(inputStream, keyStorePasswordSupplier.get());
        }

        // KeyManagerFactory.getInstance("SunX509", "SunJSSE")
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, certPasswordSupplier.get());

        final KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        if (clientAlias != null && !clientAlias.isBlank()) {
            for (int i = 0; i < keyManagers.length; i++) {
                if (keyManagers[i] instanceof X509KeyManager x509KeyManager) {
                    keyManagers[i] = new Alias509KeyManager(x509KeyManager, clientAlias);
                }
            }
        }

        final KeyStore trustStore;

        if (keyStorePath.endsWith(".p12")) {
            trustStore = KeyStore.getInstance("PKCS12");
        }
        else {
            trustStore = KeyStore.getInstance("JKS");
        }

        try (InputStream inputStream = Files.newInputStream(trustStorePath)) {
            trustStore.load(inputStream, trustStorePasswordSupplier.get());
        }

        // TrustManagerFactory.getInstance("SunX509", "SunJSSE")
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        if (trustLocalHost) {
            for (int i = 0; i < trustManagers.length; i++) {
                if (trustManagers[i] instanceof X509TrustManager x509TrustManager) {
                    trustManagers[i] = new TrustLocalHostTrustManager(x509TrustManager, trustLocalHost);
                }
            }
        }

        final SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(keyManagers, trustManagers, null);

        // SSLContext.setDefault(sslContext);
        // HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        // HttpsURLConnection.setDefaultHostnameVerifier(createHostnameVerifier(trustLocalHost));

        return sslContext;
    }

    public SSLContext buildDefault() throws Exception {
        // System.setProperty("javax.net.debug", "ssl");
        // System.setProperty("https.protocols", "SSL");

        final TrustManager[] trustManagers = {new TrustAllTrustManager()};

        // final SSLContext sslContext = SSLContext.getDefault();
        final SSLContext sslContext = SSLContext.getInstance("TLS"); // SSL, TLS
        sslContext.init(null, trustManagers, SecureRandom.getInstanceStrong());

        SSLContext.setDefault(sslContext);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(createHostnameVerifier(trustLocalHost));

        return sslContext;
    }

    public SslContextBuilder certPassword(final Supplier<char[]> certPasswordSupplier) {
        this.certPasswordSupplier = Objects.requireNonNull(certPasswordSupplier, "certPasswordSupplier required");

        return this;
    }

    public SslContextBuilder clientAlias(final String clientAlias) {
        this.clientAlias = Objects.requireNonNull(clientAlias, "clientAlias required");

        return this;
    }

    public SslContextBuilder keyStorePassword(final Supplier<char[]> keyStorePasswordSupplier) {
        this.keyStorePasswordSupplier = Objects.requireNonNull(keyStorePasswordSupplier, "keyStorePasswordSupplier required");

        return this;
    }

    public SslContextBuilder keyStorePath(final Path keyStorePath) {
        this.keyStorePath = Objects.requireNonNull(keyStorePath, "keyStorePath required");

        return this;
    }

    public SslContextBuilder trustLocalHost(final boolean trustLocalHost) {
        this.trustLocalHost = trustLocalHost;

        return this;
    }

    public SslContextBuilder trustStorePassword(final Supplier<char[]> trustStorePasswordSupplier) {
        this.trustStorePasswordSupplier = Objects.requireNonNull(trustStorePasswordSupplier, "trustStorePasswordSupplier required");

        return this;
    }

    public SslContextBuilder trustStorePath(final Path trustStorePath) {
        this.trustStorePath = Objects.requireNonNull(trustStorePath, "trustStorePath required");

        return this;
    }
}
