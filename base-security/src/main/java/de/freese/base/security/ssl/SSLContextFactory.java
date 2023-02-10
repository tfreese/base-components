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
public final class SSLContextFactory {
    public static final TrustManager[] X509_TRUST_ALL_MANAGER = {new X509TrustManager() {
        /**
         * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
         */
        @Override
        public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
            // Empty
        }

        /**
         * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
         */
        @Override
        public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
            // Empty
        }

        /**
         * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
         */
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
        String invokeHost = "remotehost";
        boolean trustLocalHost = true;

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
    public static SSLContext createSslContext(final String keyStoreFile, final char[] keyStorePassword, final String trustStoreFile, final char[] trustStorePassword, final char[] certPassword) throws Exception {
        // KeyStore.getInstance("JKS", "SUN")
        KeyStore keyStore = KeyStore.getInstance("JKS");

        try (InputStream inputStream = new FileInputStream(keyStoreFile)) {
            keyStore.load(inputStream, keyStorePassword);
        }

        // KeyManagerFactory.getInstance("SunX509", "SunJSSE")
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, certPassword);

        KeyStore trustStore = KeyStore.getInstance("JKS");

        try (InputStream inputStream = new FileInputStream(trustStoreFile)) {
            trustStore.load(inputStream, trustStorePassword);
        }

        // TrustManagerFactory.getInstance("SunX509", "SunJSSE")
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // Localhost immer vertrauen
        boolean trustLocalHost = true;
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        for (int i = 0; i < trustManagers.length; i++) {
            final TrustManager tm = trustManagers[i];

            if (tm instanceof X509TrustManager) {
                trustManagers[i] = new X509TrustManager() {
                    /**
                     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
                     */
                    @Override
                    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                        if (!trustLocalHost) {
                            ((X509TrustManager) tm).checkClientTrusted(chain, authType);
                        }
                    }

                    /**
                     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
                     */
                    @Override
                    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                        if (!trustLocalHost) {
                            ((X509TrustManager) tm).checkServerTrusted(chain, authType);
                        }
                    }

                    /**
                     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
                     */
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return trustLocalHost ? null : ((X509TrustManager) tm).getAcceptedIssuers();
                    }
                };
            }
        }

        // SSLContext.getInstance("TLSv1.3", "SunJSSE")
        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        return sslContext;
    }

    private SSLContextFactory() {
        super();
    }
}
