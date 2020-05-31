/**
 * Created: 17.07.2012
 */
package de.freese.base.net;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.base.utils.NetUtils;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestNetUtils
{
    /**
     *
     */
    @BeforeAll
    public static void beforeAll()
    {
        // enableProxy();
    }

    /**
     * Aktiviert die Proxy-Kommunikation.
     */
    public static void enableProxy()
    {
        String proxy = "...";
        // DNS-Auflösung konfigurieren.
        // System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
        // System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
        // System.setProperty("sun.net.spi.nameservice.domain", "DOMAIN");

        // Proxy instance, proxy ip = 123.0.0.1 with port 8080
        // Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy, 8080));
        // URL url = new URL("http://www.yahoo.com");
        // HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
        // uc.connect();
        //
        // ProxySelector.getDefault().select(new URI("http://www.yahoo.com"));
        //
        // Use the default network proxies configured in the sytem.
        // System.setProperty("java.net.useSystemProxies", "true");
        // Proxy konfigurieren.
        // System.setProperty("proxySet", "true");
        System.setProperty("socksProxyHost", proxy);
        System.setProperty("socksProxyPort", "8080");
        System.setProperty("http.proxyHost", proxy);
        System.setProperty("http.proxyPort", "8080");
        // System.setProperty("http.proxyUser", "USER");
        // System.setProperty("http.proxyPassword", "...");
        // System.setProperty("http.auth.ntlm.domain", "DOMAIN");
        // System.setProperty("http.nonProxyHosts", "localhost|host1|host2");
        //
        System.setProperty("https.proxyHost", proxy);
        System.setProperty("https.proxyPort", "8080");
        // System.setProperty("https.proxyUser", "USER");
        // System.setProperty("https.proxyPassword", "...");
        // System.setProperty("https.auth.ntlm.domain", "DOMAIN");
        // System.setProperty("https.nonProxyHosts", "localhost|host1|host2");
        // java.net.Authenticator.setDefault(new java.net.Authenticator()
        // {
        // /**
        // * @see java.net.Authenticator#getPasswordAuthentication()
        // */
        // @Override
        // protected PasswordAuthentication getPasswordAuthentication()
        // {
        // return new PasswordAuthentication("USER", "PASSWORD".toCharArray());
        // }
        // });
    }

    /**
     * Erstellt ein neues {@link TestNetUtils} Object.
     */
    public TestNetUtils()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010LocalHost() throws Exception
    {
        String hostName = null;

        // hostName = InetAddress.getLocalHost().getHostName();

        // Cross Platform (Windows, Linux, Unix, Mac)
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("hostname").getInputStream())))
        {
            hostName = br.readLine();
        }

        assertNotNull(hostName);

        // List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements())
        {
            NetworkInterface nic = interfaces.nextElement();
            Enumeration<InetAddress> addresses = nic.getInetAddresses();

            while (addresses.hasMoreElements())
            {
                InetAddress address = addresses.nextElement();

                if (!address.isLoopbackAddress())
                {
                    hostName = address.getHostName();
                    assertNotNull(hostName);
                }
            }
        }
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020ValidHost() throws Exception
    {
        boolean validHost = NetUtils.isValidHost("ptbtime1.ptb.de");
        assertTrue(validHost);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030PTBZeit() throws Exception
    {
        Date ptbDate = NetUtils.getPTBZeit();
        assertNotNull(ptbDate);

        // System.out.println(String.format("%1$tY-%1$tm-%1$td %1$tT", ptbDate));
    }
}
