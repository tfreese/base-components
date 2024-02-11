// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * @author Thomas Freese
 */
public class CryptoConfigAsymetric extends CryptoConfig<CryptoConfigAsymetric> {
    private KeyPair keyPair;

    CryptoConfigAsymetric() {
        super();
    }

    @Override
    public Crypto build() throws Exception {
        final CryptoAsymetric crypto = new CryptoAsymetric(this);

        // Key
        KeyPair theKeyPair = null;

        if (getKeyPair() != null) {
            theKeyPair = getKeyPair();
        }
        else if (getKeySize() > 0) {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(getAlgorithmKeyGenerator(), getProviderKeyGenerator());
            keyPairGenerator.initialize(getKeySize(), crypto.getSecureRandom());

            theKeyPair = keyPairGenerator.generateKeyPair();
        }
        else {
            throw new IllegalStateException("at least one the key parameter must be set: key, keyBytes, keyPassword, keySize");
        }

        crypto.setKeyPair(theKeyPair);

        return crypto;
    }

    public CryptoConfigAsymetric keyPair(final KeyPair keyPair) {
        this.keyPair = keyPair;

        return this;
    }

    protected KeyPair getKeyPair() {
        return this.keyPair;
    }
}
