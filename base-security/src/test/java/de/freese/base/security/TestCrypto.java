// Created: 13.06.2011
package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Security;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import de.freese.base.utils.Encoding;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestCrypto {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String PASSWORD = "password";
    private static final String SOURCE = "abcABC123,.;:-_ÖÄÜöäü*'#+`?ß´987/()=?";
    private static final byte[] SOURCE_BYTES = SOURCE.getBytes(CHARSET);

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
            System.out.println(algorithm);

            testCrypto(new PbeCryptoAlgorithm(PASSWORD, algorithm));
        }
    }

    private void testCrypto(final Crypto crypto) throws Exception {
        final String cipherText1 = crypto.encrypt(SOURCE);
        final String cipherText2 = crypto.encrypt(SOURCE);

        if (!(crypto instanceof KeyPairCryptoEcdhForAes)) {
            assertNotEquals(cipherText1, cipherText2);
        }

        assertEquals(SOURCE, crypto.decrypt(cipherText1));
        assertEquals(SOURCE, crypto.decrypt(cipherText2));
    }

    private void testCrypto(final CryptoKeyPair cryptoKeyPair) throws Exception {
        testCrypto((Crypto) cryptoKeyPair);

        final String cipherText1;
        final String cipherText2;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // {@link CipherOutputStream#close()} is the Trigger for {@link Cipher#doFinal()}.
        try (CipherOutputStream cipherOutputStream = cryptoKeyPair.encrypt(baos)) {
            cipherOutputStream.write(SOURCE_BYTES);
            cipherOutputStream.flush();
        }

        cipherText1 = Encoding.BASE64.encode(baos.toByteArray());

        baos = new ByteArrayOutputStream();

        // {@link CipherOutputStream#close()} is the Trigger for {@link Cipher#doFinal()}.
        try (CipherOutputStream cipherOutputStream = cryptoKeyPair.encrypt(baos)) {
            cipherOutputStream.write(SOURCE_BYTES);
            cipherOutputStream.flush();
        }

        cipherText2 = Encoding.BASE64.encode(baos.toByteArray());

        if (!(cryptoKeyPair instanceof KeyPairCryptoEcdhForAes)) {
            assertNotEquals(cipherText1, cipherText2);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(Encoding.BASE64.decode(cipherText1));
             CipherInputStream cipherInputStream = cryptoKeyPair.decrypt(bais)) {
            final String decrypted = new String(cipherInputStream.readAllBytes(), CHARSET);
            assertEquals(SOURCE, decrypted);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(Encoding.BASE64.decode(cipherText2));
             CipherInputStream cipherInputStream = cryptoKeyPair.decrypt(bais)) {
            final String decrypted = new String(cipherInputStream.readAllBytes(), CHARSET);
            assertEquals(SOURCE, decrypted);
        }
    }
}
