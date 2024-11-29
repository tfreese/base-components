// Created: 16 Sept. 2024
package de.freese.base.security.ssl;

import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Thomas Freese
 */
public final class SslContextBuilder {
    @SuppressWarnings("java:S4830")
    private static final TrustManager[] X509_TRUST_ALL_MANAGER = {new X509ExtendedTrustManager() {
        @Override
        public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
            // Empty
        }

        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) {
            checkClientTrusted(chain, authType);
        }

        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket) {
            checkClientTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
            // Empty
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket) {
            checkServerTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) {
            checkServerTrusted(chain, authType);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }
    };

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

    private char[] certPassword;
    private char[] keyStorePassword;
    private Path keyStorePath;
    private boolean trustLocalHost;
    private char[] trustStorePassword;
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
            keyStore.load(inputStream, keyStorePassword);
        }

        // KeyManagerFactory.getInstance("SunX509", "SunJSSE")
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, certPassword);

        final KeyStore trustStore;

        if (keyStorePath.endsWith(".p12")) {
            trustStore = KeyStore.getInstance("PKCS12");
        }
        else {
            trustStore = KeyStore.getInstance("JKS");
        }

        try (InputStream inputStream = Files.newInputStream(trustStorePath)) {
            trustStore.load(inputStream, trustStorePassword);
        }

        // TrustManagerFactory.getInstance("SunX509", "SunJSSE")
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        for (int i = 0; i < trustManagers.length; i++) {
            if (trustManagers[i] instanceof X509TrustManager x509TrustManager) {
                trustManagers[i] = new X509ExtendedTrustManager() {
                    @Override
                    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                        if (!trustLocalHost) {
                            x509TrustManager.checkClientTrusted(chain, authType);
                        }
                    }

                    @Override
                    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
                        checkClientTrusted(chain, authType);
                    }

                    @Override
                    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
                        checkClientTrusted(chain, authType);
                    }

                    @Override
                    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
                        checkServerTrusted(chain, authType);
                    }

                    @Override
                    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
                        checkServerTrusted(chain, authType);
                    }

                    @Override
                    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                        if (!trustLocalHost) {
                            x509TrustManager.checkServerTrusted(chain, authType);
                        }
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return trustLocalHost ? new X509Certificate[]{} : x509TrustManager.getAcceptedIssuers();
                    }
                };
            }
        }

        final SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        // SSLContext.setDefault(sslContext);
        // HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        // HttpsURLConnection.setDefaultHostnameVerifier(createHostnameVerifier(trustLocalHost));

        return sslContext;
    }

    public SSLContext buildDefault() throws Exception {
        // System.setProperty("javax.net.debug", "ssl");
        // System.setProperty("https.protocols", "SSL");

        // final SSLContext sslContext = SSLContext.getDefault();
        final SSLContext sslContext = SSLContext.getInstance("TLS"); // SSL, TLS
        sslContext.init(null, X509_TRUST_ALL_MANAGER, SecureRandom.getInstanceStrong());

        SSLContext.setDefault(sslContext);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(createHostnameVerifier(trustLocalHost));

        return sslContext;
    }

    public SslContextBuilder certPassword(final char[] certPassword) {
        this.certPassword = certPassword;

        return this;
    }

    public SslContextBuilder keyStorePassword(final char[] keyStorePassword) {
        this.keyStorePassword = keyStorePassword;

        return this;
    }

    public SslContextBuilder keyStorePath(final Path keyStorePath) {
        this.keyStorePath = keyStorePath;

        return this;
    }

    public SslContextBuilder trustLocalHost(final boolean trustLocalHost) {
        this.trustLocalHost = trustLocalHost;

        return this;
    }

    public SslContextBuilder trustStorePassword(final char[] trustStorePassword) {
        this.trustStorePassword = trustStorePassword;

        return this;
    }

    public SslContextBuilder trustStorePath(final Path trustStorePath) {
        this.trustStorePath = trustStorePath;

        return this;
    }
}
