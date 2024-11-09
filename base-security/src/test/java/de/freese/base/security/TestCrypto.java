// Created: 13.06.2011
package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.utils.Encoding;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestCrypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCrypto.class);
    private static final String PASSWORD = "password";
    private static final String SOURCE = "abcABC123,.;:-_ÖÄÜöäü*'#+`?ß´987/()=?";
    private static final byte[] SOURCE_BYTES = SOURCE.getBytes(CHARSET);

    @Test
    void testAes() throws Exception {
        // final Key key = new SecretKeySpec(SecureRandom.getInstanceStrong().generateSeed(256 / 8), "AES");

        final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, SecureRandom.getInstanceStrong());
        final SecretKey secretKey = keyGenerator.generateKey();

        final Crypto cryptoAes = new Crypto() {
            private static final int IV_LENGTH = 16;

            @Override
            public CipherInputStream decrypt(final InputStream inputStream) throws Exception {
                final byte[] iv = new byte[IV_LENGTH];
                inputStream.read(iv);

                final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);

                return new CipherInputStream(inputStream, cipher);
            }

            @Override
            public String decrypt(final String encrypted) throws Exception {
                final byte[] decoded = Encoding.BASE64.decode(encrypted);
                final byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
                final byte[] encryptedBytes = Arrays.copyOfRange(decoded, IV_LENGTH, decoded.length);

                // final ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
                //
                // final byte[] iv = new byte[16];
                // byteBuffer.get(iv);
                //
                // final byte[] encryptedBytes = new byte[byteBuffer.remaining()];
                // byteBuffer.get(encryptedBytes);

                final Cipher cipher = initCipher(Cipher.DECRYPT_MODE, secretKey, iv);
                final byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

                return new String(decryptedBytes, CHARSET);
            }

            @Override
            public CipherOutputStream encrypt(final OutputStream outputStream) throws Exception {
                final byte[] iv = SecureRandom.getInstanceStrong().generateSeed(IV_LENGTH);
                final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);

                // prefix IV
                outputStream.write(iv);

                return new CipherOutputStream(outputStream, cipher);
            }

            @Override
            public String encrypt(final String message) throws Exception {
                final byte[] iv = SecureRandom.getInstanceStrong().generateSeed(IV_LENGTH);
                final Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, secretKey, iv);
                final byte[] encryptedBytes = cipher.doFinal(message.getBytes(CHARSET));

                final byte[] encryptedBytesWithIv = new byte[iv.length + encryptedBytes.length];
                System.arraycopy(iv, 0, encryptedBytesWithIv, 0, iv.length);
                System.arraycopy(encryptedBytes, 0, encryptedBytesWithIv, iv.length, encryptedBytes.length);

                // final byte[] encryptedBytesWithIv = ByteBuffer.allocate(iv.length + encryptedBytes.length)
                //         .put(iv)
                //         .put(encryptedBytes)
                //         .array();

                return Encoding.BASE64.encode(encryptedBytesWithIv);
            }

            private Cipher initCipher(final int mode, final Key key, final byte[] iv) throws Exception {
                // final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                // cipher.init(mode, key, new IvParameterSpec(iv));

                final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(mode, key, new GCMParameterSpec(128, iv));

                return cipher;
            }
        };

        testCrypto(cryptoAes);
    }

    @Test
    void testAsymetricEcc() throws Exception {
        // Required by EC
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        testCrypto(KeyPairCryptoEcc.create(384));
    }

    @Test
    void testAsymetricEcda() throws Exception {
        // Required by ECDSA
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        testCrypto(KeyPairCryptoEcda.create());
    }

    @Test
    void testAsymetricEcdhForAes() throws Exception {
        // Required by ECDH
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        testCrypto(KeyPairCryptoEcdhForAes.create());
    }

    @Test
    void testAsymetricEcdsa() throws Exception {
        // Required by ECDSA
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        testCrypto(KeyPairCryptoEcdsa.create());
    }

    @Test
    void testAsymetricRsa() throws Exception {
        testCrypto(KeyPairCryptoRsa.create(512)); // 512, 1024, 4096, 16384
    }

    @Test
    void testSymetricAesCbc() throws Exception {
        testCrypto(new PbeCryptoAesCbc(PASSWORD));
    }

    @Test
    void testSymetricAesEcb() throws Exception {
        testCrypto(KeyCryptoAesEcb.create(256));
    }

    @Test
    void testSymetricAesGcm() throws Exception {
        testCrypto(new PbeCryptoAesGcm(PASSWORD));
    }

    @Test
    void testSymetricBlowfish() throws Exception {
        testCrypto(new PbeCryptoBlowFish(PASSWORD));
    }

    @Test
    void testSymetricDes() throws Exception {
        testCrypto(new PbeCryptoDes(PASSWORD));
    }

    @Test
    void textSymetricCrypto() throws Exception {
        // Required by PBEWITHSHA256AND256BITAES-CBC-BC
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        for (PbeCryptoAlgorithm.Algorithm algorithm : PbeCryptoAlgorithm.Algorithm.values()) {
            LOGGER.info("{}", algorithm);

            testCrypto(new PbeCryptoAlgorithm(PASSWORD, algorithm));
        }
    }

    private void testCrypto(final Crypto crypto) throws Exception {
        String cipherText1 = crypto.encrypt(SOURCE);
        String cipherText2 = crypto.encrypt(SOURCE);

        if (crypto instanceof KeyCryptoAesEcb) {
            assertEquals(cipherText1, cipherText2);
        }
        else if (!(crypto instanceof KeyPairCryptoEcdhForAes)) {
            assertNotEquals(cipherText1, cipherText2);
        }

        assertEquals(SOURCE, crypto.decrypt(cipherText1));
        assertEquals(SOURCE, crypto.decrypt(cipherText2));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // {@link CipherOutputStream#close()} is the Trigger for {@link Cipher#doFinal()}.
        try (CipherOutputStream cipherOutputStream = crypto.encrypt(baos)) {
            cipherOutputStream.write(SOURCE_BYTES);
            cipherOutputStream.flush();
        }

        cipherText1 = Encoding.BASE64.encode(baos.toByteArray());

        baos = new ByteArrayOutputStream();

        // {@link CipherOutputStream#close()} is the Trigger for {@link Cipher#doFinal()}.
        try (CipherOutputStream cipherOutputStream = crypto.encrypt(baos)) {
            cipherOutputStream.write(SOURCE_BYTES);
            cipherOutputStream.flush();
        }

        cipherText2 = Encoding.BASE64.encode(baos.toByteArray());

        if (crypto instanceof KeyCryptoAesEcb) {
            assertEquals(cipherText1, cipherText2);
        }
        else if (!(crypto instanceof KeyPairCryptoEcdhForAes)) {
            assertNotEquals(cipherText1, cipherText2);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(Encoding.BASE64.decode(cipherText1));
             CipherInputStream cipherInputStream = crypto.decrypt(bais)) {
            final String decrypted = new String(cipherInputStream.readAllBytes(), CHARSET);
            assertEquals(SOURCE, decrypted);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(Encoding.BASE64.decode(cipherText2));
             CipherInputStream cipherInputStream = crypto.decrypt(bais)) {
            final String decrypted = new String(cipherInputStream.readAllBytes(), CHARSET);
            assertEquals(SOURCE, decrypted);
        }
    }
}
