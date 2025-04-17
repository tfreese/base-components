// Created: 17 Apr. 2025
package de.freese.base.security.ssl;

import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.X509KeyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * If {@link KeyStore} has multiple Certificates, choose this for the right Certificate to communicate.
 *
 * @author Thomas Freese
 */
public final class Alias509KeyManager implements X509KeyManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(Alias509KeyManager.class);

    private final String clientAlias;
    private final X509KeyManager delegate;

    public Alias509KeyManager(final X509KeyManager delegate, final String clientAlias) {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.clientAlias = Objects.requireNonNull(clientAlias, "clientAlias required");
    }

    @Override
    public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        boolean aliasFound = false;

        for (int i = 0; i < keyType.length && !aliasFound; i++) {
            final String[] validAliases = delegate.getClientAliases(keyType[i], issuers);

            if (validAliases == null) {
                continue;
            }

            for (String validAlias : validAliases) {
                if (validAlias.equals(clientAlias)) {
                    aliasFound = true;
                    break;
                }
            }
        }

        if (aliasFound) {
            return clientAlias;
        }

        LOGGER.warn("clientAlias not found: {}", clientAlias);

        return null;
    }

    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        return delegate.chooseServerAlias(keyType, issuers, socket);
    }

    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        return delegate.getCertificateChain(alias);
    }

    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return delegate.getClientAliases(keyType, issuers);
    }

    @Override
    public PrivateKey getPrivateKey(final String alias) {
        return delegate.getPrivateKey(alias);
    }

    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return delegate.getServerAliases(keyType, issuers);
    }
}
