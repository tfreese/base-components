// Created: 13.06.2011
package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testAsymetricEllipticCurve() throws GeneralSecurityException {
        // Required by ECDH
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        testCrypter(AsymetricCrypto.createEllipticCurve());
    }

    @Test
    void testAsymetricRsa() throws GeneralSecurityException {
        testCrypter(AsymetricCrypto.createRsa(512));
    }

    @Test
    void testSymetricAesCbc() throws GeneralSecurityException {
        testCrypter(SymetricCrypto.createAesCbc("password"));
    }

    @Test
    void testSymetricAesGcm() throws GeneralSecurityException {
        testCrypter(SymetricCrypto.createAesGcm("password"));
    }

    @Test
    void testSymetricBlowfish() throws GeneralSecurityException {
        testCrypter(SymetricCrypto.createBlowfish("password"));
    }

    @Test
    void testSymetricDes() throws GeneralSecurityException {
        testCrypter(SymetricCrypto.createDes("password"));
    }

    @Test
    void textSymetricCrypto() throws GeneralSecurityException {
        // Required by PBEWITHSHA256AND256BITAES-CBC-BC
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        for (SymetricCrypto.Algorithm algorithm : SymetricCrypto.Algorithm.values()) {
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
