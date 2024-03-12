// Created: 07.03.24
package de.freese.base.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.function.Supplier;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
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

    public static Crypter create(final String password, final Algorithm algorithm) throws GeneralSecurityException {
        return create(password, algorithm.getAlgorithmName());
    }

    public static Crypter create(final String password, final String algorithm) throws GeneralSecurityException {
        return create(password, algorithm, algorithm);
    }

    public static Crypter create(final String password, final String algorithmKeyFactory, final String algorithmCipher) throws GeneralSecurityException {
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), SALT, ITERATIONS, KEY_LENGTH);
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithmKeyFactory);
        final SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        // final byte[] initVector = new byte[16];
        // SecureRandom.getInstanceStrong().nextBytes(initVector);
        // final AlgorithmParameterSpec ivParamSpec = new IvParameterSpec(iv);
        // final AlgorithmParameterSpec pbeParameterSpec = new PBEParameterSpec(pbeKeySpec.getSalt(), pbeKeySpec.getIterationCount(), ivParamSpec);
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(pbeKeySpec.getSalt(), pbeKeySpec.getIterationCount());

        final Cipher encryptCipher = Cipher.getInstance(algorithmCipher);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);

        final Cipher decryptCipher = Cipher.getInstance(algorithmCipher);
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, encryptCipher.getParameters());

        return new Crypter(encryptCipher, decryptCipher);
    }

    public static Crypter createAesCbc(final String password) throws GeneralSecurityException {
        // final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        // keyGenerator.init(256, SecureRandom.getInstanceStrong());
        // final SecretKey secretKey = keyGenerator.generateKey();

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

        final Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

        final Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, encryptCipher.getParameters());

        return new Crypter(encryptCipher, decryptCipher);
    }

    /**
     * AES-GCM Cipher can not be reused !
     */
    public static Crypter createAesGcm(final String password) throws GeneralSecurityException {
        // "AES/GCM/NoPadding", "AES/GCM/PKCS5Padding"
        final String algorithm = "AES/GCM/NoPadding";

        // final SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        // final SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG", "SUN");

        // final byte[] initVector = secureRandom.generateSeed(256);
        final byte[] initVector = SALT;

        // final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        // keyGenerator.init(256, secureRandom);
        // final SecretKey secretKey = keyGenerator.generateKey();
        final SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(password.getBytes(StandardCharsets.UTF_8), 32), "AES");

        final AlgorithmParameterSpec parameterSpec = new GCMParameterSpec(128, initVector);

        final Supplier<Cipher> encryptCipherSupplier = () -> {
            try {
                final Cipher encryptCipher = Cipher.getInstance(algorithm);
                encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec); // , secureRandom
                return encryptCipher;
            }
            catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        };

        final Supplier<Cipher> decryptCipherSupplier = () -> {
            try {
                final Cipher decryptCipher = Cipher.getInstance(algorithm);
                decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec); // , secureRandom
                return decryptCipher;
            }
            catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        };

        return new Crypter(encryptCipherSupplier, decryptCipherSupplier);
    }

    public static Crypter createBlowfish(final String password) throws GeneralSecurityException {
        // final KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
        // keyGenerator.init(448, SecureRandom.getInstanceStrong());
        // final SecretKey secretKey = keyGenerator.generateKey();

        final SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(password.getBytes(StandardCharsets.UTF_8), 56), "Blowfish");

        final Cipher encryptCipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

        final Cipher decryptCipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, encryptCipher.getParameters());

        return new Crypter(encryptCipher, decryptCipher);
    }

    public static Crypter createDes(final String password) throws GeneralSecurityException {
        // final KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        // keyGenerator.init(56, SecureRandom.getInstanceStrong());
        // final SecretKey secretKey = keyGenerator.generateKey();

        final SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(password.getBytes(StandardCharsets.UTF_8), 8), "DES");

        final Cipher encryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);

        final Cipher decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, encryptCipher.getParameters());

        return new Crypter(encryptCipher, decryptCipher);
    }

    private SymetricCrypto() {
        super();
    }
}
