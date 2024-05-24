// Created: 13.06.2011
package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;

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
class TestCrypter {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String SOURCE = "abcABC123,.;:-_ÖÄÜöäü*'#+`?ß´987/()=?";
    private static final byte[] SOURCE_BYTES = SOURCE.getBytes(CHARSET);

    @Test
    void testAsymetricEcda() throws GeneralSecurityException {
        // Required by ECDSA
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        testCrypter(AsymetricCrypto.createEcda());
    }

    @Test
    void testAsymetricEcdhForAes() throws GeneralSecurityException {
        // Required by ECDH
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        testCrypter(AsymetricCrypto.createEcdhForAes());
    }

    @Test
    void testAsymetricEcdsa() throws GeneralSecurityException {
        // Required by ECDSA
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        testCrypter(AsymetricCrypto.createEcdsa());
    }

    @Test
    void testAsymetricRsa() throws GeneralSecurityException {
        testCrypter(AsymetricCrypto.createRsa(512)); // 512, 1024, 4096, 16384
    }

    @Test
    void testSymetricAesCbc() throws Exception {
        final String password = "password";

        final String cipherText1 = CryptoAesCbc.encrypt(password, SOURCE);
        final String cipherText2 = CryptoAesCbc.encrypt(password, SOURCE);

        assertNotEquals(cipherText1, cipherText2);

        assertEquals(SOURCE, CryptoAesCbc.decrypt(password, cipherText1));
        assertEquals(SOURCE, CryptoAesCbc.decrypt(password, cipherText2));
    }

    @Test
    void testSymetricAesGcm() throws Exception {
        final String password = "password";

        final String cipherText1 = CryptoAesGcm.encrypt(password, SOURCE);
        final String cipherText2 = CryptoAesGcm.encrypt(password, SOURCE);

        assertNotEquals(cipherText1, cipherText2);

        assertEquals(SOURCE, CryptoAesGcm.decrypt(password, cipherText1));
        assertEquals(SOURCE, CryptoAesGcm.decrypt(password, cipherText2));
    }

    @Test
    void testSymetricBlowfish() throws Exception {
        final String password = "password";

        final String cipherText1 = CryptoBlowFish.encrypt(password, SOURCE);
        final String cipherText2 = CryptoBlowFish.encrypt(password, SOURCE);

        assertNotEquals(cipherText1, cipherText2);

        assertEquals(SOURCE, CryptoBlowFish.decrypt(password, cipherText1));
        assertEquals(SOURCE, CryptoBlowFish.decrypt(password, cipherText2));
    }

    @Test
    void testSymetricDes() throws Exception {
        final String password = "password";

        final String cipherText1 = CryptoDes.encrypt(password, SOURCE);
        final String cipherText2 = CryptoDes.encrypt(password, SOURCE);

        assertNotEquals(cipherText1, cipherText2);

        assertEquals(SOURCE, CryptoDes.decrypt(password, cipherText1));
        assertEquals(SOURCE, CryptoDes.decrypt(password, cipherText2));
    }

    @Test
    void textSymetricCrypto() throws GeneralSecurityException {
        // Required by PBEWITHSHA256AND256BITAES-CBC-BC
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        for (SymetricAlgorithm algorithm : SymetricAlgorithm.values()) {
            System.out.println(algorithm);

            testCrypter(SymetricCrypto.create("password", algorithm));
        }
    }

    private void testCrypter(final Crypter crypter) throws GeneralSecurityException {
        for (Encoding encoding : Encoding.values()) {
            final String encrypted = crypter.encrypt(SOURCE, encoding);
            System.out.printf("%6s: %s%n", encoding, encrypted);

            assertEquals(SOURCE, crypter.decrypt(encrypted, encoding));
        }
    }
}
