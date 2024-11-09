package de.freese.base.resourcemap.scanner;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResourceScanner für Property-Dateien über den gesamten ClassPath mittels regulären Ausdrücken.
 *
 * @author Thomas Freese
 */
public class DefaultResourceScanner implements ResourceScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResourceScanner.class);

    /**
     * Versucht alle Property-Dateien im Package zu finden und setzt diese als BundleBaseNames.<br>
     * Die Packages dürfen im bin-, META_INF- oder im Jar-Ordner liegen.
     */
    @Override
    public Set<String> scanResources(final String basePath) {
        String searchPath = basePath == null ? "" : basePath;

        if (!searchPath.isEmpty()) {
            // Package kann durch / oder \ getrennt sein
            searchPath = searchPath.replace("/", "[/\\\\]");
            searchPath = "(" + searchPath + ")";
        }

        // Packages dürfen im bin-, META_INF- oder im Jar-Ordner liegen
        final String folderRegex = "(.*bin[/\\\\]|^META-INF[/\\\\]|^)";

        // Properties-Struktur
        final String propertyRegex = ".[^/\\\\]+(_[a-z]{2})?(_[A-Z]{2})?\\.properties$";

        // Nach dem Package darf kein / oder \ Zeichen kommen
        final String pathRegex = !searchPath.isEmpty() ? searchPath + "[/\\\\]" : searchPath;

        // Alles zusammenbauen
        final String resourceRegex = folderRegex + pathRegex + propertyRegex;

        final Set<String> resources = getResources(resourceRegex);

        if (resources.isEmpty()) {
            DefaultResourceScanner.LOGGER.error("No Bundles in Path \"{}\"", basePath);

            return Collections.emptySet();
        }

        // Für ResourceBundle normalisieren
        final Set<String> bundleNames = new HashSet<>();

        for (String resource : resources) {
            // Den reinen Dateinamen raus fummeln, Unterstrich berücksichtigen
            String[] splits = resource.split(folderRegex);
            splits = splits[splits.length - 1].split("(_+[a-zA-Z]{2}|\\.properties$)");

            bundleNames.add(splits[0]);
        }

        return bundleNames;
    }

    /**
     * Durchsucht die Ressourcen eines ClassPath-Elements.
     */
    private Set<String> getResources(final String pattern) {
        final Pattern regExPattern = Pattern.compile(pattern);

        final String classPath = System.getProperty("java.class.path", ".");
        final String pathSeparator = System.getProperty("path.separator", ";");

        final String[] classPathElements = classPath.split(pathSeparator);
        final Set<String> resources = new HashSet<>();

        for (String element : classPathElements) {
            final File file = new File(element);

            if (file.isDirectory()) {
                resources.addAll(getResourcesFromDirectory(file, regExPattern));
            }
            else {
                resources.addAll(getResourcesFromJarFile(file, regExPattern));
            }
        }

        return resources;
    }

    /**
     * Durchsucht rekursiv die Dateien eines Verzeichnisses.
     */
    private Set<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
        final Set<String> resources = new HashSet<>();
        final File[] fileList = directory.listFiles();

        for (File file : fileList) {
            if (file.isDirectory()) {
                resources.addAll(getResourcesFromDirectory(file, pattern));
            }
            else {
                try {
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();

                    if (accept) {
                        resources.add(fileName);
                    }
                }
                catch (IOException ex) {
                    DefaultResourceScanner.LOGGER.error(ex.getMessage(), ex);
                }
            }
        }

        return resources;
    }

    /**
     * Durchsucht den Inhalt einer JAR-Datei.
     */
    private Set<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
        final Set<String> resources = new HashSet<>();
        ZipFile zf = null;

        try {
            zf = new ZipFile(file);
        }
        catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        if (zf == null) {
            return resources;
        }

        final Enumeration<? extends ZipEntry> e = zf.entries();

        while (e.hasMoreElements()) {
            final ZipEntry ze = e.nextElement();
            final String fileName = ze.getName();
            // LOGGER.info(fileName);

            final boolean accept = pattern.matcher(fileName).matches();

            if (accept) {
                resources.add(fileName);
            }
        }

        try {
            zf.close();
        }
        catch (IOException ex) {
            DefaultResourceScanner.LOGGER.error(ex.getMessage(), ex);
        }

        return resources;
    }
}
