/*
 * Created on 06.12.2004
 */
package de.freese.base.security.algorythm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

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
     * @return byte[]
     * @throws GeneralSecurityException Falls was schief geht.
     */
    public byte[] decrypt(byte[] bytes) throws GeneralSecurityException;

    /**
     * Entschlüsselt den InputStream.<br>
     * 
     * @param in {@link InputStream}
     * @param out {@link OutputStream}; Entschlüsselt
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public void decrypt(InputStream in, OutputStream out) throws GeneralSecurityException, IOException;

    /**
     * Erstellt eine Prüfsumme der Bytes.<br>
     * 
     * @param bytes byte[]
     * @return byte[]
     * @throws GeneralSecurityException Falls was schief geht.
     */
    public byte[] digest(byte[] bytes) throws GeneralSecurityException;

    /**
     * Erstellt eine Prüfsumme des InputStreams.<br>
     *
     * @param in {@link InputStream}, Verschlüsselt
     * @return byte[]
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public byte[] digest(final InputStream in) throws GeneralSecurityException, IOException;

    /**
     * Verschlüsselt ein byte[].
     * 
     * @param bytes byte[]
     * @return byte[]
     * @throws GeneralSecurityException Falls was schief geht.
     */
    public byte[] encrypt(byte[] bytes) throws GeneralSecurityException;

    /**
     * Verschlüsselt den InputStream.<br>
     * 
     * @param in {@link InputStream}
     * @param out {@link OutputStream}; Verschlüsselt
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public void encrypt(InputStream in, OutputStream out) throws GeneralSecurityException, IOException;

    /**
     * Signiert den InputStream.<br>
     * 
     * @param in {@link InputStream}, Verschlüsselt
     * @param out {@link OutputStream};
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public void sign(InputStream in, OutputStream out) throws GeneralSecurityException, IOException;

    /**
     * Verifiziert den InputStream.<br>
     * 
     * @param in {@link InputStream}; Verschlüsselt
     * @param signIn {@link InputStream}
     * @return boolean
     * @throws GeneralSecurityException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    public boolean verify(InputStream in, InputStream signIn) throws GeneralSecurityException, IOException;
}
