// Created: 07.03.24
package de.freese.base.security;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

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

    public static Crypter create(final String password, final SymetricAlgorithm algorithm) throws GeneralSecurityException {
        return create(password, algorithm.getAlgorithmName());
    }

    public static Crypter create(final String password, final String algorithm) throws GeneralSecurityException {
        return create(password, algorithm, algorithm);
    }

    public static Crypter create(final String password, final String algorithmKeyFactory, final String algorithmCipher) throws GeneralSecurityException {
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), SALT, ITERATIONS, KEY_LENGTH);
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithmKeyFactory);
        final SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        // final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(pbeKeySpec.getSalt(), pbeKeySpec.getIterationCount());

        final Cipher encryptCipher = Cipher.getInstance(algorithmCipher);
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new PBEParameterSpec(SALT, ITERATIONS));

        final Cipher decryptCipher = Cipher.getInstance(algorithmCipher);
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, encryptCipher.getParameters());

        return new Crypter(encryptCipher, decryptCipher);
    }

    private SymetricCrypto() {
        super();
    }
}
