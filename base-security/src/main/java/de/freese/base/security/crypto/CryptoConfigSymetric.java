// Created: 29.05.2021
package de.freese.base.security.crypto;

import java.security.Key;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Builder einer symetrischen PasswordBasedEncryption (PBE) der "java.security"-API.
 *
 * @author Thomas Freese
 */
public class CryptoConfigSymetric extends CryptoConfig<CryptoConfigSymetric>
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
    *
    */
    private byte[] initVector;

    /**
    *
    */
    private Key key;

    /**
    *
    */
    private byte[] keyBytes;

    /**
     * Erstellt ein neues {@link CryptoConfigSymetric} Object.
     */
    CryptoConfigSymetric()
    {
        super();
    }

    /**
     * @see de.freese.base.security.crypto.CryptoConfig#build()
     */
    @Override
    public Crypto build() throws Exception
    {
        CryptoSymetric crypto = new CryptoSymetric(this);

        // Key
        Key theKey = null;

        if (getKey() != null)
        {
            theKey = getKey();
        }
        else if ((getKeyBytes() != null) && (getKeyBytes().length > 0))
        {
            SecretKey secretKey = new SecretKeySpec(getKeyBytes(), getAlgorythmKeyGenerator());
            theKey = secretKey;
        }
        // else if ((getKeyPassword() != null) && (getKeyPassword().length() > 0))
        // {
        // if (BouncyCastleProvider.PROVIDER_NAME.equals(getProviderKeyGenerator()))
        // {
        // KeySpec keySpec = new PBEKeySpec(getKeyPassword().toCharArray(), getInitVector(), 4096);
        //
        // SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(getAlgorythmKeyGenerator(), getProviderKeyGenerator());
        // SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        // theKey = secretKey;
        // }
        // else
        // {
        // SecretKey secretKey = new SecretKeySpec(getKeyPassword().getBytes(StandardCharsets.UTF_8), getAlgorythmKeyGenerator());
        // theKey = secretKey;
        // }
        // }
        else if (getKeySize() > 0)
        {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(getAlgorythmKeyGenerator(), getProviderKeyGenerator());
            keyGenerator.init(getKeySize(), crypto.getSecureRandom());

            SecretKey secretKey = keyGenerator.generateKey();
            theKey = secretKey;
        }
        else
        {
            throw new IllegalStateException("at least one the key parameter must be set: key, keyBytes, keyPassword, keySize");
        }

        crypto.setKey(theKey);

        return crypto;
    }

    /**
     * @return byte[]
     */
    protected byte[] getInitVector()
    {
        return this.initVector;
    }

    /**
     * @return {@link Key}
     */
    protected Key getKey()
    {
        return this.key;
    }

    /**
     * @return byte[]
     */
    protected byte[] getKeyBytes()
    {
        return this.keyBytes;
    }

    /**
     * Initialisierungsvector für die {@link IvParameterSpec}.
     *
     * @param initVector byte[]
     * @return {@link CryptoConfigSymetric}
     */
    public CryptoConfigSymetric initVector(final byte[] initVector)
    {
        this.initVector = initVector;

        return this;
    }

    /**
     * @param key {@link Key}
     * @return {@link CryptoConfigSymetric}
     */
    public CryptoConfigSymetric key(final Key key)
    {
        this.key = key;

        return this;
    }

    /**
     * @param keyBytes byte[]
     * @return {@link CryptoConfigSymetric}
     */
    public CryptoConfigSymetric keyBytes(final byte[] keyBytes)
    {
        this.keyBytes = keyBytes;

        return this;
    }
}
