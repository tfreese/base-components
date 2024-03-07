// Created: 04.04.2012
package de.freese.base.security.ssl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
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
    public static final TrustManager[] X509_TRUST_ALL_MANAGER = {new X509TrustManager() {
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
            return null;
        }
    }};

    // public static SSLContext createDefault()
    // throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException
    // {
    // // System.setProperty("javax.net.debug", "ssl");
    // // System.setProperty("https.protocols", "TLSv1");
    //
    // SSLContext sslContext = SSLContext.getDefault();
    // // SSLContext sslContext = SSLContext.getInstance("SSL");
    // // SSLContext sslContext = SSLContext.getInstance("TLSv1", "SunJSSE");
    // // sslContext.init(null, X509_TRUST_ALL_Manager, null);
    //
    // // HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    //
    // return sslContext;
    // }

    /**
     * HttpsURLConnection.setDefaultHostnameVerifier<br>
     * ClientBuilder.newBuilder().sslContext(createSslContext()).hostnameVerifier(createHostnameVerifier())
     */
    public static HostnameVerifier createHostnameVerifier1() {
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
     * KeyStore = Chain der Root-Authority und das eigene Zertifikat mit Private-Key.<br>
     * TrustStore = Public-Keys angeschlossener Systeme <br>
     * <br>
     * SSLContext.setDefault(sslContext);<br>
     * HttpsURLConnection.setDefaultSSLSocketFactory(SSLContext.getDefault().getSocketFactory());<br>
     * ClientBuilder.newBuilder().sslContext(createSslContext()).hostnameVerifier(createHostnameVerifier())<br>
     */
    public static SSLContext createSslContext(final String keyStoreFile, final char[] keyStorePassword, final String trustStoreFile, final char[] trustStorePassword,
                                              final char[] certPassword) throws Exception {
        // KeyStore.getInstance("JKS", "SUN")
        final KeyStore keyStore = KeyStore.getInstance("JKS");

        try (InputStream inputStream = new FileInputStream(keyStoreFile)) {
            keyStore.load(inputStream, keyStorePassword);
        }

        // KeyManagerFactory.getInstance("SunX509", "SunJSSE")
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, certPassword);

        final KeyStore trustStore = KeyStore.getInstance("JKS");

        try (InputStream inputStream = new FileInputStream(trustStoreFile)) {
            trustStore.load(inputStream, trustStorePassword);
        }

        // TrustManagerFactory.getInstance("SunX509", "SunJSSE")
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // Localhost immer vertrauen
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
                        return trustLocalHost ? null : x509TrustManager.getAcceptedIssuers();
                    }
                };
            }
        }

        // SSLContext.getInstance("TLSv1.3", "SunJSSE")
        final SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        return sslContext;
    }

    private SslContextFactory() {
        super();
    }
}
