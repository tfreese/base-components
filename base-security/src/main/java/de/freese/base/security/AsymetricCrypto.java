// Created: 07.03.24
package de.freese.base.security;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.function.Supplier;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
public final class AsymetricCrypto {
    /**
     * Needs BouncyCastleProvider<br>
     * <pre>{@code
     * if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
     *      Security.addProvider(new BouncyCastleProvider());
     * }
     * }</pre>
     */
    public static Crypter createEcda() throws GeneralSecurityException {
        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH"); // , BouncyCastleProvider.PROVIDER_NAME
        keyPairGenerator.initialize(new ECGenParameterSpec("secp384r1"), secureRandom); // KeyLength = 384

        final KeyPair keyPair = keyPairGenerator.genKeyPair();

        final Cipher encryptCipher = Cipher.getInstance("ECIES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

        final Cipher decryptCipher = Cipher.getInstance("ECIES");
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

        return new Crypter(encryptCipher, decryptCipher);
    }

    /**
     * Needs BouncyCastleProvider<br>
     * <pre>{@code
     * if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
     *      Security.addProvider(new BouncyCastleProvider());
     * }
     * }</pre>
     */
    public static Crypter createEcdhForAes() throws GeneralSecurityException {
        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH"); // , BouncyCastleProvider.PROVIDER_NAME
        keyPairGenerator.initialize(new ECGenParameterSpec("secp384r1"), secureRandom); // secp256r1
        // keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec("prime192v1"));
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();

        final byte[] initVector = secureRandom.generateSeed(256);
        // secureRandom.nextBytes(initVector);
        final AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(initVector);
        // final AlgorithmParameterSpec algorithmParameterSpec = new GCMParameterSpec(128, initVector);

        final KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH"); // , BouncyCastleProvider.PROVIDER_NAME
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(keyPair.getPublic(), true);
        final SecretKey secretKey = keyAgreement.generateSecret("AES");

        final String cipherAlgorithm = "AES/GCM/NoPadding";

        final Supplier<Cipher> encryptCipherSupplier = () -> {
            try {
                final Cipher encryptCipher = Cipher.getInstance(cipherAlgorithm); // , BouncyCastleProvider.PROVIDER_NAME
                encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, algorithmParameterSpec);
                return encryptCipher;
            }
            catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        };

        final Supplier<Cipher> decryptCipherSupplier = () -> {
            try {
                final Cipher decryptCipher = Cipher.getInstance(cipherAlgorithm); // , BouncyCastleProvider.PROVIDER_NAME
                decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, algorithmParameterSpec);
                return decryptCipher;
            }
            catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        };

        return new Crypter(encryptCipherSupplier, decryptCipherSupplier);
    }

    /**
     * Needs BouncyCastleProvider<br>
     * <pre>{@code
     * if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
     *      Security.addProvider(new BouncyCastleProvider());
     * }
     * }</pre>
     */
    public static Crypter createEcdsa() throws GeneralSecurityException {
        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp384r1"), secureRandom); // KeyLength = 384

        final KeyPair keyPair = keyPairGenerator.genKeyPair();

        final Cipher encryptCipher = Cipher.getInstance("ECIES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

        final Cipher decryptCipher = Cipher.getInstance("ECIES");
        decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

        return new Crypter(encryptCipher, decryptCipher);
    }

    public static Crypter createRsa(final int keySize) throws GeneralSecurityException {
        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
        keyPairGenerator.initialize(keySize, secureRandom);
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();

        return new Crypter(keyPair.getPublic(), keyPair.getPrivate());

        // final String cipherAlgorithm = "RSA/ECB/PKCS1Padding";
        // // final String cipherAlgorithm = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
        //
        // final Cipher encryptCipher = Cipher.getInstance(cipherAlgorithm);
        // encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic(), secureRandom);
        //
        // final Cipher decryptCipher = Cipher.getInstance(cipherAlgorithm);
        // decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(), secureRandom);
        //
        // return new Crypter(encryptCipher, decryptCipher);

        // final Supplier<Cipher> encryptCipherSupplier = () -> {
        //     try {
        //         final Cipher encryptCipher = Cipher.getInstance(cipherAlgorithm);
        //         encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic(), secureRandom);
        //         return encryptCipher;
        //     }
        //     catch (GeneralSecurityException ex) {
        //         throw new RuntimeException(ex);
        //     }
        // };
        //
        // final Supplier<Cipher> decryptCipherSupplier = () -> {
        //     try {
        //         final Cipher decryptCipher = Cipher.getInstance(cipherAlgorithm);
        //         decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(), secureRandom);
        //         return decryptCipher;
        //     }
        //     catch (GeneralSecurityException ex) {
        //         throw new RuntimeException(ex);
        //     }
        // };
        //
        // return new Crypter(encryptCipherSupplier, decryptCipherSupplier);
    }

    private AsymetricCrypto() {
        super();
    }
}
