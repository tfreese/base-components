// Created: 04.04.2012
package de.freese.base.security.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Factory für einen {@link SSLContext}.
 *
 * @author Thomas Freese
 */
public final class SslContextFactory {
    private static final TrustManager[] X509_TRUST_ALL_MANAGER = {new X509TrustManager() {
        @Override
        public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
            // Empty
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
            // Empty
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }};

    public static SSLContext createDefault() throws GeneralSecurityException {
        // System.setProperty("javax.net.debug", "ssl");
        // System.setProperty("https.protocols", "SSL");

        // final SSLContext sslContext = SSLContext.getDefault();
        final SSLContext sslContext = SSLContext.getInstance("TLS"); // SSL, TLS
        sslContext.init(null, X509_TRUST_ALL_MANAGER, SecureRandom.getInstanceStrong());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(createHostnameVerifier());

        return sslContext;
    }

    /**
     * HttpsURLConnection.setDefaultHostnameVerifier<br>
     * ClientBuilder.newBuilder().sslContext(createSslContext()).hostnameVerifier(createHostnameVerifier())
     */
    public static HostnameVerifier createHostnameVerifier() {
        final String invokeHost = "remotehost";
        final boolean trustLocalHost = true;

        final HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();

        return (hostname, session) -> {
            // Localhost immer vertrauen
            if ((trustLocalHost && hostname.contains("localhost")) || hostname.contains(invokeHost)) {
                return true;
            }

            return defaultHostnameVerifier.verify(hostname, session);
        };
    }

    /**
     * KeyStore = Chain of the Root-Authority and the own Certificate with Private-Key.<br>
     * TrustStore = Public-Keys trusted Systems<br>
     * <br>
     * SSLContext.setDefault(sslContext);<br>
     * HttpsURLConnection.setDefaultSSLSocketFactory(SSLContext.getDefault().getSocketFactory());<br>
     * ClientBuilder.newBuilder().sslContext(createSslContext()).hostnameVerifier(createHostnameVerifier())<br>
     */
    public static SSLContext createSslContext(final String keyStoreFile, final char[] keyStorePassword, final String trustStoreFile, final char[] trustStorePassword,
                                              final char[] certPassword) throws GeneralSecurityException, IOException {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12"); // PKCS12, JKS

        try (InputStream inputStream = new FileInputStream(keyStoreFile)) {
            keyStore.load(inputStream, keyStorePassword);
        }

        // KeyManagerFactory.getInstance("SunX509", "SunJSSE")
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, certPassword);

        final KeyStore trustStore = KeyStore.getInstance("PKCS12"); // // PKCS12, JKS

        try (InputStream inputStream = new FileInputStream(trustStoreFile)) {
            trustStore.load(inputStream, trustStorePassword);
        }

        // TrustManagerFactory.getInstance("SunX509", "SunJSSE")
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // Trust Localhost.
        final boolean trustLocalHost = true;
        final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        for (int i = 0; i < trustManagers.length; i++) {
            final TrustManager tm = trustManagers[i];

            if (tm instanceof X509TrustManager x509TrustManager) {
                trustManagers[i] = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                        if (!trustLocalHost) {
                            x509TrustManager.checkClientTrusted(chain, authType);
                        }
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

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(createHostnameVerifier());

        return sslContext;
    }

    private SslContextFactory() {
        super();
    }
}
