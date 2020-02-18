/**
 * Created: 13.05.2019
 */

package de.freese.base.security.algorythm;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.function.Supplier;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Builder einer symetrischen PasswordBasedEncryption (PBE) der "java.security"-API.
 *
 * @author Thomas Freese
 */
public class AlgorythmConfigBuilderSymetric extends AlgorythmConfigBuilder<AlgorythmConfigBuilderSymetric>
{
    /**
    *
    */
    private byte[] initVector = null;

    /**
    *
    */
    private Key key = null;

    /**
    *
    */
    private byte[] keyBytes = null;

    /**
    *
    */
    private String keyPassword = null;

    /**
     * Erstellt ein neues {@link AlgorythmConfigBuilderSymetric} Object.
     */
    protected AlgorythmConfigBuilderSymetric()
    {
        super();
    }

    /**
     * @see de.freese.base.security.algorythm.AlgorythmConfigBuilder#build()
     */
    @Override
    public SymetricCrypto build() throws GeneralSecurityException
    {
        SymetricCrypto crypto = new SymetricCrypto();

        // SecureRandom
        SecureRandom secureRandom = SecureRandom.getInstance(getAlgorythmSecureRandom(), getProviderSecureRandom());
        crypto.setSecureRandom(secureRandom);

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
        else if ((getKeyPassword() != null) && (getKeyPassword().length() > 0))
        {
            if (BouncyCastleProvider.PROVIDER_NAME.equals(getProviderKeyGenerator()))
            {
                KeySpec keySpec = new PBEKeySpec(getKeyPassword().toCharArray(), getInitVector(), 4096);

                SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(getAlgorythmKeyGenerator(), getProviderKeyGenerator());
                SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
                theKey = secretKey;
            }
            else
            {
                SecretKey secretKey = new SecretKeySpec(getKeyPassword().getBytes(StandardCharsets.UTF_8), getAlgorythmKeyGenerator());
                theKey = secretKey;
            }
        }
        else if (getKeySize() > 0)
        {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(getAlgorythmKeyGenerator(), getProviderKeyGenerator());
            keyGenerator.init(getKeySize(), secureRandom);

            SecretKey secretKey = keyGenerator.generateKey();
            theKey = secretKey;
        }
        else
        {
            throw new IllegalStateException("at least one the key parameter must be set: key, keyBytes, keyPassword, keySize");
        }

        crypto.setKey(theKey);

        /**
         * ParameterSpec, Cipher
         */
        Supplier<Cipher> cipherEncryptSupplier = null;
        Supplier<Cipher> cipherDecryptSupplier = null;

        if (getAlgorythmCipher().contains("/GCM/"))
        {
            // GCM braucht speziellen ParameterSpec und die Cipher können nicht wiedeverwendet werden.
            final AlgorithmParameterSpec parameterSpec = new GCMParameterSpec(128, getInitVector());

            // Cipher
            cipherEncryptSupplier = () -> {
                try
                {
                    Cipher cipherEncrypt = Cipher.getInstance(getAlgorythmCipher(), getProviderCipher());
                    cipherEncrypt.init(Cipher.ENCRYPT_MODE, crypto.getKey(), parameterSpec, secureRandom);
                    return cipherEncrypt;
                }
                catch (GeneralSecurityException gsex)
                {
                    throw new RuntimeException(gsex);
                }
            };

            cipherDecryptSupplier = () -> {
                try
                {
                    Cipher cipherDecrypt = Cipher.getInstance(getAlgorythmCipher(), getProviderCipher());
                    cipherDecrypt.init(Cipher.DECRYPT_MODE, crypto.getKey(), parameterSpec, secureRandom);
                    return cipherDecrypt;
                }
                catch (GeneralSecurityException gsex)
                {
                    throw new RuntimeException(gsex);
                }
            };
        }
        else
        {
            AlgorithmParameterSpec parameterSpec = null;

            if (BouncyCastleProvider.PROVIDER_NAME.equals(getProviderCipher()))
            {
                parameterSpec = new PBEParameterSpec(getInitVector(), 4096);
            }
            else
            {
                parameterSpec = new IvParameterSpec(getInitVector());

            }

            // Cipher
            Cipher cipherEncrypt = Cipher.getInstance(getAlgorythmCipher(), getProviderCipher());
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, theKey, parameterSpec, secureRandom);
            cipherEncryptSupplier = () -> cipherEncrypt;

            Cipher cipherDecrypt = Cipher.getInstance(getAlgorythmCipher(), getProviderCipher());
            cipherDecrypt.init(Cipher.DECRYPT_MODE, theKey, parameterSpec, secureRandom);
            cipherDecryptSupplier = () -> cipherDecrypt;
        }

        crypto.setCipherEncryptSupplier(cipherEncryptSupplier);
        crypto.setCipherDecryptSupplier(cipherDecryptSupplier);

        // MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance(getAlgorythmDigest(), getProviderDigest());
        crypto.setMessageDigest(messageDigest);

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
     * @return String
     */
    protected String getKeyPassword()
    {
        return this.keyPassword;
    }

    /**
     * Initialisierungsvector für die {@link IvParameterSpec}.
     *
     * @param initVector byte[]
     * @return {@link AlgorythmConfigBuilderSymetric}
     */
    public AlgorythmConfigBuilderSymetric initVector(final byte[] initVector)
    {
        this.initVector = initVector;

        return this;
    }

    /**
     * @param key {@link Key}
     * @return {@link AlgorythmConfigBuilderSymetric}
     */
    public AlgorythmConfigBuilderSymetric key(final Key key)
    {
        this.key = key;

        return this;
    }

    /**
     * @param keyBytes byte[]
     * @return {@link AlgorythmConfigBuilderSymetric}
     */
    public AlgorythmConfigBuilderSymetric keyBytes(final byte[] keyBytes)
    {
        this.keyBytes = keyBytes;

        return this;
    }

    /**
     * Default: {@link Charset} UTF-8
     *
     * @param keyPassword String
     * @return {@link AlgorythmConfigBuilderSymetric}
     */
    public AlgorythmConfigBuilderSymetric keyPassword(final String keyPassword)
    {
        this.keyPassword = keyPassword;

        return this;
    }
}
