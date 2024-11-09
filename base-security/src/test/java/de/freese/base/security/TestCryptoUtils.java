// Created: 01.04.2012

package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.utils.CryptoUtils;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestCryptoUtils {
    private static final boolean DEBUG = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCryptoUtils.class);

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
                LOGGER.info("{}: MaxAllowedKeyLength={}", value, Cipher.getMaxAllowedKeyLength(value));
            }
            catch (Exception ex) {
                LOGGER.error("{}: {}", value, ex.getMessage());
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

        LOGGER.info("Public key {}", keyPair.getPublic());
        LOGGER.info("Private key {}", keyPair.getPrivate());

        final RSAPublicKey rsaPub = (RSAPublicKey) keyPair.getPublic();
        final BigInteger publicKeyModulus = rsaPub.getModulus();
        final BigInteger publicKeyExponent = rsaPub.getPublicExponent();

        LOGGER.info("publicKeyModulus: {}", publicKeyModulus);
        LOGGER.info("publicKeyExponent: {}", publicKeyExponent);

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
            LOGGER.info(value);
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
            LOGGER.info(value);
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
            LOGGER.info(value);
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
            LOGGER.info(value);
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
            LOGGER.info(value);
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
            LOGGER.info(value);
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
            LOGGER.info(value);
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
            LOGGER.info(value);
        }
    }
}
