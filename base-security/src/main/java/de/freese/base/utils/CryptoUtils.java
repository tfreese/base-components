// Created: 01.04.2012
package de.freese.base.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
public final class CryptoUtils {
    public static KeyPair createDefaultKeyPair() throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String privateExponent = "5138544001051708225694705061407665599595524826747694658336007761333701981410937082375873446012307288200994165994472813360893870518885435"
                + "319090441822725441627690499320334435350300940494522749432667856172764307527519074586833262194381816873904322488655826752182824974144008854604942329535473894333"
                + "824580492858449586817256321528001177784102944939692303450460009397672572907290330325997136649637204048067905599347833963795575667224864970235692866430079232594"
                + "194041737862981069119546463156500442782695410091939765796914863721508943073164972981473120204021700164704286323063981331538511244466774976093164038768225765136"
                + "732771276948427033416875060472355686664634183118400774537971318346805832670217191059105608989575398609211903793924177737206669036945707241985029814455005977029"
                + "892970400710694787784129319430802378952606823364568350369250683922115128579165544808491814426165844279936442312003475166200913425765804114935763828328885006266"
                + "216279949980913521278797863211032467113597388411208573643934744497777881913124826337142017743483627181511300202840331809320872783161677380085846000363352644646"
                + "06715974317826409744743026617025506873702254177945246315338012719968104155501308523739732068584645075053640420904860968845642369289668394951909";
        final String modulus =
                "550692129898657141902038960655199549328229049385252832769229540095378520687012221097358458344330748690707822440076144701376713476356343555520138481735903828027"
                        + "9826892495182404014075142503841668744841463124530643321138639106691485833910559826461219221247001092437007677070761928999484093443536914883350477661998"
                        + "7665567106690429024444988267902230898646220593860627486371989123408266949183949646532745446066303281767776768211290192791122976240476710562468975470526"
                        + "9364260445647617805432092570305053732590707661817134743060383843151089910281240174445752408162631800259602381149780124297137702208572298483490185619625"
                        + "8010002756058076790910347954745195425522709408288383739719227847172638277880098293746241877218774993104655631984910833060274915241631786954117508688623"
                        + "3846043567617279238219286132958797936697202163259480548521343607227500514310975421245722721996224735411891394587500585594163747917379418697424341525711"
                        + "5561725051146326335100549968344994115127786093475945074647441651741901655909252256270502790950678162975148361066616669631317134418223398052703498488451"
                        + "9091801974559206647830227191100426710936911999624588380033219266359417331467648870031551365412511782176812354640288100909819117022533692770748245393877"
                        + "86241896740170521";
        final String exponent = "65537";

        final RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(exponent));
        final RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));

        final KeyFactory factory = KeyFactory.getInstance("RSA");

        return new KeyPair(factory.generatePublic(publicSpec), factory.generatePrivate(privateSpec));

    }

    public static byte[] decode(final Encoding encoding, final String value) {
        return switch (encoding) {
            case HEX -> HexFormat.of().parseHex(value);
            default -> Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8));
        };
    }

    public static String encode(final Encoding encoding, final byte[] data) {
        return switch (encoding) {
            case HEX -> HexFormat.of().withUpperCase().formatHex(data);
            default -> new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8);
        };
    }

    /**
     * This method returns the available implementations for a service type.
     */
    public static String[] getCryptoImpls(final String serviceType) {
        final Set<String> result = new TreeSet<>();

        // All  providers
        final Provider[] providers = Security.getProviders();

        for (Provider provider : providers) {
            // Get services provided by each provider
            final Set<Object> keys = provider.keySet();

            for (Object key : keys) {
                String k = (String) key;
                k = k.split(" ")[0];
                String impl = null;

                if ("Provider".equals(serviceType) && k.startsWith("Provider.id")) {
                    impl = provider.getName();
                }
                else if (k.startsWith(serviceType + ".")) {
                    impl = k.substring(serviceType.length() + 1);
                }
                else if (k.startsWith("Alg.Alias." + serviceType + ".")) {
                    // This is an alias
                    impl = k.substring(serviceType.length() + 11);
                }

                if (impl != null && !impl.isEmpty()) {
                    result.add(impl);
                }
            }
        }

        return result.toArray(new String[0]);
    }

    public static String[] getServiceTypes() {
        final Set<String> result = new TreeSet<>();

        // All  providers
        final Provider[] providers = Security.getProviders();

        for (Provider provider : providers) {
            // Get services provided by each provider
            final Set<Object> keys = provider.keySet();

            for (Object key : keys) {
                String k = (String) key;
                k = k.split(" ")[0];

                if (k.startsWith("Alg.Alias.")) {
                    // Strip the alias
                    k = k.substring(10);
                }

                final int ix = k.indexOf('.');
                result.add(k.substring(0, ix));
            }
        }

        return result.toArray(new String[0]);
    }

    public static PrivateKey loadPrivateKey(final Path file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] privateKey = Files.readAllBytes(file);

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
    }

    public static PublicKey loadPublicKey(final Path file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] publicKey = Files.readAllBytes(file);

        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
    }

    public static void savePrivateKey(final Path file, final PrivateKey privateKey) throws IOException {
        // Saves the private key encoded in PKCS #8
        final PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());

        Files.write(file, privateSpec.getEncoded());
    }

    public static void savePublicKey(final Path file, final PublicKey publicKey) throws IOException {
        // Saves the public key encoded in X.509
        final X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey.getEncoded());

        Files.write(file, publicSpec.getEncoded());
    }

    /**
     * Start TLS on an existing socket. Supports the "STARTTLS" command in many protocols.
     */
    public static Socket startSSL(final Socket socket) throws IOException {
        final InetAddress hostAddress = socket.getInetAddress();
        final int port = socket.getPort();
        final SSLSocket sslSocket;

        final SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        sslSocket = (SSLSocket) socketFactory.createSocket(hostAddress, port);
        sslSocket.setEnabledProtocols(new String[]{"TLSv1.3"});

        return sslSocket;
    }

    private CryptoUtils() {
        super();
    }
}
