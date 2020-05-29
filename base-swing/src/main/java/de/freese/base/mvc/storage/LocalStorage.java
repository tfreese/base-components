package de.freese.base.mvc.storage;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.activation.DataSource;
import org.apache.commons.io.IOUtils;
import de.freese.base.utils.ByteArrayDataSource;

/**
 * Klasse fuer lokalen Dateizugriff.<br>
 * Alle Operationen finden unterhalb des User-Verzeichnisses statt.
 *
 * @author Thomas Freese
 */
public final class LocalStorage
{
    /**
     *
     */
    private Path directory = null;

    /**
     * Erstellt ein neues {@link LocalStorage} Object.
     */
    public LocalStorage()
    {
        super();
    }

    /**
     * Kopiert den {@link InputStream} in den {@link OutputStream}.<br>
     * Die Streams werden NICHT geschlossen.
     *
     * @param src {@link InputStream}
     * @param dest {@link OutputStream}
     * @throws Exception Falls was schief geht.
     */
    public synchronized void copy(final InputStream src, final OutputStream dest) throws Exception
    {
        IOUtils.copy(src, dest);
        // if ((dest instanceof FileOutputStream) && (src instanceof FileInputStream))
        // {
        // FileChannel target = ((FileOutputStream) dest).getChannel();
        // FileChannel source = ((FileInputStream) src).getChannel();
        //
        // source.transferTo(0, Long.MAX_VALUE, target);
        //
        // source.close();
        // target.close();
        //
        // return;
        // }
        //
        // byte[] buf = new byte[4096];
        //
        // while (true)
        // {
        // int length = src.read(buf);
        //
        // if (length < 0)
        // {
        // break;
        // }
        //
        // dest.write(buf, 0, length);
        // }
    }

    /**
     * Erzeugt das Verzeichniss, inlusive der Parent-Verzeichnisse.
     *
     * @param path {@link Path}
     */
    private void createDirectories(final Path path)
    {
        if (!Files.exists(path))
        {
            try
            {
                Files.createDirectories(path);
            }
            catch (IOException ex)
            {
                throw new UncheckedIOException(ex);
            }
        }
    }

    /**
     * Erzeugt eine temporaere Datei im BasisVerzeichnis
     *
     * @param prefix String
     * @param suffix String
     * @return {@link File}
     * @throws IOException Falls was schief geht.
     */
    public File createTemporaryFile(final String prefix, final String suffix) throws IOException
    {
        Path path = Files.createTempFile(getDirectory(), prefix, suffix);
        File file = path.toFile();

        // File file = File.createTempFile(prefix, suffix, getDirectory().toFile());
        file.deleteOnExit();

        return file;
    }

    /**
     * Loescht rekursiv ein Verzeichnis inkl. aller Dateien. Gefaehrliche Methode, deswegen private !!!
     *
     * @param path {@link Path}
     * @return true, wenn erfolgreich geloescht
     * @throws IOException Falls was schief geht.
     */
    private boolean deleteDirectory(final Path path) throws IOException
    {
        if (Files.isDirectory(path))
        {
            // String[] children = path.toFile().list();
            //
            // for (String element : children)
            // {
            // boolean success = deleteDirectory(new File(path, element));
            //
            // if (!success)
            // {
            // return false;
            // }
            // }
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                /**
                 * @see java.nio.file.SimpleFileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
                 */
                @Override
                public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

                /**
                 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
                 */
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
                {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        return Files.deleteIfExists(path);
    }

    /**
     * Liefert true, wenn das Verzeichniss geloescht wurde.
     *
     * @param fileName String, wenn "" wird das Basisverzeichniss geloescht
     * @return boolean
     * @throws IOException Falls was schief geht.
     */
    public boolean deleteDirectory(final String fileName) throws IOException
    {
        validateFileName(fileName);

        Path path = getDirectory().resolve(fileName);

        return deleteDirectory(path);
    }

    /**
     * Liefert true, wenn die Datei geloescht wurde.
     *
     * @param fileName String
     * @return boolean
     * @throws IOException Falls was schief geht.
     */
    public boolean deleteFile(final String fileName) throws IOException
    {
        Path path = getPath(fileName);

        Files.delete(path);

        return true;
    }

    /**
     * Basisverzeichniss.
     *
     * @return {@link Path}
     */
    public Path getDirectory()
    {
        if (this.directory == null)
        {
            String userHome = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("user.home"));

            if (userHome == null)
            {
                throw new IllegalStateException("user.home is null");
            }

            Path path = Paths.get(userHome, ".java-apps", "unknown");

            // OS os = ApplicationContext.getOS();
            // if (os.name().toLowerCase().startsWith("windows"))
            // if (SystemUtils.IS_OS_WINDOWS)
            // {
            // String appData = System.getenv("APPDATA");
            //
            // if (appData != null)
            // {
            // path.append(appData).append(separator);
            // }
            // else
            // {
            // path.append(userHome).append(separator);
            // path.append("Application Data").append(separator);
            // }
            // }
            // else if (SystemUtils.IS_OS_MAC_OSX)
            // {
            // path.append(userHome).append(separator);
            // path.append("Library/Application Support").append(separator);
            // }
            // else if (SystemUtils.IS_OS_LINUX)
            // {
            // path.append(userHome).append(separator);
            // path.append(".");
            // }
            // else
            // {
            // throw new IllegalStateException("unsupported operating system");
            // }

            this.directory = path;

            createDirectories(this.directory);
            // File dir = new File(this.directory);
            //
            // if (!dir.isDirectory())
            // {
            // if (!dir.mkdirs())
            // {
            // throw new RuntimeException("couldn't create directory " + dir);
            // }
            // }
        }

        return this.directory;
    }

    /**
     * Liefert den InputStream einer Datei.<br>
     * If no options are present then it is equivalent to opening the file with the {@link StandardOpenOption#READ READ} option
     *
     * @param fileName String, relativ zum Basisverzeichnis
     * @param options {@link OpenOption}[]
     * @return {@link InputStream}
     * @throws IOException Falls was schief geht.
     */
    public InputStream getInputStream(final String fileName, final OpenOption...options) throws IOException
    {
        validateFileName(fileName);

        Path path = getDirectory().resolve(fileName);

        createDirectories(path.getParent());

        return new BufferedInputStream(Files.newInputStream(path, options));
    }

    /**
     * Liefert den {@link OutputStream} einer Datei.<br>
     * If no options are present then this method works as if the {@link StandardOpenOption#CREATE CREATE}, {@link StandardOpenOption#TRUNCATE_EXISTING
     * TRUNCATE_EXISTING}, and {@link StandardOpenOption#WRITE WRITE} options are present.
     *
     * @param fileName {@link String}, relativ zum Basisverzeichnis
     * @param options {@link OpenOption}[]
     * @return {@link OutputStream}
     * @throws IOException Falls was schief geht.
     */
    public OutputStream getOutputStream(final String fileName, final OpenOption...options) throws IOException
    {
        validateFileName(fileName);

        Path path = getDirectory().resolve(fileName);

        createDirectories(path.getParent());

        return new BufferedOutputStream(Files.newOutputStream(path, options));
    }

    /**
     * Liefert das Path Objekt des Datei-Namens.
     *
     * @param fileName {@link Path}
     * @return {@link File}
     */
    public Path getPath(final String fileName)
    {
        validateFileName(fileName);

        return getDirectory().resolve(fileName);
    }

    /**
     * Öffnet eine Datei mit dem Default Systemprogramm.
     *
     * @param file {@link Path}, relativ zum Basisverzeichnis
     * @throws Exception Falls was schief geht.
     */
    public void openPath(final Path file) throws Exception
    {
        Desktop.getDesktop().open(getDirectory().resolve(file).toFile());
    }

    /**
     * Entfernt illegale Zeichen im Dateinamen.
     *
     * @param fileName String
     * @return String
     */
    public String removeIllegalChars(final String fileName)
    {
        validateFileName(fileName);

        String internalName = fileName;

        if (internalName.indexOf('/') != -1)
        {
            internalName = internalName.replaceAll("[//]", "-");
        }

        if (internalName.indexOf('\\') != -1)
        {
            internalName = internalName.replaceAll("[\\\\]", "-");
        }

        return internalName;
    }

    /**
     * Speichert die {@link DataSource} in das temp. Verzeichnis.
     *
     * @param dataSource {@link DataSource}
     * @return String ggf. korrogierter Dateiname
     * @throws Exception Falls was schief geht.
     * @see DataSource#getName()
     * @see DataSource#getInputStream()
     */
    public synchronized String save(final DataSource dataSource) throws Exception
    {
        String fileName = dataSource.getName();
        validateFileName(fileName);
        fileName = removeIllegalChars(fileName);

        Path path = getDirectory().resolve(fileName);

        createDirectories(path.getParent());

        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
             InputStream inputStream = dataSource.getInputStream())
        {
            copy(inputStream, outputStream);
        }

        return fileName;
    }

    /**
     * Speichert das byte[] in das temp. Verzeichnis.
     *
     * @param fileName String, der relative Dateiname
     * @param data byte[]
     * @return String absoluter Dateiname
     * @throws Exception Falls was schief geht.
     */
    public synchronized String save(final String fileName, final byte[] data) throws Exception
    {
        ByteArrayDataSource dataSource = new ByteArrayDataSource(data, ByteArrayDataSource.MIMETYPE_APPLICATION_OCTET_STREAM);
        dataSource.setName(fileName);

        return save(dataSource);
    }

    /**
     * Speichert den {@link InputStream} in das temp. Verzeichnis.
     *
     * @param fileName String, der relative Dateiname
     * @param inputStream {@link InputStream}
     * @return String absoluter Dateiname
     * @throws Exception Falls was schief geht.
     */
    public synchronized String saveTemp(final String fileName, final InputStream inputStream) throws Exception
    {
        ByteArrayDataSource dataSource = new ByteArrayDataSource(inputStream, ByteArrayDataSource.MIMETYPE_APPLICATION_OCTET_STREAM);
        dataSource.setName(fileName);

        return save(dataSource);
    }

    /**
     * Basisverzeichniss.
     *
     * @param directory Path
     */
    public void setDirectory(final Path directory)
    {
        this.directory = directory;

        createDirectories(this.directory);
    }

    /**
     * @param fileName String
     */
    private void validateFileName(final String fileName)
    {
        String internalName = fileName;

        if (internalName == null)
        {
            throw new NullPointerException("Filename");
        }

        if (internalName.trim().length() == 0)
        {
            throw new IllegalArgumentException("Filename is empty");
        }

        if (internalName.indexOf('.') == -1)
        {
            throw new IllegalArgumentException("Filename dont have extension");
        }
    }
}
