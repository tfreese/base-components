// Created: 13.11.22
package de.freese.base.security.bouncycastle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class PgpCryptoBcMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(PgpCryptoBcMain.class);

    public static void main(final String[] args) throws Exception {
        // KeyID = -3266217749052483355
        LOGGER.info("{}", Long.decode("0x09DFF54A96322AD9"));
        LOGGER.info("{}", new BigInteger("09DFF54A96322AD9", 16).longValue());
        LOGGER.info("{}", new BigInteger("D2AC10432CA698E5", 16).longValue());

        // pubRingDump("/home/tommy/.gnupg/pubring.gpg");

        final PgpCryptoBc codec = new PgpCryptoBc();

        LOGGER.info("Encrypt");
        final PGPPublicKey publicKey = codec.findPublicKey("/home/tommy/.gnupg/pubring.gpg", "96322AD9");
        // PGPSecretKey secretKey = codec.readSecretKey("/home/tommy/.gnupg/secring.gpg");
        // codec.signEncryptFile("/tmp/conkyrc.gpg", "/home/tommy/.conkyrc", publicKey, secretKey, args[0].toCharArray(), false, true);
        codec.encryptFile("/tmp/conkyrc.gpg", "/home/tommy/.conkyrc", publicKey, false, true);

        // LOGGER.info("Verify");
        // codec.verifyFile(new FileInputStream("/tmp/conkyrc.gpg"), new FileInputStream("/home/tommy/.gnupg/pubring.gpg"), "/tmp/extraContent.txt");

        LOGGER.info("Decrypt");

        try (InputStream in = new FileInputStream("/tmp/conkyrc.gpg");
             OutputStream out = new FileOutputStream("/tmp/test.txt");
             InputStream keyIn = new FileInputStream("/home/tommy/.gnupg/secring.gpg")) {
            codec.decryptFile(in, out, keyIn, args[0].toCharArray());
        }
    }

    private PgpCryptoBcMain() {
        super();
    }
}
