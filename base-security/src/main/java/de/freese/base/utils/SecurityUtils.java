// Created: 01.04.2012
package de.freese.base.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.Provider;
import java.security.Security;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author Thomas Freese
 */
public final class SecurityUtils {
    public static KeyPair createDefaultKeyPair() throws Exception {
        String privateExponent = "513854400105170822569470506140766559959552482674769465833600776133370198141093708237587344601230728820099416599447281336089387051888543531909044182272544162769049932033443535030094049452274943266785617276430752751907458683326219438181687390432248865582675218282497414400885460494232953547389433382458049285844958681725632152800117778410294493969230345046000939767257290729033032599713664963720404806790559934783396379557566722486497023569286643007923259419404173786298106911954646315650044278269541009193976579691486372150894307316497298147312020402170016470428632306398133153851124446677497609316403876822576513673277127694842703341687506047235568666463418311840077453797131834680583267021719105910560898957539860921190379392417773720666903694570724198502981445500597702989297040071069478778412931943080237895260682336456835036925068392211512857916554480849181442616584427993644231200347516620091342576580411493576382832888500626621627994998091352127879786321103246711359738841120857364393474449777788191312482633714201774348362718151130020284033180932087278316167738008584600036335264464606715974317826409744743026617025506873702254177945246315338012719968104155501308523739732068584645075053640420904860968845642369289668394951909";
        String modulus = "550692129898657141902038960655199549328229049385252832769229540095378520687012221097358458344330748690707822440076144701376713476356343555520138481735903828027982689249518240401407514250384166874484146312453064332113863910669148583391055982646121922124700109243700767707076192899948409344353691488335047766199876655671066904290244449882679022308986462205938606274863719891234082669491839496465327454460663032817677767682112901927911229762404767105624689754705269364260445647617805432092570305053732590707661817134743060383843151089910281240174445752408162631800259602381149780124297137702208572298483490185619625801000275605807679091034795474519542552270940828838373971922784717263827788009829374624187721877499310465563198491083306027491524163178695411750868862338460435676172792382192861329587979366972021632594805485213436072275005143109754212457227219962247354118913945875005855941637479173794186974243415257115561725051146326335100549968344994115127786093475945074647441651741901655909252256270502790950678162975148361066616669631317134418223398052703498488451909180197455920664783022719110042671093691199962458838003321926635941733146764887003155136541251178217681235464028810090981911702253369277074824539387786241896740170521";
        String exponent = "65537";

        RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(exponent));
        RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));

        KeyFactory factory = KeyFactory.getInstance("RSA");

        return new KeyPair(factory.generatePublic(publicSpec), factory.generatePrivate(privateSpec));

    }

    /**
     * This method returns the available implementations for a service type.
     *
     * @param serviceType String, @see {@link #getServiceTypes()}
     */
    public static String[] getCryptoImpls(final String serviceType) {
        Set<String> result = new TreeSet<>();

        // All  providers
        Provider[] providers = Security.getProviders();

        for (Provider provider : providers) {
            // Get services provided by each provider
            Set<Object> keys = provider.keySet();

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

                if ((impl != null) && (impl.length() > 0)) {
                    result.add(impl);
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    public static String[] getServiceTypes() {
        Set<String> result = new TreeSet<>();

        // All  providers
        Provider[] providers = Security.getProviders();

        for (Provider provider : providers) {
            // Get services provided by each provider
            Set<Object> keys = provider.keySet();

            for (Object key : keys) {
                String k = (String) key;
                k = k.split(" ")[0];

                if (k.startsWith("Alg.Alias.")) {
                    // Strip the alias
                    k = k.substring(10);
                }

                int ix = k.indexOf('.');
                result.add(k.substring(0, ix));
            }
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * Start TLS on an existing socket. Supports the "STARTTLS" command in many protocols.
     */
    public static Socket startSSL(final Socket socket) throws IOException {
        InetAddress hostAddress = socket.getInetAddress();
        int port = socket.getPort();
        SSLSocket sslSocket;

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        sslSocket = (SSLSocket) socketFactory.createSocket(hostAddress, port);
        sslSocket.setEnabledProtocols(new String[]{"TLSv1.3"});

        return sslSocket;
    }

    private SecurityUtils() {
        super();
    }
}
