package de.freese.base.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * @author Thomas Freese
 */
public final class NetUtils
{
    /**
     * Wandelt die binäre IP-Adresse in ein lesbares Format um.
     *
     * @param ipAddr byte[]
     *
     * @return String
     */
    public static String convertAddressToDotRepresentation(final byte[] ipAddr)
    {
        StringBuilder ipAddrStr = new StringBuilder();

        for (int i = 0; i < ipAddr.length; i++)
        {
            if (i > 0)
            {
                ipAddrStr.append(".");
            }

            ipAddrStr.append(ipAddr[i] & 0xFF);
        }

        return ipAddrStr.toString();
    }

    /**
     * @return String
     */
    public static String getHostName()
    {
        String hostName = null;

        try
        {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (Exception ex)
        {
            // Bei Betriebssystemen ohne DNS-Konfiguration funktioniert InetAddress.getLocalHost nicht !
        }

        if (hostName == null)
        {
            // Cross Platform (Windows, Linux, Unix, Mac)
            try (BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"hostname"}).getInputStream(), StandardCharsets.UTF_8)))
            {
                hostName = br.readLine();
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        if (hostName == null)
        {
            try
            {
                // List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

                while (interfaces.hasMoreElements())
                {
                    NetworkInterface nic = interfaces.nextElement();

                    // nic.getInterfaceAddresses().forEach(System.out::println);

                    // Stream<InetAddress> addresses = nic.inetAddresses();
                    Enumeration<InetAddress> addresses = nic.getInetAddresses();

                    while (addresses.hasMoreElements())
                    {
                        InetAddress address = addresses.nextElement();

                        if (!address.isLoopbackAddress() && (address instanceof Inet4Address))
                        {
                            // IPv4
                            hostName = address.getHostName();
                            break;
                        }
                        else if (!address.isLoopbackAddress() && !address.isLinkLocalAddress())
                        {
                            // IPv6
                            hostName = address.getHostName();
                            break;
                        }
                        else if (!address.isLoopbackAddress())
                        {
                            // IPv6-Link
                            hostName = address.getHostName();
                            break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        return hostName;
    }

    /**
     * Liefert die Zeit der Atomuhr in der Physikalisch-Technischen Bundesanstalt.
     *
     * @return {@link Date}
     *
     * @throws IOException Falls was schiefgeht.
     */
    public static Date getPTBZeit() throws IOException
    {
        final int NTP_PORT = 123; // 37
        IOException exception = null;
        Date date = null;

        for (int i = 1; i <= 3; i++)
        {
            String host = String.format("ptbtime%d.ptb.de", i);
            InetAddress inetAddress = InetAddress.getByName(host);

            try
            {
                NTPUDPClient timeClient = new NTPUDPClient();
                TimeInfo timeInfo = timeClient.getTime(inetAddress, NTP_PORT);
                long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                date = new Date(returnTime);

                timeClient.close();

                break;
            }
            catch (IOException ex)
            {
                exception = ex;
            }

            // try (Socket so = new Socket(inetAddress, 37))
            // {
            // try (InputStream inputStream = so.getInputStream())
            // {
            // long returnTime = 0L;
            //
            // for (int b = 3; b >= 0; b--)
            // {
            // int value = inputStream.read();
            // returnTime ^= (long) value << (b * 8);
            // }
            //
            // // Der Time-Server gibt die Sekunden seit 1900 aus, Java erwartet Millisekunden seit 1970.
            // date = new Date((returnTime - 2208988800L) * 1000);
            //
            // break;
            // }
            // }
            // catch (IOException ex)
            // {
            // exception = ex;
            // }
        }

        if ((date == null) && (exception != null))
        {
            throw exception;
        }

        return date;
    }

    /**
     * NSLookUp
     *
     * @param host String
     *
     * @return boolean
     */
    public static boolean isValidHost(final String host)
    {
        boolean isValid = false;

        try
        {
            InetAddress.getByName("www." + host);

            isValid = true;
        }
        catch (UnknownHostException ex)
        {
            // Ignore
        }

        if (!isValid)
        {
            try
            {
                InetAddress.getByName(host);

                isValid = true;
            }
            catch (UnknownHostException ex)
            {
                // Ignore
            }
        }

        return isValid;
    }

    /**
     * Erstellt ein neues {@link NetUtils} Object.
     */
    private NetUtils()
    {
        super();
    }
}
