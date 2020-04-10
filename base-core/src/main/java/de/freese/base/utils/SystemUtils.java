/**
 * Created: 09.04.2020
 */

package de.freese.base.utils;

/**
 * @author Thomas Freese
 */
public final class SystemUtils
{
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
