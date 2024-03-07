// Created: 07.03.24
package de.freese.base.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.NoSuchPaddingException;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
public class AsymetricCrypto extends AbstractCrypto {
    /**
     * @param publicKey {@link PublicKey}; required for encryption
     * @param privateKey {@link PrivateKey; required for decryption
     */
    public AsymetricCrypto(final PublicKey publicKey, final PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        super(publicKey, privateKey);
    }
}
