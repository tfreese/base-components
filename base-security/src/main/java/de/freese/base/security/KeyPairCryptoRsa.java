// Created: 25 Mai 2024
package de.freese.base.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import de.freese.base.utils.Encoding;

/**
 * @author Thomas Freese
 */
public final class KeyPairCryptoRsa implements Crypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static Crypto create(final int keySize) throws GeneralSecurityException {
        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize, secureRandom);

        final KeyPair keyPair = keyPairGenerator.generateKeyPair();

        return new KeyPairCryptoRsa(keyPair.getPublic(), keyPair.getPrivate());
    }

    private static Cipher initCipher(final int mode, final Key key) throws Exception {
        // String cipherAlgorithm = "RSA/None/OAEPWITHSHA-256ANDMGF1PADDING";
        // String cipherAlgorithm = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
        // String cipherAlgorithm = "RSA/ECB/PKCS1Padding";
        final Cipher cipher = Cipher.getInstance(key.getAlgorithm()); // "RSA"
        cipher.init(mode, key);

        return cipher;
    }

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public KeyPairCryptoRsa(final PublicKey publicKey, final PrivateKey privateKey) {
        super();

        this.publicKey = Objects.requireNonNull(publicKey, "publicKey required");
        this.privateKey = Objects.requireNonNull(privateKey, "privateKey required");
    }

    @Override
    public CipherInputStream decrypt(final InputStream inputStream) throws Exception {
        return new CipherInputStream(inputStream, initCipher(Cipher.DECRYPT_MODE, privateKey));
    }

    @Override
    public String decrypt(final String encrypted) throws Exception {
        final byte[] decoded = Encoding.BASE64.decode(encrypted);

        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, privateKey);
        final byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted, CHARSET);
    }

    @Override
    public CipherOutputStream encrypt(final OutputStream outputStream) throws Exception {
        return new CipherOutputStream(outputStream, initCipher(Cipher.ENCRYPT_MODE, publicKey));
    }

    @Override
    public String encrypt(final String message) throws Exception {
        final byte[] messageBytes = message.getBytes(CHARSET);

        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, publicKey);

        final byte[] encrypted = cipher.doFinal(messageBytes);

        return Encoding.BASE64.encode(encrypted);
    }
}
