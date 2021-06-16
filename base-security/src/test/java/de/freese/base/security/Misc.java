//// Created: 16.06.2021
// package de.freese.base.security;
//
// import java.math.BigInteger;
// import java.security.KeyPair;
// import java.security.KeyPairGenerator;
// import java.security.PrivateKey;
// import java.security.SecureRandom;
// import java.security.cert.CertificateException;
// import java.time.LocalDate;
// import java.time.ZoneId;
// import java.util.Base64;
// import java.util.Date;
//
// import sun.security.x509.AlgorithmId;
// import sun.security.x509.CertificateAlgorithmId;
// import sun.security.x509.CertificateIssuerName;
// import sun.security.x509.CertificateSerialNumber;
// import sun.security.x509.CertificateSubjectName;
// import sun.security.x509.CertificateValidity;
// import sun.security.x509.CertificateVersion;
// import sun.security.x509.CertificateX509Key;
// import sun.security.x509.X500Name;
// import sun.security.x509.X509CertImpl;
// import sun.security.x509.X509CertInfo;
//
/// **
// * Siehe maven-compiler-plugin in pom.xml
// *
// * @author Thomas Freese
// */
// public class Misc
// {
// /**
// * Geklaut von io.netty.handler.ssl.util.SelfSignedCertificate.
// */
// static void cryptoSelfSignedCert() throws Exception
// {
// SecureRandom secureRandom = new SecureRandom();
// String fqdn = "Localhost";
// Date notBefore = Date.from(LocalDate.of(2021, 01, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
// Date notAfter = Date.from(LocalDate.of(9999, 12, 31).atStartOfDay(ZoneId.systemDefault()).toInstant());
//
// KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
// keyGen.initialize(4096, secureRandom);
// KeyPair keypair = keyGen.generateKeyPair();
//
// PrivateKey key = keypair.getPrivate();
//
// // Prepare the information required for generating an X.509 certificate.
// X509CertInfo certInfo = new X509CertInfo();
// X500Name owner = new X500Name("CN=" + fqdn);
// certInfo.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
// certInfo.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new BigInteger(64, secureRandom)));
//
// try
// {
// certInfo.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
// }
// catch (CertificateException ignore)
// {
// certInfo.set(X509CertInfo.SUBJECT, owner);
// }
//
// try
// {
// certInfo.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
// }
// catch (CertificateException ignore)
// {
// certInfo.set(X509CertInfo.ISSUER, owner);
// }
//
// certInfo.set(X509CertInfo.VALIDITY, new CertificateValidity(notBefore, notAfter));
// certInfo.set(X509CertInfo.KEY, new CertificateX509Key(keypair.getPublic()));
//
// // KnownOIDs.SHA256withRSA("1.2.840.113549.1.1.11")
// // KnownOIDs.SHA384withRSA("1.2.840.113549.1.1.12")
// // KnownOIDs.SHA512withRSA("1.2.840.113549.1.1.13"),
// certInfo.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(AlgorithmId.get("1.2.840.113549.1.1.13")));
//
// // Sign the cert to identify the algorithm that's used.
// X509CertImpl cert = new X509CertImpl(certInfo);
// cert.sign(key, "SHA512withRSA");
//
// // Update the algorithm and sign again.
// certInfo.set(CertificateAlgorithmId.NAME + '.' + CertificateAlgorithmId.ALGORITHM, cert.get(X509CertImpl.SIG_ALG));
// cert = new X509CertImpl(certInfo);
// cert.sign(key, "SHA512withRSA");
// cert.verify(keypair.getPublic());
//
// // Dump private Key -> File private.key
// String keyText =
// "-----BEGIN PRIVATE KEY-----\n" + Base64.getEncoder().encodeToString(keypair.getPrivate().getEncoded()) + "\n-----END PRIVATE KEY-----\n";
//
// System.out.println(keyText);
// System.out.println();
//
// // Dump Certificate -> File cert.crt
// // Encode the certificate into a CRT file.
// String certText = "-----BEGIN CERTIFICATE-----\n" + Base64.getEncoder().encodeToString(cert.getEncoded()) + "\n-----END CERTIFICATE-----\n";
//
// System.out.println(certText);
// }
//
// /**
// * @param args String[]
// */
// public static void main(final String[] args) throws Exception
// {
// cryptoSelfSignedCert();
// }
// }
