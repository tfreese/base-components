package de.freese.base.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Enumeration;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * @author Thomas Freese
 */
public final class NetUtils {
    /**
     * Wandelt die binäre IP-Adresse in ein lesbares Format um.
     */
    public static String convertAddressToDotRepresentation(final byte[] ipAddress) {
        final StringBuilder ipAddressStr = new StringBuilder();

        for (int i = 0; i < ipAddress.length; i++) {
            if (i > 0) {
                ipAddressStr.append(".");
            }

            ipAddressStr.append(ipAddress[i] & 0xFF);
        }

        return ipAddressStr.toString();
    }

    public static String getHostName() {
        String hostName = null;

        try {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (Exception ex) {
            // Bei Betriebssystemen ohne DNS-Konfiguration funktioniert InetAddress.getLocalHost nicht !
        }

        if (hostName == null) {
            // Cross Platform (Windows, Linux, Unix, Mac)
            try (BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"hostname"}).getInputStream(), StandardCharsets.UTF_8))) {
                hostName = br.readLine();
            }
            catch (Exception ex) {
                // Ignore
            }
        }

        if (hostName == null) {
            try {
                // final List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

                while (interfaces.hasMoreElements()) {
                    final NetworkInterface nic = interfaces.nextElement();

                    // nic.getInterfaceAddresses().forEach(System.out::println);

                    // final Stream<InetAddress> addresses = nic.inetAddresses();
                    final Enumeration<InetAddress> addresses = nic.getInetAddresses();

                    while (addresses.hasMoreElements()) {
                        final InetAddress address = addresses.nextElement();

                        if ((!address.isLoopbackAddress() && address instanceof Inet4Address)
                                || !address.isLinkLocalAddress()) {
                            // IPv4
                            hostName = address.getHostName();
                            break;
                        }

                    }
                }
            }
            catch (Exception ex) {
                // Ignore
            }
        }

        return hostName;
    }

    public static LocalDateTime getNtpTime(final String host, final int port) throws IOException {
        final InetAddress inetAddress = InetAddress.getByName(host);

        try (NTPUDPClient timeClient = new NTPUDPClient()) {
            final TimeInfo timeInfo = timeClient.getTime(inetAddress, port);
            final long timeStamp = timeInfo.getMessage().getTransmitTimeStamp().getTime();

            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
        }
    }

    /**
     * Liefert die Zeit der Atomuhr in der Physikalisch-Technischen Bundesanstalt.
     */
    public static LocalDateTime getPtbTime() throws Exception {
        final int NTP_PORT = 123; // 37

        for (int i = 1; i <= 3; i++) {
            final String host = String.format("ptbtime%d.ptb.de", i);

            try {
                return getNtpTime(host, NTP_PORT);
            }
            catch (Exception ex) {
                // Ignore
            }
        }

        return LocalDateTime.now();
    }

    /**
     * NSLookUp
     */
    public static boolean isValidHost(final String host) {
        boolean isValid = false;

        try {
            InetAddress.getByName("www." + host);

            isValid = true;
        }
        catch (UnknownHostException ex) {
            // Ignore
        }

        if (!isValid) {
            try {
                InetAddress.getByName(host);

                isValid = true;
            }
            catch (UnknownHostException ex) {
                // Ignore
            }
        }

        return isValid;
    }

    private NetUtils() {
        super();
    }
}
