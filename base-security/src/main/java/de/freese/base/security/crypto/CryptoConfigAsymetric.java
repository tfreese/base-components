// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Builder einer asymetrischen Public- / Private-Key Verschlüsselung der "java.security"-API.
 *
 * @author Thomas Freese
 */
public class CryptoConfigAsymetric extends CryptoConfig<CryptoConfigAsymetric>
{
    /**
    *
    */
    private KeyPair keyPair;

    /**
     * Erstellt ein neues {@link CryptoConfigAsymetric} Object.
     */
    CryptoConfigAsymetric()
    {
        super();
    }

    /**
     * @see de.freese.base.security.crypto.CryptoConfig#build()
     */
    @Override
    public Crypto build() throws Exception
    {
        CryptoAsymetric crypto = new CryptoAsymetric(this);

        // Key
        KeyPair theKeyPair = null;

        if (getKeyPair() != null)
        {
            theKeyPair = getKeyPair();
        }
        else if (getKeySize() > 0)
        {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(getAlgorythmKeyGenerator(), getProviderKeyGenerator());
            keyPairGenerator.initialize(getKeySize(), crypto.getSecureRandom());

            theKeyPair = keyPairGenerator.generateKeyPair();
        }
        else
        {
            throw new IllegalStateException("at least one the key parameter must be set: key, keyBytes, keyPassword, keySize");
        }

        crypto.setKeyPair(theKeyPair);

        return crypto;
    }

    /**
     * @return {@link KeyPair}
     */
    protected KeyPair getKeyPair()
    {
        return this.keyPair;
    }

    /**
     * @param keyPair {@link KeyPair}
     * @return {@link CryptoConfigAsymetric}
     */
    public CryptoConfigAsymetric keyPair(final KeyPair keyPair)
    {
        this.keyPair = keyPair;

        return this;
    }
}
