// Created: 23 Mai 2024
package de.freese.base.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import de.freese.base.utils.Encoding;

/**
 * @author Thomas Freese
 * @deprecated Use GCM instead
 */
@Deprecated(since = "now")
public final class PbeCryptoAesCbc implements Crypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5PADDING";
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int IV_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private static byte[] generateRandomBytes(final int length) throws NoSuchAlgorithmException {
        return SecureRandom.getInstanceStrong().generateSeed(length);
    }

    private static SecretKey getSecretKey(final String password, final byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM);
        final SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);

        return new SecretKeySpec(secretKey.getEncoded(), ENCRYPTION_ALGORITHM);

        // final KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        // keyGenerator.init(256, SecureRandom.getInstanceStrong());
        // final SecretKey secretKey = keyGenerator.generateKey();
    }

    private static Cipher initCipher(final int mode, final SecretKey secretKey, final byte[] iv) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(mode, secretKey, new IvParameterSpec(iv));

        return cipher;
    }

    private final String password;

    public PbeCryptoAesCbc(final String password) {
        super();

        this.password = Objects.requireNonNull(password, "password required");
    }

    @Override
    public CipherInputStream decrypt(final InputStream inputStream) throws GeneralSecurityException, IOException {
        final byte[] iv = new byte[IV_LENGTH];

        if (inputStream.read(iv) != IV_LENGTH) {
            throw new IllegalStateException("invalid IV");
        }

        final byte[] salt = new byte[SALT_LENGTH];

        if (inputStream.read(salt) != SALT_LENGTH) {
            throw new IllegalStateException("invalid SALT");
        }

        final SecretKey secretKey = getSecretKey(password, salt);
        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);

        return new CipherInputStream(inputStream, cipher);
    }

    @Override
    public String decrypt(final String encrypted) throws GeneralSecurityException {
        final byte[] decoded = Encoding.BASE64.decode(encrypted);
        final byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
        final byte[] salt = Arrays.copyOfRange(decoded, IV_LENGTH, IV_LENGTH + SALT_LENGTH);
        final byte[] encryptedBytes = Arrays.copyOfRange(decoded, IV_LENGTH + SALT_LENGTH, decoded.length);

        final SecretKey secretKey = getSecretKey(password, salt);
        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);

        final byte[] decrypted = cipher.doFinal(encryptedBytes);

        return new String(decrypted, CHARSET);
    }

    @Override
    public CipherOutputStream encrypt(final OutputStream outputStream) throws GeneralSecurityException, IOException {
        final byte[] salt = generateRandomBytes(SALT_LENGTH);
        final SecretKey secret = getSecretKey(password, salt);

        final byte[] iv = generateRandomBytes(IV_LENGTH);
        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secret, iv);

        // prefix IV and Salt
        outputStream.write(iv);
        outputStream.write(salt);

        return new CipherOutputStream(outputStream, cipher);
    }

    @Override
    public String encrypt(final String message) throws GeneralSecurityException {
        final byte[] salt = generateRandomBytes(SALT_LENGTH);
        final SecretKey secretKey = getSecretKey(password, salt);

        final byte[] iv = generateRandomBytes(IV_LENGTH);
        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

        final byte[] encrypted = cipher.doFinal(message.getBytes(CHARSET));

        // prefix IV and Salt
        final byte[] encryptedBytes = ByteBuffer.allocate(iv.length + salt.length + encrypted.length)
                .put(iv)
                .put(salt)
                .put(encrypted)
                .array();

        return Encoding.BASE64.encode(encryptedBytes);
    }
}
