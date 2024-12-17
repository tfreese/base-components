// Created: 23 Mai 2024
package de.freese.base.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.freese.base.utils.Encoding;

/**
 * @author Thomas Freese
 */
public final class PbeCryptoDes implements Crypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
    private static final String ENCRYPTION_ALGORITHM = "DES";
    // private static final String FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    // private static final int ITERATION_COUNT = 65536;
    private static final int IV_LENGTH = 8;
    // private static final int KEY_LENGTH = 256;

    private static byte[] generateRandomBytes(final int length) throws NoSuchAlgorithmException {
        return SecureRandom.getInstanceStrong().generateSeed(length);
    }

    private static SecretKey getSecretKey(final String password) {
        // final KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        // final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM);
        // final SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        //
        // return new SecretKeySpec(Arrays.copyOf(secretKey.getEncoded(), 8), ENCRYPTION_ALGORITHM);
        return new SecretKeySpec(Arrays.copyOf(password.getBytes(CHARSET), 8), ENCRYPTION_ALGORITHM);

        // final KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        // keyGenerator.init(56, SecureRandom.getInstanceStrong());
        // final SecretKey secretKey = keyGenerator.generateKey();
    }

    private static Cipher initCipher(final int mode, final SecretKey secretKey, final byte[] iv) throws Exception {
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(mode, secretKey, new IvParameterSpec(iv));

        return cipher;
    }

    private final String password;

    public PbeCryptoDes(final String password) {
        super();

        this.password = Objects.requireNonNull(password, "password required");
    }

    @Override
    public CipherInputStream decrypt(final InputStream inputStream) throws Exception {
        final byte[] iv = new byte[IV_LENGTH];
        inputStream.read(iv);

        final SecretKey secretKey = getSecretKey(password);
        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);

        return new CipherInputStream(inputStream, cipher);
    }

    @Override
    public String decrypt(final String encrypted) throws Exception {
        final byte[] decoded = Base64.getDecoder().decode(encrypted);
        final byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
        final byte[] encryptedBytes = Arrays.copyOfRange(decoded, IV_LENGTH, decoded.length);

        final SecretKey secretKey = getSecretKey(password);
        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);

        final byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, CHARSET);
    }

    @Override
    public CipherOutputStream encrypt(final OutputStream outputStream) throws Exception {
        final SecretKey secretKey = getSecretKey(password);

        final byte[] iv = generateRandomBytes(IV_LENGTH);
        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

        // prefix IV and Salt
        outputStream.write(iv);

        return new CipherOutputStream(outputStream, cipher);
    }

    @Override
    public String encrypt(final String message) throws Exception {
        final SecretKey secretKey = getSecretKey(password);

        final byte[] iv = generateRandomBytes(IV_LENGTH);
        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

        final byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        // prefix IV and Salt
        final byte[] encryptedBytesWithIv = ByteBuffer.allocate(iv.length + encryptedBytes.length)
                .put(iv)
                .put(encryptedBytes)
                .array();

        return Encoding.BASE64.encode(encryptedBytesWithIv);
    }
}
