// Created: 23 Mai 2024
package de.freese.base.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import de.freese.base.utils.Encoding;

/**
 * @author Thomas Freese
 */
public final class PbeCryptoAlgorithm implements Crypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int IV_LENGTH = 8;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 8;

    public enum Algorithm {
        // PBE_WITH_HMAC_SHA512_AND_AES256("PBEWithHmacSHA512AndAES_256"),
        // PBE_WITH_HMAC_SHA256_AND_AES128("PBEWithHmacSHA256AndAES_128"),
        /**
         * Needs BouncyCastleProvider<br>
         * <pre>{@code
         * if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
         *      Security.addProvider(new BouncyCastleProvider());
         * }
         * }</pre>
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

    private static byte[] generateRandomBytes(final int length) throws NoSuchAlgorithmException {
        return SecureRandom.getInstanceStrong().generateSeed(length);
    }

    private static SecretKey getSecretKey(final Algorithm algorithm, final String password, final byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm.getAlgorithmName());

        return secretKeyFactory.generateSecret(pbeKeySpec);
    }

    private static Cipher initCipher(final Algorithm algorithm, final int mode, final SecretKey secretKey, final byte[] salt) throws Exception {
        final Cipher cipher = Cipher.getInstance(algorithm.getAlgorithmName());

        cipher.init(mode, secretKey, new PBEParameterSpec(salt, ITERATION_COUNT));

        // if (mode == Cipher.ENCRYPT_MODE) {
        //     cipher.init(mode, secretKey, new PBEParameterSpec(salt, ITERATION_COUNT));
        // }
        // else {
        //     // final Cipher encryptCipher = Cipher.getInstance(algorithm.getAlgorithmName());
        //     // encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new PBEParameterSpec(salt, ITERATION_COUNT));
        //     // cipher.init(mode, secretKey, encryptCipher.getParameters());
        //     cipher.init(mode, secretKey, new PBEParameterSpec(salt, ITERATION_COUNT));
        // }

        return cipher;
    }

    private final Algorithm algorithm;
    private final String password;

    public PbeCryptoAlgorithm(final String password, final Algorithm algorithm) {
        super();

        this.password = Objects.requireNonNull(password, "password required");
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm required");
    }

    @Override
    public CipherInputStream decrypt(final InputStream inputStream) throws Exception {
        final byte[] iv = new byte[IV_LENGTH];
        inputStream.read(iv);

        final byte[] salt = new byte[SALT_LENGTH];
        inputStream.read(salt);

        final SecretKey secretKey = getSecretKey(algorithm, password, salt);
        final Cipher cipher = initCipher(algorithm, Cipher.DECRYPT_MODE, secretKey, salt);

        return new CipherInputStream(inputStream, cipher);
    }

    @Override
    public String decrypt(final String encrypted) throws Exception {
        final byte[] decoded = Base64.getDecoder().decode(encrypted);
        // final byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
        final byte[] salt = Arrays.copyOfRange(decoded, 0, SALT_LENGTH);
        final byte[] encryptedBytes = Arrays.copyOfRange(decoded, SALT_LENGTH, decoded.length);

        final SecretKey secretKey = getSecretKey(algorithm, password, salt);
        final Cipher cipher = initCipher(algorithm, Cipher.DECRYPT_MODE, secretKey, salt);

        final byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, CHARSET);
    }

    @Override
    public CipherOutputStream encrypt(final OutputStream outputStream) throws Exception {
        final byte[] salt = generateRandomBytes(SALT_LENGTH);
        final SecretKey secretKey = getSecretKey(algorithm, password, salt);

        final byte[] iv = generateRandomBytes(IV_LENGTH);

        final Cipher cipher = initCipher(algorithm, Cipher.ENCRYPT_MODE, secretKey, salt);

        // prefix IV and Salt
        outputStream.write(iv);
        outputStream.write(salt);

        return new CipherOutputStream(outputStream, cipher);
    }

    @Override
    public String encrypt(final String message) throws Exception {
        final byte[] salt = generateRandomBytes(SALT_LENGTH);
        // final byte[] salt = new byte[0];
        final SecretKey secretKey = getSecretKey(algorithm, password, salt);

        // final byte[] iv = generateRandomBytes(IV_LENGTH);
        final Cipher cipher = initCipher(algorithm, Cipher.ENCRYPT_MODE, secretKey, salt);

        final byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        // prefix IV and Salt
        final byte[] encryptedBytesWithIv = ByteBuffer.allocate(salt.length + encryptedBytes.length)
                // .put(iv)
                .put(salt)
                .put(encryptedBytes)
                .array();

        return Encoding.BASE64.encode(encryptedBytesWithIv);
    }
}
