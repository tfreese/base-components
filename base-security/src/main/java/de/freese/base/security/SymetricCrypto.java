// Created: 07.03.24
package de.freese.base.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
public final class SymetricCrypto {
    private static final int ITERATIONS = 100;
    private static final int KEY_LENGTH = 256;
    /**
     * Must be 8 Bites long.
     * <pre>{@code
     * byte[] salt = new byte[8];
     * SecureRandom.getInstanceStrong().nextBytes(salt);
     * }</pre>
     */
    private static final byte[] SALT = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};

    public enum Algorithm {
        PBE_WITH_HMAC_SHA512_AND_AES256("PBEWithHmacSHA512AndAES_256"),
        PBE_WITH_HMAC_SHA256_AND_AES128("PBEWithHmacSHA256AndAES_128"),
        /**
         * Needs BouncyCastleProvider
         */
        PBE_WITH_SHA256_AND_256BIT_AES_CBC_BC("PBEWITHSHA256AND256BITAES-CBC-BC"),
        PBE_WITH_MD5_AND_TRIPLEDES("PBEWithMD5AndTripleDES"),
        PBE_WITH_MD5_AND_DES("PBEWithMD5AndDES");

        private final String algorithmName;

        Algorithm(final String algorithmName) {
            this.algorithmName = algorithmName;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }
    }

    public static Crypter create(final String password, final Algorithm algorithm)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        return create(password, algorithm.getAlgorithmName());
    }

    public static Crypter create(final String password, final String algorithm)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        return create(password, algorithm, algorithm);
    }

    public static Crypter create(final String password, final String algorithmKeyFactory, final String algorithmCipher)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), SALT, ITERATIONS, KEY_LENGTH);
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithmKeyFactory);
        final SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        // final byte[] iv = new byte[16];
        // SecureRandom.getInstanceStrong().nextBytes(iv);
        // final IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
        // final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(pbeKeySpec.getSalt(), pbeKeySpec.getIterationCount(), ivParamSpec);
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(pbeKeySpec.getSalt(), pbeKeySpec.getIterationCount());

        final Cipher encryptCipher = Cipher.getInstance(algorithmCipher);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);

        final Cipher decryptCipher = Cipher.getInstance(algorithmCipher);
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, encryptCipher.getParameters());

        return new Crypter(encryptCipher, decryptCipher);
    }

    public static Crypter createAesCbc(final String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        // final byte[] key = Arrays.copyOf(password.getBytes(StandardCharsets.UTF_8), 32);
        // SecretKey secretKey = new SecretKeySpec(key, "AES");

        // String pw = password;
        //
        // while (pw.length() < 33) {
        //     pw += password;
        // }

        // char[] pwChars = pw.toCharArray();
        // pwChars = Arrays.copyOf(pwChars, 32);
        //
        // final PBEKeySpec pbeKeySpec = new PBEKeySpec(pwChars, SALT, ITERATIONS, KEY_LENGTH);
        // final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(Algorithm.PBE_WITH_HMAC_SHA512_AND_AES256.getAlgorithmName());
        // final SecretKey secret = secretKeyFactory.generateSecret(pbeKeySpec);
        //
        // final SecretKey secretKey = new SecretKeySpec(secret.getEncoded(), "AES");

        final SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(password.getBytes(StandardCharsets.UTF_8), 32), "AES");

        // final IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOf(SALT, 16));

        final Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

        final Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, encryptCipher.getParameters());

        return new Crypter(encryptCipher, decryptCipher);
    }
}
