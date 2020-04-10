package de.freese.base.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * Toolkit zum Arbeiten mit Sockets etc.
 *
 * @author Thomas Freese
 */
public final class NetUtils
{
    /**
     * Wandelt die binaere IP Addresse in ein lesbares Format um.
     *
     * @param ipAddr byte[]
     * @return String
     */
    public static String convertAddressToDotRepresentation(final byte[] ipAddr)
    {
        String ipAddrStr = "";

        for (int i = 0; i < ipAddr.length; i++)
        {
            if (i > 0)
            {
                ipAddrStr += ".";
            }

            ipAddrStr += (ipAddr[i] & 0xFF);
        }

        return ipAddrStr;
    }

    /**
     * Liefert die Zeit der Atomuhr in der Physikalisch Technischen Bundesanstalt.
     *
     * @return {@link Date}
     * @throws IOException Falls was schief geht.
     */
    public static Date getPTBZeit() throws IOException
    {
        final int NTP_PORT = 123; // 37
        IOException exception = null;
        Date date = null;

        for (int i = 1; i <= 3; i++)
        {
            String host = String.format("ptbtime%d.ptb.de", Integer.valueOf(i));
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
