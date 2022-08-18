// Created: 01.04.2012

package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

import de.freese.base.utils.SecurityUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestSecurityUtils
{
    /**
     *
     */
    private static final boolean DEBUG = true;

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
        {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     *
     */
    @Test
    void testCipher()
    {
        String[] values = SecurityUtils.getCryptoImpls("Cipher");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG)
        {
            return;
        }

        for (String value : values)
        {
            try
            {
                System.out.println(value + ": MaxAllowedKeyLength=" + Cipher.getMaxAllowedKeyLength(value));
            }
            catch (Exception ex)
            {
                System.err.println(value + ": " + ex.getMessage());
            }
        }
    }

    /**
     * @throws Exception Falls was schiefgeht.
     */
    @Test
    void testCreateRsaKeyPair() throws Exception
    {
        // SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG", "SUN");
        //
        // KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
        // keyPairGenerator.initialize(4096, secureRandom);
        //
        // KeyPair keyPair = keyPairGenerator.genKeyPair();

        KeyPair keyPair = SecurityUtils.createDefaultKeyPair();

        System.out.println("Public key " + keyPair.getPublic());
        System.out.println();
        System.out.println("Private key " + keyPair.getPrivate());

        RSAPublicKey rsaPub = (RSAPublicKey) keyPair.getPublic();
        BigInteger publicKeyModulus = rsaPub.getModulus();
        BigInteger publicKeyExponent = rsaPub.getPublicExponent();

        System.out.println();
        System.out.println("publicKeyModulus: " + publicKeyModulus);
        System.out.println("publicKeyExponent: " + publicKeyExponent);

        assertTrue(true);
    }

    /**
     *
     */
    @Test
    void testKeyManagerFactory()
    {
        String[] values = SecurityUtils.getCryptoImpls("KeyManagerFactory");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG)
        {
            return;
        }

        for (String value : values)
        {
            System.out.println(value);
        }
    }

    /**
     *
     */
    @Test
    void testKeyPairGenerator()
    {
        String[] values = SecurityUtils.getCryptoImpls("KeyPairGenerator");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG)
        {
            return;
        }

        for (String value : values)
        {
            System.out.println(value);
        }
    }

    /**
     *
     */
    @Test
    void testMessageDigest()
    {
        String[] values = SecurityUtils.getCryptoImpls("MessageDigest");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG)
        {
            return;
        }

        for (String value : values)
        {
            System.out.println(value);
        }
    }

    /**
     *
     */
    @Test
    void testProvider()
    {
        String[] values = SecurityUtils.getCryptoImpls("Provider");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG)
        {
            return;
        }

        for (String value : values)
        {
            System.out.println(value);
        }
    }

    /**
     *
     */
    @Test
    void testSecureRandom()
    {
        String[] values = SecurityUtils.getCryptoImpls("SecureRandom");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG)
        {
            return;
        }

        for (String value : values)
        {
            System.out.println(value);
        }
    }

    /**
     *
     */
    @Test
    void testServiceTypes()
    {
        String[] values = SecurityUtils.getServiceTypes();
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG)
        {
            return;
        }

        for (String value : values)
        {
            System.out.println(value);
        }
    }

    /**
     *
     */
    @Test
    void testSignature()
    {
        String[] values = SecurityUtils.getCryptoImpls("Signature");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG)
        {
            return;
        }

        for (String value : values)
        {
            System.out.println(value);
        }
    }

    /**
     *
     */
    @Test
    void testTrustManagerFactory()
    {
        String[] values = SecurityUtils.getCryptoImpls("TrustManagerFactory");
        assertNotNull(values);
        assertTrue(values.length > 0);

        if (!DEBUG)
        {
            return;
        }

        for (String value : values)
        {
            System.out.println(value);
        }
    }
}
