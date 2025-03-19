// Created: 25 Mai 2024
package de.freese.base.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

/**
 * @author Thomas Freese
 */
public interface Crypto {
    String decrypt(String encrypted) throws GeneralSecurityException;

    /**
     * {@link CipherInputStream#close()} is the Trigger for {@link Cipher#doFinal()}.
     */
    CipherInputStream decrypt(InputStream inputStream) throws GeneralSecurityException, IOException;

    String encrypt(String message) throws GeneralSecurityException;

    /**
     * {@link CipherOutputStream#close()} is the Trigger for {@link Cipher#doFinal()}.
     */
    CipherOutputStream encrypt(OutputStream outputStream) throws GeneralSecurityException, IOException;

}
