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
     */
    public static CryptoConfigAsymetric asymetric()
    {
        return new CryptoConfigAsymetric();
    }

    /**
     * Builder einer symmetrischen PasswordBasedEncryption (PBE) der "java.security"-API.
     */
    public static CryptoConfigSymetric symetric()
    {
        return new CryptoConfigSymetric();
    }

    private String algorithmCipher;

    private String algorithmDefault;

    private String algorithmDigest;

    private String algorithmKeyGenerator;

    private String algorithmSecureRandom;

    private String algorithmSignature;

    private int keySize;

    private String providerCipher;

    private String providerDefault;

    private String providerDigest;

    private String providerKeyGenerator;

    private String providerSecureRandom;

    private String providerSignature;

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
     * Default: {@link #algorithmDefault(String)}
     */
    public T algorithmCipher(final String algorithmCipher)
    {
        this.algorithmCipher = algorithmCipher;

        return getThis();
    }

    /**
     * Algorithmus als Default.<br>
     * {@link Cipher}, {@link KeyGenerator}, {@link KeyPairGenerator}, {@link MessageDigest}, {@link Signature}, {@link SecureRandom}
     */
    public T algorithmDefault(final String algorithmDefault)
    {
        this.algorithmDefault = algorithmDefault;

        return getThis();
    }

    /**
     * Default: {@link #algorithmDefault(String)}
     */
    public T algorithmDigest(final String algorithmDigest)
    {
        this.algorithmDigest = algorithmDigest;

        return getThis();
    }

    /**
     * Algorithmus für {@link KeyGenerator} oder {@link KeyPairGenerator}.<br>
     * Default: {@link #algorithmDefault(String)}
     */
    public T algorithmKeyGenerator(final String algorithmKeyGenerator)
    {
        this.algorithmKeyGenerator = algorithmKeyGenerator;

        return getThis();
    }

    /**
     * Default: {@link #algorithmDefault(String)}<br>
     * Beispiel: "NativePRNG", "SHA1PRNG", {@link SecureRandom#getInstanceStrong()}
     */
    public T algorithmSecureRandom(final String algorithmSecureRandom)
    {
        this.algorithmSecureRandom = algorithmSecureRandom;

        return getThis();
    }

    /**
     * Default: {@link #algorithmDefault(String)}
     */
    public T algorithmSignature(final String algorithmSignature)
    {
        this.algorithmSignature = algorithmSignature;

        return getThis();
    }

    public abstract Crypto build() throws Exception;

    public String getAlgorithmDigest()
    {
        return this.algorithmDigest != null ? this.algorithmDigest : getAlgorithmDefault();
    }

    public String getAlgorithmSecureRandom()
    {
        return this.algorithmSecureRandom != null ? this.algorithmSecureRandom : getAlgorithmDefault();
    }

    public String getProviderDigest()
    {
        return this.providerDigest != null ? this.providerDigest : getProviderDefault();
    }

    public String getProviderSecureRandom()
    {
        return this.providerSecureRandom != null ? this.providerSecureRandom : getProviderDefault();
    }

    /**
     * Default: 0
     */
    public T keySize(final int keySize)
    {
        this.keySize = keySize;

        return getThis();
    }

    /**
     * Default: {@link #providerDefault(String)}
     */
    public T providerCipher(final String providerCipher)
    {
        this.providerCipher = providerCipher;

        return getThis();
    }

    /**
     * Provider als Default.<br>
     * {@link Cipher}, {@link KeyGenerator}, {@link KeyPairGenerator}, {@link MessageDigest}, {@link Signature}, {@link SecureRandom}
     */
    public T providerDefault(final String providerDefault)
    {
        this.providerDefault = providerDefault;

        return getThis();
    }

    /**
     * Default: {@link #providerDefault(String)}
     */
    public T providerDigest(final String providerDigest)
    {
        this.providerDigest = providerDigest;

        return getThis();
    }

    /**
     * Provider für {@link KeyGenerator} oder {@link KeyPairGenerator}.<br>
     * Default: {@link #providerDefault(String)}
     */
    public T providerKeyGenerator(final String providerKeyGenerator)
    {
        this.providerKeyGenerator = providerKeyGenerator;

        return getThis();
    }

    /**
     * Default: {@link #providerDefault(String)}<br>
     * Beispiel: "SUN"
     */
    public T providerSecureRandom(final String providerSecureRandom)
    {
        this.providerSecureRandom = providerSecureRandom;

        return getThis();
    }

    /**
     * Default: {@link #providerDefault(String)}
     */
    public T providerSignature(final String providerSignature)
    {
        this.providerSignature = providerSignature;

        return getThis();
    }

    protected String getAlgorithmCipher()
    {
        return this.algorithmCipher != null ? this.algorithmCipher : getAlgorithmDefault();
    }

    protected String getAlgorithmDefault()
    {
        return this.algorithmDefault;
    }

    protected String getAlgorithmKeyGenerator()
    {
        return this.algorithmKeyGenerator != null ? this.algorithmKeyGenerator : getAlgorithmDefault();
    }

    protected String getAlgorithmSignature()
    {
        return this.algorithmSignature != null ? this.algorithmSignature : getAlgorithmDefault();
    }

    protected int getKeySize()
    {
        return this.keySize;
    }

    protected String getProviderCipher()
    {
        return this.providerCipher != null ? this.providerCipher : getProviderDefault();
    }

    protected String getProviderDefault()
    {
        return this.providerDefault;
    }

    protected String getProviderKeyGenerator()
    {
        return this.providerKeyGenerator != null ? this.providerKeyGenerator : getProviderDefault();
    }

    protected String getProviderSignature()
    {
        return this.providerSignature != null ? this.providerSignature : getProviderDefault();
    }

    @SuppressWarnings("unchecked")
    protected T getThis()
    {
        return (T) this;
    }
}
