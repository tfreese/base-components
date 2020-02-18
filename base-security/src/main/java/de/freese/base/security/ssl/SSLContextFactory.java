/**
 * Created: 04.04.2012
 */

package de.freese.base.security.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Factory für einen {@link SSLContext}.
 *
 * @author Thomas Freese
 */
public final class SSLContextFactory
{
    /**
     *
     */
    public static final TrustManager[] X509_TRUST_ALL_MANAGER = new TrustManager[]
    {
            new javax.net.ssl.X509TrustManager()
            {
                /**
                 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
                 */
                @Override
                public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType)
                {
                    // Empty
                }

                /**
                 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
                 */
                @Override
                public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType)
                {
                    // Empty
                }

                /**
                 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
                 */
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
            }
    };

    // /**
    // * @return {@link SSLContext}
    // * @throws NoSuchAlgorithmException Falls was schief geht.
    // * @throws KeyManagementException Falls was schief geht.
    // * @throws NoSuchProviderException Falls was schief geht.
    // */
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
     * @param serverKeyStoreFile String
     * @param serverKeyStorePassword char[]
     * @param clientTrustStoreFile String
     * @param clientTrustStorePassword char[]
     * @param certPassword char[]
     * @return {@link SSLContext}
     * @throws NoSuchAlgorithmException Falls was schief geht.
     * @throws KeyManagementException Falls was schief geht.
     * @throws NoSuchProviderException Falls was schief geht.
     * @throws KeyStoreException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     * @throws FileNotFoundException Falls was schief geht.
     * @throws CertificateException Falls was schief geht.
     * @throws UnrecoverableKeyException Falls was schief geht.
     */
    public static SSLContext createTrusted(final String serverKeyStoreFile, final char[] serverKeyStorePassword, final String clientTrustStoreFile,
                                           final char[] clientTrustStorePassword, final char[] certPassword)
        throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException, KeyStoreException, CertificateException, FileNotFoundException,
        IOException, UnrecoverableKeyException
    {
        KeyStore serverKeyStore = KeyStore.getInstance("JKS", "SUN");
        KeyStore clientTrustStore = KeyStore.getInstance("JKS", "SUN");

        SSLContext sslContext = null;

        try (InputStream serverKeyStoreIS = new FileInputStream(serverKeyStoreFile);
             InputStream clientTrustStoreIS = new FileInputStream(clientTrustStoreFile))
        {
            serverKeyStore.load(serverKeyStoreIS, serverKeyStorePassword);
            clientTrustStore.load(clientTrustStoreIS, clientTrustStorePassword);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            trustManagerFactory.init(clientTrustStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            keyManagerFactory.init(serverKeyStore, certPassword);

            // SSLContext sslContext = SSLContext.getInstance("TLSv1", "SunJSSE");
            sslContext = SSLContext.getInstance("SSLv3", "SunJSSE");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        }

        return sslContext;
    }
}
