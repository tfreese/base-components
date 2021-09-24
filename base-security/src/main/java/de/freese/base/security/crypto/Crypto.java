// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Crypto-Interface zum Ver- und Entschlüsseln.
 *
 * @author Thomas Freese
 */
public interface Crypto
{
    /**
     * Entschlüsselt ein byte[].
     *
     * @param bytes byte[]
     *
     * @return byte[]
     *
     * @throws Exception Falls was schief geht.
     */
    byte[] decrypt(byte[] bytes) throws Exception;

    /**
     * Entschlüsselt den InputStream.<br>
     *
     * @param in {@link InputStream}
     * @param out {@link OutputStream}; Entschlüsselt
     *
     * @throws Exception Falls was schief geht.
     */
    void decrypt(InputStream in, OutputStream out) throws Exception;

    /**
     * Erstellt eine Prüfsumme der Bytes.<br>
     *
     * @param bytes byte[]
     *
     * @return byte[]
     *
     * @throws Exception Falls was schief geht.
     */
    byte[] digest(byte[] bytes) throws Exception;

    /**
     * Erstellt eine Prüfsumme des InputStreams.<br>
     *
     * @param in {@link InputStream}, Verschlüsselt
     *
     * @return byte[]
     *
     * @throws Exception Falls was schief geht.
     */
    byte[] digest(final InputStream in) throws Exception;

    /**
     * Verschlüsselt ein byte[].
     *
     * @param bytes byte[]
     *
     * @return byte[]
     *
     * @throws Exception Falls was schief geht.
     */
    byte[] encrypt(byte[] bytes) throws Exception;

    /**
     * Verschlüsselt den InputStream.<br>
     *
     * @param in {@link InputStream}
     * @param out {@link OutputStream}; Verschlüsselt
     *
     * @throws Exception Falls was schief geht.
     */
    void encrypt(InputStream in, OutputStream out) throws Exception;

    /**
     * Signiert den InputStream.<br>
     *
     * @param in {@link InputStream}, Verschlüsselt
     * @param out {@link OutputStream};
     *
     * @throws Exception Falls was schief geht.
     */
    void sign(InputStream in, OutputStream out) throws Exception;

    /**
     * Verifiziert den InputStream.<br>
     *
     * @param in {@link InputStream}; Verschlüsselt
     * @param signIn {@link InputStream}
     *
     * @return boolean
     *
     * @throws Exception Falls was schief geht.
     */
    boolean verify(InputStream in, InputStream signIn) throws Exception;
}
