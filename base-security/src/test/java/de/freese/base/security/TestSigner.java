// Created: 10.03.24
package de.freese.base.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.freese.base.utils.Encoding;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
class TestSigner {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String SOURCE = "abcABC123,.;:-_ÖÄÜöäü*'#+`?ß´987/()=?";
    private static final byte[] SOURCE_BYTES = SOURCE.getBytes(CHARSET);

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @BeforeEach
    void beforeEach() throws GeneralSecurityException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024, SecureRandom.getInstanceStrong());
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    @Test
    void testSigner() throws GeneralSecurityException {
        for (Signer.Algorithm algorithm : Signer.Algorithm.values()) {
            System.out.println(algorithm);

            for (Encoding encoding : Encoding.values()) {
                final byte[] signedMessage = Signer.sign(SOURCE_BYTES, privateKey, Signer.Algorithm.SHA512_WITH_RSA);
                System.out.printf("%6s: %s%n", encoding, encoding.encode(signedMessage));

                assertTrue(Signer.verify(SOURCE_BYTES, signedMessage, publicKey, Signer.Algorithm.SHA512_WITH_RSA));
            }
        }
    }

    @Test
    void testSignerStream() throws GeneralSecurityException, IOException {
        for (Signer.Algorithm algorithm : Signer.Algorithm.values()) {
            System.out.println(algorithm);

            try (ByteArrayInputStream bais = new ByteArrayInputStream(SOURCE_BYTES)) {
                byte[] sig = null;

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    Signer.sign(bais, baos, privateKey, algorithm);
                    sig = baos.toByteArray();
                }

                bais.reset();

                for (Encoding encoding : Encoding.values()) {
                    System.out.printf("%6s: %s%n", encoding, encoding.encode(sig));
                }

                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(sig)) {
                    final boolean verified = Signer.verify(bais, inputStream, publicKey, algorithm);
                    assertTrue(verified);
                }
            }
        }
    }
}
