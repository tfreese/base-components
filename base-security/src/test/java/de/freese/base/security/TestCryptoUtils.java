// Created: 01.04.2012

package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import de.freese.base.utils.CryptoUtils;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestCryptoUtils {
    private static final boolean DEBUG = true;

    @BeforeAll
    static void beforeAll() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Test
    void testCipher() {
        final String[] values = CryptoUtils.getCryptoImpls("Cipher");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG) {
            return;
        }

        for (String value : values) {
            try {
                System.out.println(value + ": MaxAllowedKeyLength=" + Cipher.getMaxAllowedKeyLength(value));
            }
            catch (Exception ex) {
                System.err.println(value + ": " + ex.getMessage());
            }
        }
    }

    @Test
    void testCreateRsaKeyPair() throws Exception {
        // SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG", "SUN");
        //
        // KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
        // keyPairGenerator.initialize(4096, secureRandom);
        //
        // KeyPair keyPair = keyPairGenerator.genKeyPair();

        final KeyPair keyPair = CryptoUtils.createDefaultKeyPair();

        System.out.println("Public key " + keyPair.getPublic());
        System.out.println();
        System.out.println("Private key " + keyPair.getPrivate());

        final RSAPublicKey rsaPub = (RSAPublicKey) keyPair.getPublic();
        final BigInteger publicKeyModulus = rsaPub.getModulus();
        final BigInteger publicKeyExponent = rsaPub.getPublicExponent();

        System.out.println();
        System.out.println("publicKeyModulus: " + publicKeyModulus);
        System.out.println("publicKeyExponent: " + publicKeyExponent);

        assertTrue(true);
    }

    @Test
    void testKeyManagerFactory() {
        final String[] values = CryptoUtils.getCryptoImpls("KeyManagerFactory");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG) {
            return;
        }

        for (String value : values) {
            System.out.println(value);
        }
    }

    @Test
    void testKeyPairGenerator() {
        final String[] values = CryptoUtils.getCryptoImpls("KeyPairGenerator");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG) {
            return;
        }

        for (String value : values) {
            System.out.println(value);
        }
    }

    @Test
    void testMessageDigest() {
        final String[] values = CryptoUtils.getCryptoImpls("MessageDigest");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG) {
            return;
        }

        for (String value : values) {
            System.out.println(value);
        }
    }

    @Test
    void testProvider() {
        final String[] values = CryptoUtils.getCryptoImpls("Provider");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG) {
            return;
        }

        for (String value : values) {
            System.out.println(value);
        }
    }

    @Test
    void testSecureRandom() {
        final String[] values = CryptoUtils.getCryptoImpls("SecureRandom");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG) {
            return;
        }

        for (String value : values) {
            System.out.println(value);
        }
    }

    @Test
    void testServiceTypes() {
        final String[] values = CryptoUtils.getServiceTypes();
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG) {
            return;
        }

        for (String value : values) {
            System.out.println(value);
        }
    }

    @Test
    void testSignature() {
        final String[] values = CryptoUtils.getCryptoImpls("Signature");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG) {
            return;
        }

        for (String value : values) {
            System.out.println(value);
        }
    }

    @Test
    void testTrustManagerFactory() {
        final String[] values = CryptoUtils.getCryptoImpls("TrustManagerFactory");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG) {
            return;
        }

        for (String value : values) {
            System.out.println(value);
        }
    }
}
