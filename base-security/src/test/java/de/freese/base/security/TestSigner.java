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
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
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

    private KeyPair keyPairEcc;
    private KeyPair keyPairRsa;

    @BeforeEach
    void beforeEach() throws GeneralSecurityException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024, SecureRandom.getInstanceStrong());
        keyPairRsa = keyPairGenerator.generateKeyPair();

        // Required by ECDSA
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(256); // ECC: 256, 384, 521
        keyPairEcc = keyPairGenerator.generateKeyPair();
    }

    @Test
    void testSigner() throws GeneralSecurityException {
        for (Signer.Algorithm algorithm : Signer.Algorithm.values()) {
            System.out.println(algorithm);

            KeyPair keyPair = keyPairRsa;

            if (Signer.Algorithm.SHA256_WITH_ECDSA.equals(algorithm)) {
                keyPair = keyPairEcc;
            }

            for (Encoding encoding : Encoding.values()) {
                final byte[] signedMessage = Signer.sign(SOURCE_BYTES, keyPair.getPrivate(), algorithm);
                System.out.printf("%6s: %s%n", encoding, encoding.encode(signedMessage));

                assertTrue(Signer.verify(SOURCE_BYTES, signedMessage, keyPair.getPublic(), algorithm));
            }
        }
    }

    @Test
    void testSignerStream() throws GeneralSecurityException, IOException {
        for (Signer.Algorithm algorithm : Signer.Algorithm.values()) {
            System.out.println(algorithm);

            KeyPair keyPair = keyPairRsa;

            if (Signer.Algorithm.SHA256_WITH_ECDSA.equals(algorithm)) {
                keyPair = keyPairEcc;
            }

            try (ByteArrayInputStream bais = new ByteArrayInputStream(SOURCE_BYTES)) {
                byte[] sig = null;

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    Signer.sign(bais, baos, keyPair.getPrivate(), algorithm);
                    sig = baos.toByteArray();
                }

                bais.reset();

                for (Encoding encoding : Encoding.values()) {
                    System.out.printf("%6s: %s%n", encoding, encoding.encode(sig));
                }

                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(sig)) {
                    final boolean verified = Signer.verify(bais, inputStream, keyPair.getPublic(), algorithm);
                    assertTrue(verified);
                }
            }
        }
    }
}
