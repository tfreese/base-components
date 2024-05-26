// Created: 23 Mai 2024
package de.freese.base.security;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import de.freese.base.utils.Encoding;

/**
 * @author Thomas Freese
 */
public final class PbeCryptoAesGcm implements Crypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int IV_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final int TAG_LENGTH_BIT = 128;

    private static byte[] generateRandomBytes(final int length) throws NoSuchAlgorithmException {
        return SecureRandom.getInstanceStrong().generateSeed(length);
    }

    private static SecretKey getSecretKey(final String password, final byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        final SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM);
        final SecretKey secretKey = factory.generateSecret(keySpec);

        return new SecretKeySpec(secretKey.getEncoded(), ENCRYPTION_ALGORITHM);

        // final KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        // keyGenerator.init(256, SecureRandom.getInstanceStrong());
        // final SecretKey secretKey = keyGenerator.generateKey();
    }

    /**
     * AES-GCM Cipher can not be reused !
     */
    private static Cipher initCipher(final int mode, final SecretKey secretKey, final byte[] iv) throws Exception {
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(mode, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

        return cipher;
    }

    private final String password;

    public PbeCryptoAesGcm(final String password) {
        super();

        this.password = Objects.requireNonNull(password, "password required");
    }

    @Override
    public String decrypt(final String encrypted) throws Exception {
        final byte[] decoded = Base64.getDecoder().decode(encrypted.getBytes(CHARSET));
        final byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
        final byte[] salt = Arrays.copyOfRange(decoded, IV_LENGTH, IV_LENGTH + SALT_LENGTH);
        final byte[] encryptedBytes = Arrays.copyOfRange(decoded, IV_LENGTH + SALT_LENGTH, decoded.length);

        final SecretKey secretKey = getSecretKey(password, salt);
        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);

        final byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, CHARSET);
    }

    @Override
    public String encrypt(final String message) throws Exception {
        final byte[] salt = generateRandomBytes(SALT_LENGTH);
        final SecretKey secretKey = getSecretKey(password, salt);

        final byte[] iv = generateRandomBytes(IV_LENGTH);
        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

        final byte[] encryptedBytes = cipher.doFinal(message.getBytes(CHARSET));

        // prefix IV and Salt
        final byte[] encryptedBytesWithIv = ByteBuffer.allocate(iv.length + salt.length + encryptedBytes.length)
                .put(iv)
                .put(salt)
                .put(encryptedBytes)
                .array();

        return Encoding.BASE64.encode(encryptedBytesWithIv);
    }
}
