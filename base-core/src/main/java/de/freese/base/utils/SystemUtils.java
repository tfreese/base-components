// Created: 09.04.2020
package de.freese.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class SystemUtils
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUtils.class);

    /**
     * @return String
     */
    public static String getJavaHome()
    {
        return System.getProperty("java.home");
    }

    /**
     * @return String
     */
    public static String getJavaIoTmpDir()
    {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Liefert die Java-Version als zusammenhängende Zahl.
     *
     * @return int, Beispiel 1800072
     */
    public static int getJavaVersion()
    {
        // String javaVersion = SystemUtils.JAVA_VERSION;
        String javaVersion = System.getProperty("java.version");
        String[] splits = javaVersion.toLowerCase().split("[._]");

        // Major
        String versionString = String.format("%03d", Integer.parseInt(splits[0]));

        // Minor
        versionString += "." + String.format("%03d", Integer.parseInt(splits[1]));

        if (splits.length > 2)
        {
            // Micro
            versionString += "." + String.format("%03d", Integer.parseInt(splits[2]));
        }

        if ((splits.length > 3) && !splits[3].startsWith("ea"))
        {
            // Update
            try
            {
                versionString += "." + String.format("%03d", Integer.parseInt(splits[3]));
            }
            catch (Exception ex)
            {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        return Integer.parseInt(versionString.replace(".", ""));
    }

    /**
     * @return String
     */
    public static String getOsArch()
    {
        return System.getProperty("os.arch");
    }

    /**
     * @return String
     */
    public static String getOsName()
    {
        return System.getProperty("os.name");
    }

    /**
     * @return String
     */
    public static String getUserDir()
    {
        return System.getProperty("user.dir");
    }

    /**
     * @return String
     */
    public static String getUserHome()
    {
        return System.getProperty("user.home");
    }

    /**
     * @return boolean
     */
    public static boolean isLinux()
    {
        String os = getOsName().toLowerCase();

        return os.contains("linux");
    }

    /**
     * @return boolean
     */
    public static boolean isUnix()
    {
        String os = getOsName().toLowerCase();

        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    /**
     * @return boolean
     */
    public static boolean isWindows()
    {
        String os = getOsName().toLowerCase();

        return os.startsWith("win");
    }

    /**
     * Erstellt ein neues {@link SystemUtils} Object.
     */
    private SystemUtils()
    {
        super();
    }
}
