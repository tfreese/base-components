// Created: 17 Apr. 2025
package de.freese.base.security.ssl;

import java.net.Socket;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

/**
 * @author Thomas Freese
 */
@SuppressWarnings("java:S4830")
public final class TrustAllTrustManager extends X509ExtendedTrustManager {
    @Override
    public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
        // Empty
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) {
        // Empty
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket) {
        // Empty
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
        // Empty
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket) {
        // Empty
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) {
        // Empty
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }
}
