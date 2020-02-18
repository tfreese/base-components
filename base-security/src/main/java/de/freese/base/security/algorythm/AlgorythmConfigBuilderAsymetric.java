/**
 * Created: 13.05.2019
 */

package de.freese.base.security.algorythm;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.function.Supplier;
import javax.crypto.Cipher;

/**
 * Builder einer asymetrischen Public- / Private-Key Verschlüsselung der "java.security"-API.
 *
 * @author Thomas Freese
 */
public class AlgorythmConfigBuilderAsymetric extends AlgorythmConfigBuilder<AlgorythmConfigBuilderAsymetric>
{
    /**
     *
     */
    private KeyPair keyPair = null;

    /**
     * Erstellt ein neues {@link AlgorythmConfigBuilderAsymetric} Object.
     */
    protected AlgorythmConfigBuilderAsymetric()
    {
        super();
    }

    /**
     * @see de.freese.base.security.algorythm.AlgorythmConfigBuilder#build()
     */
    @Override
    public AsymetricCrypto build() throws GeneralSecurityException
    {
        AsymetricCrypto crypto = new AsymetricCrypto();

        // SecureRandom
        SecureRandom secureRandom = SecureRandom.getInstance(getAlgorythmSecureRandom(), getProviderSecureRandom());
        crypto.setSecureRandom(secureRandom);

        // Key
        KeyPair theKeyPair = null;

        if (getKeyPair() != null)
        {
            theKeyPair = getKeyPair();
        }
        else if (getKeySize() > 0)
        {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(getAlgorythmKeyGenerator(), getProviderKeyGenerator());
            keyPairGenerator.initialize(getKeySize(), secureRandom);

            KeyPair kp = keyPairGenerator.generateKeyPair();
            theKeyPair = kp;
        }
        else
        {
            throw new IllegalStateException("at least one the key parameter must be set: key, keyBytes, keyPassword, keySize");
        }

        crypto.setKeyPair(theKeyPair);

        /**
         * Cipher
         */
        Supplier<Cipher> cipherEncryptSupplier = null;
        Supplier<Cipher> cipherDecryptSupplier = null;

        Cipher cipherEncrypt = Cipher.getInstance(getAlgorythmCipher(), getProviderCipher());
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, crypto.getKeyPair().getPublic(), secureRandom);
        cipherEncryptSupplier = () -> cipherEncrypt;

        Cipher cipherDecrypt = Cipher.getInstance(getAlgorythmCipher(), getProviderCipher());
        cipherDecrypt.init(Cipher.DECRYPT_MODE, crypto.getKeyPair().getPrivate(), secureRandom);
        cipherDecryptSupplier = () -> cipherDecrypt;

        crypto.setCipherEncryptSupplier(cipherEncryptSupplier);
        crypto.setCipherDecryptSupplier(cipherDecryptSupplier);

        // MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance(getAlgorythmDigest(), getProviderDigest());
        crypto.setMessageDigest(messageDigest);

        // Signature
        Supplier<Signature> signatureSignSupplier = () -> {
            try
            {
                Signature signature = Signature.getInstance(getAlgorythmSignature(), getProviderSignature());
                signature.initSign(crypto.getKeyPair().getPrivate(), secureRandom);
                return signature;
            }
            catch (GeneralSecurityException gsex)
            {
                throw new RuntimeException(gsex);
            }
        };

        Supplier<Signature> signatureVerifySupplier = () -> {
            try
            {
                Signature signature = Signature.getInstance(getAlgorythmSignature(), getProviderSignature());
                signature.initVerify(crypto.getKeyPair().getPublic());
                return signature;
            }
            catch (GeneralSecurityException gsex)
            {
                throw new RuntimeException(gsex);
            }
        };

        crypto.setSignatureSignSupplier(signatureSignSupplier);
        crypto.setSignatureVerifySupplier(signatureVerifySupplier);

        return crypto;
    }

    /**
     * @return {@link KeyPair}
     */
    protected KeyPair getKeyPair()
    {
        return this.keyPair;
    }

    /**
     * @param keyPair {@link KeyPair}
     * @return {@link AlgorythmConfigBuilderAsymetric}
     */
    public AlgorythmConfigBuilderAsymetric keyPair(final KeyPair keyPair)
    {
        this.keyPair = keyPair;

        return this;
    }
}
