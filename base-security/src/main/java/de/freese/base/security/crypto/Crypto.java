// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Thomas Freese
 */
public interface Crypto {
    byte[] decrypt(byte[] bytes) throws Exception;

    /**
     * @param out {@link OutputStream}; Decrypted
     */
    void decrypt(InputStream in, OutputStream out) throws Exception;

    byte[] digest(byte[] bytes) throws Exception;

    /**
     * @param in {@link InputStream}, Encrypted
     */
    byte[] digest(InputStream in) throws Exception;

    byte[] encrypt(byte[] bytes) throws Exception;

    /**
     * @param out {@link OutputStream}; Encrypted
     */
    void encrypt(InputStream in, OutputStream out) throws Exception;

    /**
     * @param in {@link InputStream}, Encrypted
     */
    void sign(InputStream in, OutputStream out) throws Exception;

    /**
     * @param in {@link InputStream}; Encrypted
     */
    boolean verify(InputStream in, InputStream signIn) throws Exception;
}
