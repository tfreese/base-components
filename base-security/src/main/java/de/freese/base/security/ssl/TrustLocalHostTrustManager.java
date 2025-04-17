// Created: 17 Apr. 2025
package de.freese.base.security.ssl;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Thomas Freese
 */
public final class TrustLocalHostTrustManager extends X509ExtendedTrustManager {
    private final X509TrustManager delegate;
    private final boolean trustLocalHost;

    public TrustLocalHostTrustManager(final X509TrustManager delegate, final boolean trustLocalHost) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.trustLocalHost = trustLocalHost;
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        if (trustLocalHost) {
            return;
        }

        delegate.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
        if (trustLocalHost) {
            return;
        }

        if (delegate instanceof X509ExtendedTrustManager ext) {
            ext.checkClientTrusted(chain, authType, engine);
        }
        else {
            delegate.checkClientTrusted(chain, authType);
        }
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
        if (trustLocalHost) {
            return;
        }

        if (delegate instanceof X509ExtendedTrustManager ext) {
            ext.checkClientTrusted(chain, authType, socket);
        }
        else {
            delegate.checkClientTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        if (trustLocalHost) {
            return;
        }

        delegate.checkServerTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
        if (trustLocalHost) {
            return;
        }

        if (delegate instanceof X509ExtendedTrustManager ext) {
            ext.checkServerTrusted(chain, authType, socket);
        }
        else {
            delegate.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
        if (trustLocalHost) {
            return;
        }

        if (delegate instanceof X509ExtendedTrustManager ext) {
            ext.checkServerTrusted(chain, authType, engine);
        }
        else {
            delegate.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return trustLocalHost ? new X509Certificate[]{} : delegate.getAcceptedIssuers();
    }
}
