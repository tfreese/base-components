// Created: 07.03.24
package de.freese.base.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
public class SymetricCrypto extends AbstractCrypto {
    /**
     * PBKDF2WithHmacSHA256<br>
     * PBEWithHmacSHA256AndAES_128<br>
     * PBEWithMD5AndTripleDES<br>
     * PBEWithMD5AndDES
     */
    public static final String ALGORITHM = "PBEWithMD5AndTripleDES";

    private static Cipher createCipher(final int mode, final SecretKey secretKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        final byte[] salt = new byte[8];
        // final SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        // secureRandom.nextBytes(salt);

        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 8);

        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKey, pbeParameterSpec);

        return cipher;
    }

    /**
     * See {@link PBEKeySpec}, {@link PBEKey}<br>
     */
    public SymetricCrypto(final PBEKeySpec pbeKeySpec)
            throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        this(SecretKeyFactory.getInstance(ALGORITHM).generateSecret(pbeKeySpec));

        // byte[] salt = new byte[8];
        // Random random = new Random();
        // random.nextBytes(salt);

        // PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100)
        // cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
    }

    /**
     * See {@link PBEKeySpec}, {@link PBEKey}
     */
    public SymetricCrypto(final SecretKey secretKey) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        super(createCipher(Cipher.ENCRYPT_MODE, secretKey), createCipher(Cipher.DECRYPT_MODE, secretKey));
    }
}
