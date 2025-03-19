// Created: 25 Mai 2024
package de.freese.base.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.freese.base.utils.Encoding;

/**
 * Needs BouncyCastleProvider<br>
 * <pre>{@code
 * if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
 *      Security.addProvider(new BouncyCastleProvider());
 * }
 * }</pre>
 *
 * @author Thomas Freese
 */
public final class KeyPairCryptoEcdhForAes implements Crypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static Crypto create() throws GeneralSecurityException {
        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH"); // , BouncyCastleProvider.PROVIDER_NAME
        keyPairGenerator.initialize(new ECGenParameterSpec("secp384r1"), secureRandom); // secp384r1, secp256r1
        // keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec("prime192v1"));

        final KeyPair keyPair = keyPairGenerator.generateKeyPair();

        final byte[] initVector = secureRandom.generateSeed(256);
        // secureRandom.nextBytes(initVector);

        final KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH"); // , BouncyCastleProvider.PROVIDER_NAME
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(keyPair.getPublic(), true);
        final SecretKey secretKey = keyAgreement.generateSecret("AES");

        return new KeyPairCryptoEcdhForAes(secretKey, initVector);
    }

    private static Cipher initCipher(final int mode, final SecretKey secretKey, final byte[] initVector) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, secretKey, new IvParameterSpec(initVector));

        return cipher;
    }

    private final byte[] initVector;
    private final SecretKey secretKey;

    public KeyPairCryptoEcdhForAes(final SecretKey secretKey, final byte[] initVector) {
        super();

        this.secretKey = Objects.requireNonNull(secretKey, "secretKey required");
        this.initVector = Objects.requireNonNull(initVector, "initVector required");
    }

    @Override
    public CipherInputStream decrypt(final InputStream inputStream) throws GeneralSecurityException, IOException {
        return new CipherInputStream(inputStream, initCipher(Cipher.DECRYPT_MODE, secretKey, initVector));
    }

    @Override
    public String decrypt(final String encrypted) throws GeneralSecurityException {
        final byte[] decoded = Encoding.BASE64.decode(encrypted);

        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, initVector);

        final byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted, CHARSET);
    }

    @Override
    public CipherOutputStream encrypt(final OutputStream outputStream) throws GeneralSecurityException, IOException {
        return new CipherOutputStream(outputStream, initCipher(Cipher.ENCRYPT_MODE, secretKey, initVector));
    }

    @Override
    public String encrypt(final String message) throws GeneralSecurityException {
        final byte[] messageBytes = message.getBytes(CHARSET);

        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, initVector);

        final byte[] encrypted = cipher.doFinal(messageBytes);

        return Encoding.BASE64.encode(encrypted);
    }
}
