/**
 * Created: 13.05.2019
 */

package de.freese.base.security.algorythm;

import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Signature;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * Builder für die Konfiguration einer Verschlüsselung der "java.security"-API.
 *
 * @author Thomas Freese
 * @param <T> Entity-Type
 */
public abstract class AbstractAlgorythmConfigBuilder<T extends AbstractAlgorythmConfigBuilder<T>>
{
    /**
     * 64bit
     */
    // @formatter:off
    public static final byte[] DEFAULT_INIT_VECTOR = new byte[] {
                        -47, -17, -70, 18, 52, 90, 104, 34,
                        -48, -100, -106, -121, 73, -116, -69, -3,
                        -58, 124, 60, -46, -84, -94, -54, 9,
                        27, 26, 30, 56, -27, 59, -11, 9,
                        117, -90, 70, -8, 86, 69, 32, -27,
                        -23, -15, -88, -106, -128, -113, 53, -51,
                        -3, 110, 71, -27, 97, 19, -36, 2,
                        80, 117, 86, 46, -51, 3, -55, -123
        };
    //@formatter:on

    /**
     * 64bit
     */
    // @formatter:off
    public static final byte[] DEFAULT_SALT = new byte[] {
                        -17, -46, -76, -22, 83, 71, -101, -114,
                        -120, 62, 83, 9, -28, -48, 60, -51,
                        102, -120, -51, 102, -80, -46, -108, -26,
                        77, -92, 83, -85, 109, 8, 1, -92,
                        -52, -99, -93, 29, 36, -111, 98, -12,
                        -23, -100, -105, 20, -18, 95, 39, -66,
                        -120, -101, -124, 80, -30, -50, -45, 66,
                        51, 66, 45, 120, -96, -4, 81, -18
        };
    //@formatter:on

    /**
     * Builder einer asymetrischen Public- / Private-Key Verschlüsselung der "java.security"-API.
     *
     * @return {@link AlgorythmConfigBuilderAsymetric}
     */
    public static AlgorythmConfigBuilderAsymetric asymetric()
    {
        return new AlgorythmConfigBuilderAsymetric();
    }

    /**
     * Builder einer symetrischen PasswordBasedEncryption (PBE) der "java.security"-API.
     *
     * @return {@link AlgorythmConfigBuilderSymetric}
     */
    public static AlgorythmConfigBuilderSymetric symetric()
    {
        return new AlgorythmConfigBuilderSymetric();
    }

    /**
     * Algorithmus für alles.
     */
    private String algorythm;

    /**
     * Algorithmus für {@link Cipher}.
     */
    private String algorythmCipher;

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
     * Provider für alles.
     */
    private String provider;

    /**
     * Provider für die {@link Cipher}.
     */
    private String providerCipher;

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
     * Erstellt ein neues {@link AbstractAlgorythmConfigBuilder} Object.
     */
    protected AbstractAlgorythmConfigBuilder()
    {
        super();

        provider("SunJCE");
        providerDigest("SUN");
        providerSecureRandom("SUN");

        algorythmDigest("SHA-512");
        algorythmSecureRandom("NativePRNG");
    }

    /**
     * Algorithmus für alles.<br>
     * {@link Cipher}, {@link KeyGenerator}, {@link KeyPairGenerator}, {@link MessageDigest}, {@link Signature}, {@link SecureRandom}
     *
     * @param algorythm String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T algorythm(final String algorythm)
    {
        this.algorythm = algorythm;

        return getThis();
    }

    /**
     * Algorithmus für {@link Cipher}.<br>
     * Default: {@link #algorythm(String)}
     *
     * @param algorythmCipher String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T algorythmCipher(final String algorythmCipher)
    {
        this.algorythmCipher = algorythmCipher;

        return getThis();
    }

    /**
     * Algorithmus für {@link MessageDigest}.<br>
     * Default: {@link #algorythm(String)}
     *
     * @param algorythmDigest String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T algorythmDigest(final String algorythmDigest)
    {
        this.algorythmDigest = algorythmDigest;

        return getThis();
    }

    /**
     * Algorithmus für {@link KeyGenerator} oder {@link KeyPairGenerator}.<br>
     * Default: {@link #provider(String)}
     *
     * @param algorythmKeyGenerator String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T algorythmKeyGenerator(final String algorythmKeyGenerator)
    {
        this.algorythmKeyGenerator = algorythmKeyGenerator;

        return getThis();
    }

    /**
     * Algorithmus für {@link SecureRandom}.<br>
     * Default: {@link #algorythm(String)}<br>
     * Beispiel: "NativePRNG", "SHA1PRNG", {@link SecureRandom#getInstanceStrong()}
     *
     * @param algorythmSecureRandom String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T algorythmSecureRandom(final String algorythmSecureRandom)
    {
        this.algorythmSecureRandom = algorythmSecureRandom;

        return getThis();
    }

    /**
     * Algorithmus für {@link Signature}.<br>
     * Default: {@link #algorythm(String)}
     *
     * @param algorythmSignature String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T algorythmSignature(final String algorythmSignature)
    {
        this.algorythmSignature = algorythmSignature;

        return getThis();
    }

    /**
     * @return {@link Crypto}
     * @throws GeneralSecurityException Falls was schief geht.
     */
    public abstract Crypto build() throws GeneralSecurityException;

    /**
     * @see #algorythm(String)
     * @return String
     */
    protected String getAlgorythm()
    {
        return this.algorythm;
    }

    /**
     * @see #algorythmCipher(String)
     * @return String
     */
    protected String getAlgorythmCipher()
    {
        return this.algorythmCipher != null ? this.algorythmCipher : getAlgorythm();
    }

    /**
     * @see #algorythmDigest(String)
     * @return String
     */
    protected String getAlgorythmDigest()
    {
        return this.algorythmDigest != null ? this.algorythmDigest : getAlgorythm();
    }

    /**
     * @see #algorythmKeyGenerator(String)
     * @return String
     */
    protected String getAlgorythmKeyGenerator()
    {
        return this.algorythmKeyGenerator != null ? this.algorythmKeyGenerator : getAlgorythm();
    }

    /**
     * @see #algorythmSecureRandom(String)
     * @return String
     */
    protected String getAlgorythmSecureRandom()
    {
        return this.algorythmSecureRandom != null ? this.algorythmSecureRandom : getAlgorythm();
    }

    /**
     * @see #algorythmSignature(String)
     * @return String
     */
    protected String getAlgorythmSignature()
    {
        return this.algorythmSignature != null ? this.algorythmSignature : getAlgorythm();
    }

    /**
     * @see #keySize(int)
     * @return int
     */
    protected int getKeySize()
    {
        return this.keySize;
    }

    /**
     * @see #provider(String)
     * @return String
     */
    protected String getProvider()
    {
        return this.provider;
    }

    /**
     * @see #providerCipher(String)
     * @return String
     */
    protected String getProviderCipher()
    {
        return this.providerCipher != null ? this.providerCipher : getProvider();
    }

    /**
     * @see #providerDigest(String)
     * @return String
     */
    protected String getProviderDigest()
    {
        return this.providerDigest != null ? this.providerDigest : getProvider();
    }

    /**
     * @see #providerKeyGenerator(String)
     * @return String
     */
    protected String getProviderKeyGenerator()
    {
        return this.providerKeyGenerator != null ? this.providerKeyGenerator : getProvider();
    }

    /**
     * @see #providerSecureRandom(String)
     * @return String
     */
    protected String getProviderSecureRandom()
    {
        return this.providerSecureRandom != null ? this.providerSecureRandom : getProvider();
    }

    /**
     * @see #providerSignature(String)
     * @return String
     */
    protected String getProviderSignature()
    {
        return this.providerSignature != null ? this.providerSignature : getProvider();
    }

    /**
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    @SuppressWarnings("unchecked")
    protected T getThis()
    {
        return (T) this;
    }

    /**
     * Default: 0
     *
     * @param keySize int
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T keySize(final int keySize)
    {
        this.keySize = keySize;

        return getThis();
    }

    /**
     * Provider für alles.<br>
     * {@link Cipher}, {@link KeyGenerator}, {@link KeyPairGenerator}, {@link MessageDigest}, {@link Signature}, {@link SecureRandom}
     *
     * @param provider String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T provider(final String provider)
    {
        this.provider = provider;

        return getThis();
    }

    /**
     * Provider für {@link Cipher}.<br>
     * Default: {@link #provider(String)}
     *
     * @param providerCipher String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T providerCipher(final String providerCipher)
    {
        this.providerCipher = providerCipher;

        return getThis();
    }

    /**
     * Provider für {@link MessageDigest}.<br>
     * Default: {@link #algorythm(String)}
     *
     * @param providerDigest String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T providerDigest(final String providerDigest)
    {
        this.providerDigest = providerDigest;

        return getThis();
    }

    /**
     * Provider für {@link KeyGenerator} oder {@link KeyPairGenerator}.<br>
     * Default: {@link #provider(String)}
     *
     * @param providerKeyGenerator String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T providerKeyGenerator(final String providerKeyGenerator)
    {
        this.providerKeyGenerator = providerKeyGenerator;

        return getThis();
    }

    /**
     * Provider für {@link SecureRandom}.<br>
     * Default: {@link #provider(String)}<br>
     * Beispiel: "SUN"
     *
     * @param providerSecureRandom String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T providerSecureRandom(final String providerSecureRandom)
    {
        this.providerSecureRandom = providerSecureRandom;

        return getThis();
    }

    /**
     * Provider für {@link Signature}.<br>
     * Default: {@link #provider(String)}
     *
     * @param providerSignature String
     * @return {@link AbstractAlgorythmConfigBuilder}
     */
    public T providerSignature(final String providerSignature)
    {
        this.providerSignature = providerSignature;

        return getThis();
    }
}
