// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Crypto-Interface zum Ver- und Entschlüsseln.
 *
 * @author Thomas Freese
 */
public interface Crypto {
    byte[] decrypt(byte[] bytes) throws Exception;

    /**
     * @param out {@link OutputStream}; Entschlüsselt
     */
    void decrypt(InputStream in, OutputStream out) throws Exception;

    byte[] digest(byte[] bytes) throws Exception;

    /**
     * @param in {@link InputStream}, Verschlüsselt
     */
    byte[] digest(InputStream in) throws Exception;

    byte[] encrypt(byte[] bytes) throws Exception;

    /**
     * @param out {@link OutputStream}; Verschlüsselt
     */
    void encrypt(InputStream in, OutputStream out) throws Exception;

    /**
     * @param in {@link InputStream}, Verschlüsselt
     */
    void sign(InputStream in, OutputStream out) throws Exception;

    /**
     * @param in {@link InputStream}; Verschlüsselt
     */
    boolean verify(InputStream in, InputStream signIn) throws Exception;
}
