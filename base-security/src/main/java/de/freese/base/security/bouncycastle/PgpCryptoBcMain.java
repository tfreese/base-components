// Created: 13.11.22
package de.freese.base.security.bouncycastle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import org.bouncycastle.openpgp.PGPPublicKey;

/**
 * @author Thomas Freese
 */
public final class PgpCryptoBcMain {
    public static void main(final String[] args) throws Exception {
        // KeyID = -3266217749052483355
        System.out.println(Long.decode("0x09DFF54A96322AD9"));
        System.out.println(new BigInteger("09DFF54A96322AD9", 16).longValue());
        System.out.println(new BigInteger("D2AC10432CA698E5", 16).longValue());

        // pubRingDump("/home/tommy/.gnupg/pubring.gpg");

        final PgpCryptoBc codec = new PgpCryptoBc();

        System.out.println("Encrypt");
        final PGPPublicKey publicKey = codec.findPublicKey("/home/tommy/.gnupg/pubring.gpg", "96322AD9");
        // PGPSecretKey secretKey = codec.readSecretKey("/home/tommy/.gnupg/secring.gpg");
        // codec.signEncryptFile("/tmp/conkyrc.gpg", "/home/tommy/.conkyrc", publicKey, secretKey, args[0].toCharArray(), false, true);
        codec.encryptFile("/tmp/conkyrc.gpg", "/home/tommy/.conkyrc", publicKey, false, true);

        // System.out.println("Verify");
        // codec.verifyFile(new FileInputStream("/tmp/conkyrc.gpg"), new FileInputStream("/home/tommy/.gnupg/pubring.gpg"), "/tmp/extraContent.txt");

        System.out.println("Decrypt");

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
