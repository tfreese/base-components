// Created: 25 Mai 2024
package de.freese.base.security;

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
import java.security.spec.ECGenParameterSpec;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

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
public final class KeyPairCryptoEcdsa implements Crypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static Crypto create() throws GeneralSecurityException {
        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC"); // , BouncyCastleProvider.PROVIDER_NAME
        keyPairGenerator.initialize(new ECGenParameterSpec("secp384r1"), secureRandom); // secp384r1, secp256r1

        final KeyPair keyPair = keyPairGenerator.generateKeyPair();

        return new KeyPairCryptoEcdsa(keyPair.getPublic(), keyPair.getPrivate());
    }

    private static Cipher initCipher(final int mode, final Key key) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("ECIES/None/NoPadding");
        cipher.init(mode, key);

        return cipher;
    }

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public KeyPairCryptoEcdsa(final PublicKey publicKey, final PrivateKey privateKey) {
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
        final byte[] decoded = Encoding.BASE64.decode(encrypted);

        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, privateKey);

        final byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted, CHARSET);
    }

    @Override
    public CipherOutputStream encrypt(final OutputStream outputStream) throws GeneralSecurityException, IOException {
        return new CipherOutputStream(outputStream, initCipher(Cipher.ENCRYPT_MODE, publicKey));
    }

    @Override
    public String encrypt(final String message) throws GeneralSecurityException {
        final byte[] messageBytes = message.getBytes(CHARSET);

        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, publicKey);

        final byte[] encrypted = cipher.doFinal(messageBytes);

        return Encoding.BASE64.encode(encrypted);
    }
}
