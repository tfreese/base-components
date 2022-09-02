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
    private String algorithmCipher;

    /**
     * Algorithmus als Default.
     */
    private String algorithmDefault;

    /**
     * Algorithmus für {@link MessageDigest}.
     */
    private String algorithmDigest;

    /**
     * Algorithmus für {@link KeyGenerator} oder {@link KeyPairGenerator}.
     */
    private String algorithmKeyGenerator;

    /**
     * Algorithmus für {@link SecureRandom}.
     */
    private String algorithmSecureRandom;

    /**
     * Algorithmus für {@link Signature}.
     */
    private String algorithmSignature;

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

        algorithmDigest("SHA-512");
        algorithmSecureRandom("NativePRNG");
    }

    /**
     * Algorithmus für {@link Cipher}.<br>
     * Default: {@link #algorithmDefault(String)}
     *
     * @param algorithmCipher String
     *
     * @return {@link CryptoConfig}
     */
    public T algorithmCipher(final String algorithmCipher)
    {
        this.algorithmCipher = algorithmCipher;

        return getThis();
    }

    /**
     * Algorithmus als Default.<br>
     * {@link Cipher}, {@link KeyGenerator}, {@link KeyPairGenerator}, {@link MessageDigest}, {@link Signature}, {@link SecureRandom}
     *
     * @param algorithmDefault String
     *
     * @return {@link CryptoConfig}
     */
    public T algorithmDefault(final String algorithmDefault)
    {
        this.algorithmDefault = algorithmDefault;

        return getThis();
    }

    /**
     * Algorithmus für {@link MessageDigest}.<br>
     * Default: {@link #algorithmDefault(String)}
     *
     * @param algorithmDigest String
     *
     * @return {@link CryptoConfig}
     */
    public T algorithmDigest(final String algorithmDigest)
    {
        this.algorithmDigest = algorithmDigest;

        return getThis();
    }

    /**
     * Algorithmus für {@link KeyGenerator} oder {@link KeyPairGenerator}.<br>
     * Default: {@link #algorithmDefault(String)}
     *
     * @param algorithmKeyGenerator String
     *
     * @return {@link CryptoConfig}
     */
    public T algorithmKeyGenerator(final String algorithmKeyGenerator)
    {
        this.algorithmKeyGenerator = algorithmKeyGenerator;

        return getThis();
    }

    /**
     * Algorithmus für {@link SecureRandom}.<br>
     * Default: {@link #algorithmDefault(String)}<br>
     * Beispiel: "NativePRNG", "SHA1PRNG", {@link SecureRandom#getInstanceStrong()}
     *
     * @param algorithmSecureRandom String
     *
     * @return {@link CryptoConfig}
     */
    public T algorithmSecureRandom(final String algorithmSecureRandom)
    {
        this.algorithmSecureRandom = algorithmSecureRandom;

        return getThis();
    }

    /**
     * Algorithmus für {@link Signature}.<br>
     * Default: {@link #algorithmDefault(String)}
     *
     * @param algorithmSignature String
     *
     * @return {@link CryptoConfig}
     */
    public T algorithmSignature(final String algorithmSignature)
    {
        this.algorithmSignature = algorithmSignature;

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
     * @see #algorithmDigest(String)
     */
    public String getAlgorithmDigest()
    {
        return this.algorithmDigest != null ? this.algorithmDigest : getAlgorithmDefault();
    }

    /**
     * @return String
     *
     * @see #algorithmSecureRandom(String)
     */
    public String getAlgorithmSecureRandom()
    {
        return this.algorithmSecureRandom != null ? this.algorithmSecureRandom : getAlgorithmDefault();
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
     * @see #algorithmCipher(String)
     */
    protected String getAlgorithmCipher()
    {
        return this.algorithmCipher != null ? this.algorithmCipher : getAlgorithmDefault();
    }

    /**
     * @return String
     *
     * @see #algorithmDefault(String)
     */
    protected String getAlgorithmDefault()
    {
        return this.algorithmDefault;
    }

    /**
     * @return String
     *
     * @see #algorithmKeyGenerator(String)
     */
    protected String getAlgorithmKeyGenerator()
    {
        return this.algorithmKeyGenerator != null ? this.algorithmKeyGenerator : getAlgorithmDefault();
    }

    /**
     * @return String
     *
     * @see #algorithmSignature(String)
     */
    protected String getAlgorithmSignature()
    {
        return this.algorithmSignature != null ? this.algorithmSignature : getAlgorithmDefault();
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
