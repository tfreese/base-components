// Created: 25 Mai 2024
package de.freese.base.security;

import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

/**
 * @author Thomas Freese
 */
public interface CryptoKeyPair extends Crypto {
    /**
     * {@link CipherInputStream#close()} is the Trigger for {@link Cipher#doFinal()}.
     */
    CipherInputStream decrypt(final InputStream inputStream) throws Exception;

    /**
     * {@link CipherOutputStream#close()} is the Trigger for {@link Cipher#doFinal()}.
     */
    CipherOutputStream encrypt(final OutputStream outputStream) throws Exception;
}
