package de.freese.base.resourcemap.scanner;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResourceScanner fuer Property-Dateien ueber den gesamten ClassPath mittels Regulaeren
 * Ausdruecken.
 * 
 * @author Thomas Freese
 */
public class DefaultResourceScanner implements ResourceScanner
{
	/**
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResourceScanner.class);

	/**
	 * Erstellt ein neues {@link DefaultResourceScanner} Object.
	 */
	public DefaultResourceScanner()
	{
		super();
	}

	/**
	 * Durchsucht die Resourcen eines ClassPath-Elements.
	 * 
	 * @param pattern {@link Pattern}
	 * @return {@link Set}
	 */
	private Set<String> getResources(final String pattern)
	{
		Pattern regExPattern = Pattern.compile(pattern);

		String classPath = System.getProperty("java.class.path", ".");
		String pathSeparator = System.getProperty("path.separator", ";");

		String[] classPathElements = classPath.split(pathSeparator);
		Set<String> resources = new HashSet<>();

		for (String element : classPathElements)
		{
			File file = new File(element);

			if (file.isDirectory())
			{
				resources.addAll(getResourcesFromDirectory(file, regExPattern));
			}
			else
			{
				resources.addAll(getResourcesFromJarFile(file, regExPattern));
			}
		}

		return resources;
	}

	/**
	 * Durchsucht rekursiv die Dateien eines Verzeichnisses.
	 * 
	 * @param directory {@link File}
	 * @param pattern {@link Pattern}
	 * @return {@link Set}
	 */
	private Set<String> getResourcesFromDirectory(final File directory, final Pattern pattern)
	{
		Set<String> resources = new HashSet<>();
		File[] fileList = directory.listFiles();

		for (File file : fileList)
		{
			if (file.isDirectory())
			{
				resources.addAll(getResourcesFromDirectory(file, pattern));
			}
			else
			{
				try
				{
					String fileName = file.getCanonicalPath();
					boolean accept = pattern.matcher(fileName).matches();

					if (accept)
					{
						resources.add(fileName);
					}
				}
				catch (IOException ex)
				{
					DefaultResourceScanner.LOGGER.error(null, ex);
				}
			}
		}

		return resources;
	}

	/**
	 * Durchsucht den Inhalt einer JAR-Datei.
	 * 
	 * @param file {@link File}
	 * @param pattern {@link Pattern}
	 * @return {@link Set}
	 */
	@SuppressWarnings("resource")
	private Set<String> getResourcesFromJarFile(final File file, final Pattern pattern)
	{
		Set<String> resources = new HashSet<>();
		ZipFile zf = null;

		try
		{
			zf = new ZipFile(file);
		}
		catch (ZipException ex)
		{
			LOGGER.error(null, ex);
		}
		catch (IOException ex)
		{
			LOGGER.error(null, ex);
		}

		if (zf == null)
		{
			return resources;
		}

		Enumeration<? extends ZipEntry> e = zf.entries();

		while (e.hasMoreElements())
		{
			ZipEntry ze = e.nextElement();
			String fileName = ze.getName();
			// LOGGER.info(fileName);

			boolean accept = pattern.matcher(fileName).matches();

			if (accept)
			{
				resources.add(fileName);
			}
		}

		try
		{
			zf.close();
		}
		catch (IOException ex)
		{
			DefaultResourceScanner.LOGGER.error(null, ex);
		}

		return resources;
	}

	/**
	 * Versucht alle Property-Dateien im Package zu finden und setzt diese als BundleBaseNames.<br>
	 * Die Packages duerfen im bin-, META_INF- oder im Jar-Ordner liegen.
	 * 
	 * @see de.freese.base.resourcemap.scanner.ResourceScanner#scanResources(java.lang.String)
	 */
	@Override
	public Set<String> scanResources(final String basePath)
	{
		String searchPath = basePath == null ? "" : basePath;

		if (searchPath.length() > 0)
		{
			// Package kann durch / oder \ getrennt sein
			searchPath = searchPath.replace("/", "[/\\\\]");
			searchPath = "(" + searchPath + ")";
		}

		// Packages duerfen im bin-, META_INF- oder im Jar-Ordner liegen
		String folderRegex = "(.*bin[/\\\\]|^META-INF[/\\\\]|^)";

		// Properties-Struktur
		String propertyRegex = ".[^/\\\\]+(_[a-z]{2})?(_[A-Z]{2})?\\.properties$";

		// Nach dem Package darf kein / oder \ Zeichen kommen
		String pathRegex = searchPath.length() > 0 ? searchPath + "[/\\\\]" : searchPath;

		// Alles zusammenbauen
		String resourceRegex = folderRegex + pathRegex + propertyRegex;

		Set<String> resources = getResources(resourceRegex);

		if (resources.isEmpty())
		{
			DefaultResourceScanner.LOGGER.error("No Bundles in Path \"{}\"", basePath);

			return Collections.emptySet();
		}

		// Fuer ResourceBundle normalisieren
		Set<String> bundleNames = new HashSet<>();

		for (String resource : resources)
		{
			// Den reinen Dateinamen rausfummeln, Unterstrich beruecksichtigen
			String[] splits = resource.split(folderRegex);
			splits = splits[splits.length - 1].split("(_+[a-zA-Z]{2}|\\.properties$)");

			bundleNames.add(splits[0]);
		}

		return bundleNames;
	}
}
