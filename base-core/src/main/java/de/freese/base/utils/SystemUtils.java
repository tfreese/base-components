// Created: 09.04.2020
package de.freese.base.utils;

/**
 * @author Thomas Freese
 */
public final class SystemUtils {
    // private static final Logger LOGGER = LoggerFactory.getLogger(SystemUtils.class);

    public static String getJavaHome() {
        return System.getProperty("java.home");
    }

    public static String getJavaIoTmpDir() {
        return System.getProperty("java.io.tmpdir");
    }

    public static int getJavaVersion() {
        return Runtime.version().feature();

        //        Runtime.class.getPackage().getImplementationVersion();

        //        // String javaVersion = SystemUtils.JAVA_VERSION;
        //        final String javaVersion = System.getProperty("java.version");
        //        final String[] splits = javaVersion.toLowerCase().split("[._]");
        //
        //        // Major
        //        String versionString = String.format("%03d", Integer.parseInt(splits[0]));
        //
        //        // Minor
        //        versionString += "." + String.format("%03d", Integer.parseInt(splits[1]));
        //
        //        if (splits.length > 2) {
        //            // Micro
        //            versionString += "." + String.format("%03d", Integer.parseInt(splits[2]));
        //        }
        //
        //        if ((splits.length > 3) && !splits[3].startsWith("ea")) {
        //            // Update
        //            try {
        //                versionString += "." + String.format("%03d", Integer.parseInt(splits[3]));
        //            }
        //            catch (Exception ex) {
        //                LOGGER.error(ex.getMessage(), ex);
        //            }
        //        }
        //
        //        return Integer.parseInt(versionString.replace(".", ""));
    }

    public static String getOsArch() {
        return System.getProperty("os.arch");
    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static boolean isLinux() {
        final String os = getOsName().toLowerCase();

        return os.contains("linux");
    }

    public static boolean isUnix() {
        final String os = getOsName().toLowerCase();

        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    public static boolean isWindows() {
        final String os = getOsName().toLowerCase();

        return os.startsWith("win");
    }

    private SystemUtils() {
        super();
    }
}
