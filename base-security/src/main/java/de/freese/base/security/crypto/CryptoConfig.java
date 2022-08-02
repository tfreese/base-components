// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Signature;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * Builder für die Konfiguration einer Verschlüsselung der "java.security"-API.
 *
 * @param <T> Type
 *
 * @author Thomas Freese
 */
public abstract class CryptoConfig<T extends CryptoConfig<T>>
{
    /**
     * Builder einer asymmetrischen Public- / Private-Key Verschlüsselung der "java.security"-API.
     *
     * @return {@link CryptoConfigAsymetric}
     */
    public static CryptoConfigAsymetric asymetric()
    {
        return new CryptoConfigAsymetric();
    }

    /**
     * Builder einer symmetrischen PasswordBasedEncryption (PBE) der "java.security"-API.
     *
     * @return {@link CryptoConfigSymetric}
     */
    public static CryptoConfigSymetric symetric()
    {
        return new CryptoConfigSymetric();
    }

    /**
     * Algorithmus für {@link Cipher}.
     */
    private String algorythmCipher;

    /**
     * Algorithmus als Default.
     */
    private String algorythmDefault;

    /**
     * Algorithmus für {@link MessageDigest}.
     */
    private String algorythmDigest;

    /**
     * Algorithmus für {@link KeyGenerator} oder {@link KeyPairGenerator}.
     */
    private String algorythmKeyGenerator;

    /**
     * Algorithmus für {@link SecureRandom}.
     */
    private String algorythmSecureRandom;

    /**
     * Algorithmus für {@link Signature}.
     */
    private String algorythmSignature;

    /**
     *
     */
    private int keySize;

    /**
     * Provider für die {@link Cipher}.
     */
    private String providerCipher;

    /**
     * Provider als Default.
     */
    private String providerDefault;

    /**
     * Provider für {@link MessageDigest}.
     */
    private String providerDigest;

    /**
     * Provider für {@link KeyGenerator} oder {@link KeyPairGenerator}.
     */
    private String providerKeyGenerator;

    /**
     * Algorithmus für {@link SecureRandom}.
     */
    private String providerSecureRandom;

    /**
     * Provider für {@link Signature}.
     */
    private String providerSignature;

    /**
     * Erstellt ein neues {@link CryptoConfig} Object.
     */
    CryptoConfig()
    {
        super();

        providerDefault("SunJCE");
        providerDigest("SUN");
        providerSecureRandom("SUN");

        algorythmDigest("SHA-512");
        algorythmSecureRandom("NativePRNG");
    }

    /**
     * Algorithmus für {@link Cipher}.<br>
     * Default: {@link #algorythmDefault(String)}
     *
     * @param algorythmCipher String
     *
     * @return {@link CryptoConfig}
     */
    public T algorythmCipher(final String algorythmCipher)
    {
        this.algorythmCipher = algorythmCipher;

        return getThis();
    }

    /**
     * Algorithmus als Default.<br>
     * {@link Cipher}, {@link KeyGenerator}, {@link KeyPairGenerator}, {@link MessageDigest}, {@link Signature}, {@link SecureRandom}
     *
     * @param algorythmDefault String
     *
     * @return {@link CryptoConfig}
     */
    public T algorythmDefault(final String algorythmDefault)
    {
        this.algorythmDefault = algorythmDefault;

        return getThis();
    }

    /**
     * Algorithmus für {@link MessageDigest}.<br>
     * Default: {@link #algorythmDefault(String)}
     *
     * @param algorythmDigest String
     *
     * @return {@link CryptoConfig}
     */
    public T algorythmDigest(final String algorythmDigest)
    {
        this.algorythmDigest = algorythmDigest;

        return getThis();
    }

    /**
     * Algorithmus für {@link KeyGenerator} oder {@link KeyPairGenerator}.<br>
     * Default: {@link #algorythmDefault(String)}
     *
     * @param algorythmKeyGenerator String
     *
     * @return {@link CryptoConfig}
     */
    public T algorythmKeyGenerator(final String algorythmKeyGenerator)
    {
        this.algorythmKeyGenerator = algorythmKeyGenerator;

        return getThis();
    }

    /**
     * Algorithmus für {@link SecureRandom}.<br>
     * Default: {@link #algorythmDefault(String)}<br>
     * Beispiel: "NativePRNG", "SHA1PRNG", {@link SecureRandom#getInstanceStrong()}
     *
     * @param algorythmSecureRandom String
     *
     * @return {@link CryptoConfig}
     */
    public T algorythmSecureRandom(final String algorythmSecureRandom)
    {
        this.algorythmSecureRandom = algorythmSecureRandom;

        return getThis();
    }

    /**
     * Algorithmus für {@link Signature}.<br>
     * Default: {@link #algorythmDefault(String)}
     *
     * @param algorythmSignature String
     *
     * @return {@link CryptoConfig}
     */
    public T algorythmSignature(final String algorythmSignature)
    {
        this.algorythmSignature = algorythmSignature;

        return getThis();
    }

    /**
     * @return {@link Crypto}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public abstract Crypto build() throws Exception;

    /**
     * @return String
     *
     * @see #algorythmDigest(String)
     */
    public String getAlgorythmDigest()
    {
        return this.algorythmDigest != null ? this.algorythmDigest : getAlgorythmDefault();
    }

    /**
     * @return String
     *
     * @see #algorythmSecureRandom(String)
     */
    public String getAlgorythmSecureRandom()
    {
        return this.algorythmSecureRandom != null ? this.algorythmSecureRandom : getAlgorythmDefault();
    }

    /**
     * @return String
     *
     * @see #providerDigest(String)
     */
    public String getProviderDigest()
    {
        return this.providerDigest != null ? this.providerDigest : getProviderDefault();
    }

    /**
     * @return String
     *
     * @see #providerSecureRandom(String)
     */
    public String getProviderSecureRandom()
    {
        return this.providerSecureRandom != null ? this.providerSecureRandom : getProviderDefault();
    }

    /**
     * Default: 0
     *
     * @param keySize int
     *
     * @return {@link CryptoConfig}
     */
    public T keySize(final int keySize)
    {
        this.keySize = keySize;

        return getThis();
    }

    /**
     * Provider für {@link Cipher}.<br>
     * Default: {@link #providerDefault(String)}
     *
     * @param providerCipher String
     *
     * @return {@link CryptoConfig}
     */
    public T providerCipher(final String providerCipher)
    {
        this.providerCipher = providerCipher;

        return getThis();
    }

    /**
     * Provider als Default.<br>
     * {@link Cipher}, {@link KeyGenerator}, {@link KeyPairGenerator}, {@link MessageDigest}, {@link Signature}, {@link SecureRandom}
     *
     * @param providerDefault String
     *
     * @return {@link CryptoConfig}
     */
    public T providerDefault(final String providerDefault)
    {
        this.providerDefault = providerDefault;

        return getThis();
    }

    /**
     * Provider für {@link MessageDigest}.<br>
     * Default: {@link #providerDefault(String)}
     *
     * @param providerDigest String
     *
     * @return {@link CryptoConfig}
     */
    public T providerDigest(final String providerDigest)
    {
        this.providerDigest = providerDigest;

        return getThis();
    }

    /**
     * Provider für {@link KeyGenerator} oder {@link KeyPairGenerator}.<br>
     * Default: {@link #providerDefault(String)}
     *
     * @param providerKeyGenerator String
     *
     * @return {@link CryptoConfig}
     */
    public T providerKeyGenerator(final String providerKeyGenerator)
    {
        this.providerKeyGenerator = providerKeyGenerator;

        return getThis();
    }

    /**
     * Provider für {@link SecureRandom}.<br>
     * Default: {@link #providerDefault(String)}<br>
     * Beispiel: "SUN"
     *
     * @param providerSecureRandom String
     *
     * @return {@link CryptoConfig}
     */
    public T providerSecureRandom(final String providerSecureRandom)
    {
        this.providerSecureRandom = providerSecureRandom;

        return getThis();
    }

    /**
     * Provider für {@link Signature}.<br>
     * Default: {@link #providerDefault(String)}
     *
     * @param providerSignature String
     *
     * @return {@link CryptoConfig}
     */
    public T providerSignature(final String providerSignature)
    {
        this.providerSignature = providerSignature;

        return getThis();
    }

    /**
     * @return String
     *
     * @see #algorythmCipher(String)
     */
    protected String getAlgorythmCipher()
    {
        return this.algorythmCipher != null ? this.algorythmCipher : getAlgorythmDefault();
    }

    /**
     * @return String
     *
     * @see #algorythmDefault(String)
     */
    protected String getAlgorythmDefault()
    {
        return this.algorythmDefault;
    }

    /**
     * @return String
     *
     * @see #algorythmKeyGenerator(String)
     */
    protected String getAlgorythmKeyGenerator()
    {
        return this.algorythmKeyGenerator != null ? this.algorythmKeyGenerator : getAlgorythmDefault();
    }

    /**
     * @return String
     *
     * @see #algorythmSignature(String)
     */
    protected String getAlgorythmSignature()
    {
        return this.algorythmSignature != null ? this.algorythmSignature : getAlgorythmDefault();
    }

    /**
     * @return int
     *
     * @see #keySize(int)
     */
    protected int getKeySize()
    {
        return this.keySize;
    }

    /**
     * @return String
     *
     * @see #providerCipher(String)
     */
    protected String getProviderCipher()
    {
        return this.providerCipher != null ? this.providerCipher : getProviderDefault();
    }

    /**
     * @return String
     *
     * @see #providerDefault(String)
     */
    protected String getProviderDefault()
    {
        return this.providerDefault;
    }

    /**
     * @return String
     *
     * @see #providerKeyGenerator(String)
     */
    protected String getProviderKeyGenerator()
    {
        return this.providerKeyGenerator != null ? this.providerKeyGenerator : getProviderDefault();
    }

    /**
     * @return String
     *
     * @see #providerSignature(String)
     */
    protected String getProviderSignature()
    {
        return this.providerSignature != null ? this.providerSignature : getProviderDefault();
    }

    /**
     * @return {@link CryptoConfig}
     */
    @SuppressWarnings("unchecked")
    protected T getThis()
    {
        return (T) this;
    }
}
