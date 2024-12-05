/*
 * Copyright 2012 The Netty Project The Netty Project licenses this file to you under the Apache License, version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at: http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package de.freese.base.net.ssl.bogus;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;

/**
 * Creates a bogus {@link SSLContext}.<br>
 * A client-side context created by this factory accepts any certificate even if it is invalid.<br>
 * A server-side context created by this factory sends a bogus certificate defined in {@link BogusSSLKeyStore}.<br>
 * <br>
 * You will have to create your context differently in a real world application.
 * <h3>Client Certificate Authentication</h3> To enable client certificate authentication:
 * <ul>
 * <li>Enable client authentication on the server side by calling {@link SSLEngine#setNeedClientAuth(boolean)} before creating
 * <code>org.jboss.netty.handler.ssl.SslHandler</code>.</li>
 * <li>When initializing an {@link SSLContext} on the client side, specify the {@link KeyManager} that contains the client certificate as the first argument of
 * {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)}.</li>
 * <li>When initializing an {@link SSLContext} on the server side, specify the proper {@link TrustManager} as the second argument of
 * {@link SSLContext#init(KeyManager[], TrustManager[], SecureRandom)} to validate the client certificate.</li>
 * </ul>
 *
 * @author Norman Maurer norman@apache.org
 * @author Thomas Freese
 */
public final class BogusSSLContextFactory {
    private static final SSLContext CLIENT_CONTEXT;
    private static final String PROTOCOL = "TLSv1.3";
    private static final SSLContext SERVER_CONTEXT;

    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");

        if (algorithm == null) {
            algorithm = "SunX509";
        }

        SSLContext serverContext = null;
        SSLContext clientContext = null;

        try {
            final KeyStore ks = KeyStore.getInstance("JKS");

            try (InputStream inputStream = BogusSSLKeyStore.asInputStream()) {
                ks.load(inputStream, BogusSSLKeyStore.getKeyStorePassword());
            }

            // Set up key manager factory to use our key store
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, BogusSSLKeyStore.getCertificatePassword());

            // Initialize the SSLContext to work with our key managers.
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), null, null);
        }
        catch (Exception ex) {
            throw new Error("Failed to initialize the server-side SSLContext", ex);
        }

        SERVER_CONTEXT = serverContext;

        try {
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(null, BogusSSLTrustManagerFactory.getTrustManagers(), null);
        }
        catch (Exception ex) {
            throw new Error("Failed to initialize the client-side SSLContext", ex);
        }

        CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getClientContext() {
        return CLIENT_CONTEXT;
    }

    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }

    private BogusSSLContextFactory() {
        super();
    }
}
