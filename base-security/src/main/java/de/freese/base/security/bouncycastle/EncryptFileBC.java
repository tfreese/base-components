package de.freese.base.security.bouncycastle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verschlüsselt Verzeichnisse/Dateien mit einem {@link X509Certificate} des BouncyCastleProviders.
 *
 * @author Svetlana Margolina (eck*cellent)
 * @author Thomas Freese
 */
public class EncryptFileBC
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptFileBC.class);

    /**
     * Erstellt ein neues {@link EncryptFileBC} Objekt.
     */
    public EncryptFileBC()
    {
        super();

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
        {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Verschlüsselt die Datei mit einem {@link X509Certificate}.
     *
     * @param decryptedFile String
     * @param encryptedFile String
     * @param zertifikatFile String
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void encryptX509File(final String decryptedFile, final String encryptedFile, final String zertifikatFile) throws Exception
    {
        X509Certificate cert = getCertificate(zertifikatFile);

        encryptX509File(decryptedFile, encryptedFile, cert);
    }

    /**
     * Verschlüsselt die Datei mit einem {@link X509Certificate}.
     *
     * @param decryptedFile String
     * @param encryptedFile String
     * @param keystoreFile String
     * @param keyStorePassword char[]
     * @param alias String, Zertifikat
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void encryptX509File(final String decryptedFile, final String encryptedFile, final String keystoreFile, final char[] keyStorePassword,
                                final String alias)
            throws Exception
    {
        X509Certificate cert = getCertificate(keystoreFile, keyStorePassword, alias);

        encryptX509File(decryptedFile, encryptedFile, cert);
    }

    /**
     * Verschlüsselt die Datei mit einem {@link X509Certificate}.
     *
     * @param decryptedFile String
     * @param encryptedFile String
     * @param cert {@link X509Certificate}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void encryptX509File(final String decryptedFile, final String encryptedFile, final X509Certificate cert) throws Exception
    {
        File file = new File(decryptedFile);

        if (file.isDirectory())
        {
            LOGGER.warn("Skipping Folder: {}", decryptedFile);
            return;
        }

        if (!file.canRead())
        {
            String msg = String.format("unable to read file %s", decryptedFile);
            throw new IOException(msg);
        }

        LOGGER.info("Encrypt File \"{}\" to \"{}\" with \"{}\"", decryptedFile, encryptedFile, cert.getSubjectX500Principal());

        byte[] data = Files.readAllBytes(file.toPath());

        CMSTypedData msg = new CMSProcessableByteArray(data);
        CMSEnvelopedDataGenerator edGen = new CMSEnvelopedDataGenerator();
        edGen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(cert));

        CMSEnvelopedData ed =
                edGen.generate(msg, new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC).setProvider(BouncyCastleProvider.PROVIDER_NAME).build());

        try (OutputStream os = Files.newOutputStream(new File(encryptedFile).toPath()))
        {
            os.write(ed.getEncoded());
        }
    }

    /**
     * Verschlüsselt alle Dateien innerhalb des Verzeichnisses mit einem {@link X509Certificate}<br>
     * OHNE Unterverzeichnisse.
     *
     * @param inputFolder String
     * @param outputFolder String
     * @param zertifikatFile String
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void encryptX509Folder(final String inputFolder, final String outputFolder, final String zertifikatFile) throws Exception
    {
        X509Certificate cert = getCertificate(zertifikatFile);

        encryptX509Folder(inputFolder, outputFolder, cert);
    }

    /**
     * Verschlüsselt alle Dateien innerhalb des Verzeichnisses mit einem {@link X509Certificate}<br>
     * OHNE Unterverzeichnisse.
     *
     * @param inputFolder String
     * @param outputFolder String
     * @param keystoreFile String
     * @param keyStorePassword char[]
     * @param alias String, Zertifikat
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void encryptX509Folder(final String inputFolder, final String outputFolder, final String keystoreFile, final char[] keyStorePassword,
                                  final String alias)
            throws Exception
    {
        X509Certificate cert = getCertificate(keystoreFile, keyStorePassword, alias);

        encryptX509Folder(inputFolder, outputFolder, cert);
    }

    /**
     * Verschlüsselt alle Dateien innerhalb des Verzeichnisses mit einem {@link X509Certificate}<br>
     * OHNE Unterverzeichnisse.
     *
     * @param inputFolder String
     * @param outputFolder String
     * @param cert {@link X509Certificate}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void encryptX509Folder(final String inputFolder, final String outputFolder, final X509Certificate cert) throws Exception
    {
        File folder = new File(inputFolder);

        if (!folder.canRead())
        {
            String msg = String.format("unable to read folder %s", inputFolder);
            throw new IOException(msg);
        }

        String[] files = folder.list();

        for (String name : files)
        {
            String inputFile = inputFolder + File.separator + name;
            String encryptedFile = outputFolder + File.separator + "Encrypted_" + name;

            encryptX509File(inputFile, encryptedFile, cert);
        }
    }

    /**
     * Laden des {@link X509Certificate}s.
     *
     * @param zertifikatFile String
     *
     * @return {@link X509Certificate}
     *
     * @throws Exception Falls was schiefgeht.
     */
    private X509Certificate getCertificate(final String zertifikatFile) throws Exception
    {
        Certificate cert = null;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zertifikatFile)))
        {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            while (bis.available() > 0)
            {
                cert = cf.generateCertificate(bis);
            }
        }

        return (X509Certificate) cert;
    }

    /**
     * Laden des {@link X509Certificate}s.
     *
     * @param keystoreFile String
     * @param keyStorePassword char[]
     * @param alias String, Zertifikat
     *
     * @return {@link X509Certificate}
     *
     * @throws Exception Falls was schiefgeht.
     */
    private X509Certificate getCertificate(final String keystoreFile, final char[] keyStorePassword, final String alias) throws Exception
    {
        KeyStore ks = KeyStore.getInstance("PKCS12");// , BouncyCastleProvider.PROVIDER_NAME);

        try (FileInputStream fis = new FileInputStream(keystoreFile))
        {
            ks.load(fis, keyStorePassword);
        }

        Certificate cert = ks.getCertificate(alias);

        return (X509Certificate) cert;
    }
}
