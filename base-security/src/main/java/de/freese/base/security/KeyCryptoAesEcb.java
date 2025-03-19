// Created: 23 Mai 2024
package de.freese.base.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import de.freese.base.utils.Encoding;

/**
 * @author Thomas Freese
 * @deprecated Use GCM instead
 */
@Deprecated(since = "now")
public final class KeyCryptoAesEcb implements Crypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String ENCRYPTION_ALGORITHM = "AES";

    public static Crypto create(final int keySize) throws GeneralSecurityException {
        final SecureRandom secureRandom = SecureRandom.getInstanceStrong();

        final KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(keySize, secureRandom);

        return new KeyCryptoAesEcb(keyGenerator.generateKey());
    }

    private static Cipher initCipher(final int mode, final SecretKey secretKey) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(mode, secretKey);

        return cipher;
    }

    private final SecretKey secretKey;

    public KeyCryptoAesEcb(final SecretKey secretKey) {
        super();

        this.secretKey = Objects.requireNonNull(secretKey, "secretKey required");
    }

    @Override
    public CipherInputStream decrypt(final InputStream inputStream) throws GeneralSecurityException, IOException {
        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey);

        return new CipherInputStream(inputStream, cipher);
    }

    @Override
    public String decrypt(final String encrypted) throws GeneralSecurityException {
        final byte[] decoded = Encoding.BASE64.decode(encrypted);

        final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey);

        final byte[] decrypted = cipher.doFinal(decoded);

        return new String(decrypted, CHARSET);
    }

    @Override
    public CipherOutputStream encrypt(final OutputStream outputStream) throws GeneralSecurityException, IOException {
        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey);

        return new CipherOutputStream(outputStream, cipher);
    }

    @Override
    public String encrypt(final String message) throws GeneralSecurityException {
        final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey);

        final byte[] encrypted = cipher.doFinal(message.getBytes(CHARSET));

        return Encoding.BASE64.encode(encrypted);
    }
}
