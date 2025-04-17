// Created: 25 Mai 2024
package de.freese.base.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import java.security.interfaces.RSAKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

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

    private static Cipher initCipher(final int mode, final Key key) throws GeneralSecurityException {
        final String cipherAlgorithm = key.getAlgorithm(); // "RSA"
        // final String cipherAlgorithm = "RSA/None/OAEPWITHSHA-1ANDMGF1PADDING";
        // final String cipherAlgorithm = "RSA/None/OAEPWITHSHA-256ANDMGF1PADDING";
        // final String cipherAlgorithm = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";
        // final String cipherAlgorithm = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
        // final String cipherAlgorithm = "RSA/ECB/PKCS1Padding";

        final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
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
    public CipherInputStream decrypt(final InputStream inputStream) throws GeneralSecurityException, IOException {
        return new CipherInputStream(inputStream, initCipher(Cipher.DECRYPT_MODE, privateKey));
    }

    @Override
    public String decrypt(final String encrypted) throws GeneralSecurityException {
        final Base64.Decoder decoder = Base64.getDecoder();

        final byte[] decoded = decoder.decode(encrypted);

        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, privateKey);

        // final int blockSize = (((RSAKey) privateKey).getModulus().bitLength() / 8) - 11;
        final int blockSize = ((RSAKey) privateKey).getModulus().bitLength() / 8;

        // RSA is not designed for large Text.
        if (decoded.length <= blockSize) {
            final byte[] decrypted = cipher.doFinal(decoded);
            final byte[] decryptedDecoded = decoder.decode(decrypted);

            return new String(decryptedDecoded, CHARSET);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[blockSize];
            int read;

            while ((read = bais.read(buffer, 0, blockSize)) >= 0) {
                baos.write(cipher.doFinal(buffer, 0, read));
            }

            baos.flush();

            final byte[] decryptedDecoded = decoder.decode(baos.toByteArray());

            return new String(decryptedDecoded, CHARSET);
        }
        catch (IOException ex) {
            throw new GeneralSecurityException(ex);
        }
    }

    @Override
    public CipherOutputStream encrypt(final OutputStream outputStream) throws GeneralSecurityException, IOException {
        return new CipherOutputStream(outputStream, initCipher(Cipher.ENCRYPT_MODE, publicKey));
    }

    @Override
    public String encrypt(final String message) throws GeneralSecurityException {
        final Base64.Encoder encoder = Base64.getEncoder();

        final byte[] messageBytes = encoder.encode(message.getBytes(CHARSET));

        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, publicKey);

        final int blockSize = (((RSAKey) publicKey).getModulus().bitLength() / 8) - 11;

        // RSA is not designed for large Text.
        if (messageBytes.length <= blockSize) {
            final byte[] encrypted = cipher.doFinal(messageBytes);

            return encoder.encodeToString(encrypted);
        }

        int position = 0;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(messageBytes.length)) {
            while (position < messageBytes.length) {
                final byte[] chunk = Arrays.copyOfRange(messageBytes, position, Math.min(position + blockSize, messageBytes.length));

                baos.write(cipher.doFinal(chunk));

                position += blockSize;
            }

            baos.flush();

            return encoder.encodeToString(baos.toByteArray());
        }
        catch (IOException ex) {
            throw new GeneralSecurityException(ex);
        }
    }
}
