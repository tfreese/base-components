// Created: 17.07.2012
package de.freese.base.net;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.util.Enumeration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.utils.NetUtils;

/**
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestNetUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestNetUtils.class);

    @BeforeAll
    static void beforeAll() {
        // enableProxy();
    }

    /**
     * Aktiviert die Proxy-Kommunikation.
     */
    static void enableProxy() {
        final String proxy = "...";
        // DNS-Auflösung konfigurieren.
        // System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
        // System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
        // System.setProperty("sun.net.spi.nameservice.domain", "DOMAIN");

        // Proxy instance, proxy ip = 123.0.0.1 with port 8080
        // final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy, 8080));
        // URL url = new URL("http://www.yahoo.com");
        // final HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
        // uc.connect();
        //
        // ProxySelector.getDefault().select(new URI("http://www.yahoo.com"));
        //
        // Use the default network proxies configured in the system.
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
        // java.net.Authenticator.setDefault(new java.net.Authenticator() {
        // @Override
        // protected PasswordAuthentication getPasswordAuthentication() {
        // return new PasswordAuthentication("USER", "PASSWORD".toCharArray());
        // }
        // });
    }

    @Test
    void testLocalHost() {
        String hostName = null;

        try {
            hostName = InetAddress.getLocalHost().getHostName();
            assertNotNull(hostName);
            LOGGER.info("InetAddress.getLocalHost: {}", hostName);
        }
        catch (Exception ex) {
            // Bei Betriebssystemen ohne DNS-Konfiguration funktioniert InetAddress.getLocalHost nicht !
            LOGGER.info("InetAddress.getLocalHost: {}", ex.getMessage());
        }

        // Cross Platform (Windows, Linux, Unix, Mac)
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"hostname"}).getInputStream()))) {
            hostName = br.readLine();
            assertNotNull(hostName);
            LOGGER.info("CMD 'hostname': {}", hostName);
        }
        catch (Exception ex) {
            LOGGER.error("CMD 'hostname': {}", ex.getMessage());
        }

        try {
            // List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                final NetworkInterface nic = interfaces.nextElement();

                // nic.getInterfaceAddresses().forEach(System.out::println);

                // Stream<InetAddress> addresses = nic.inetAddresses();
                final Enumeration<InetAddress> addresses = nic.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    final InetAddress address = addresses.nextElement();

                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        hostName = address.getHostName();
                        assertNotNull(hostName);
                        LOGGER.info("NetworkInterface IPv4: {}", hostName);
                    }
                    else if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                        hostName = address.getHostName();
                        assertNotNull(hostName);
                        LOGGER.info("NetworkInterface IPv6: {}", hostName);
                    }
                    else if (!address.isLoopbackAddress()) {
                        hostName = address.getHostName();
                        assertNotNull(hostName);
                        LOGGER.info("NetworkInterface IPv6 Link: {}", hostName);
                    }
                }
            }
        }
        catch (Exception ex) {
            // Ignore
        }
    }

    @Test
    void testPtbZeit() throws Exception {
        final LocalDateTime ptbTime = NetUtils.getPtbTime();
        assertNotNull(ptbTime);

        //        System.out.printf("%1$tY-%1$tm-%1$td %1$tT%n", ptbTime);
    }

    @Test
    void testValidHost() {
        final boolean validHost = NetUtils.isValidHost("ptbtime1.ptb.de");
        assertTrue(validHost);
    }
}
