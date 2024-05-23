// Created: 23 Mai 2024
package de.freese.base.security;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Thomas Freese
 */
public final class CryptoAesCbc {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int IV_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String decrypt(final String password, final String encrypted) throws Exception {
        final byte[] decoded = Base64.getDecoder().decode(encrypted);
        final byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
        final byte[] salt = Arrays.copyOfRange(decoded, IV_LENGTH, IV_LENGTH + SALT_LENGTH);
        final byte[] encryptedBytes = Arrays.copyOfRange(decoded, IV_LENGTH + SALT_LENGTH, decoded.length);

        final SecretKey secret = getSecretKey(password, salt);
        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secret, iv);

        final byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, CHARSET);
    }

    public static String encrypt(final String password, final String message) throws Exception {
        final byte[] salt = generateRandomBytes(SALT_LENGTH);
        final SecretKey secret = getSecretKey(password, salt);

        final byte[] iv = generateRandomBytes(IV_LENGTH);
        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secret, iv);

        final byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        // prefix IV and Salt
        final byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + salt.length + encryptedBytes.length)
                .put(iv)
                .put(salt)
                .put(encryptedBytes)
                .array();

        return new String(Base64.getEncoder().encode(cipherTextWithIv), CHARSET);
    }

    public static void main(final String[] args) throws Exception {
        final String password = "yourSecretKey";
        final String plainText = "abcABC123,.;:-_ÖÄÜöäü*'#+`?ß´987/()=?";

        final String cipherText1 = encrypt(password, plainText);
        final String cipherText2 = encrypt(password, plainText);

        System.out.println("CipherText: " + cipherText1);
        System.out.println("CipherText: " + cipherText2);

        System.out.println("DecryptedMessage: " + decrypt(password, cipherText1));
        System.out.println("DecryptedMessage: " + decrypt(password, cipherText2));
    }

    private static byte[] generateRandomBytes(final int length) throws NoSuchAlgorithmException {
        return SecureRandom.getInstanceStrong().generateSeed(length);
    }

    private static SecretKey getSecretKey(final String password, final byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM);
        final SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);

        return new SecretKeySpec(secretKey.getEncoded(), ENCRYPTION_ALGORITHM);
    }

    private static Cipher initCipher(final int mode, final SecretKey secretKey, final byte[] iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(mode, secretKey, new IvParameterSpec(iv));

        return cipher;
    }

    private CryptoAesCbc() {
        super();
    }
}
