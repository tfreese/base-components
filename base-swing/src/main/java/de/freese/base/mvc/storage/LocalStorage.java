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
import java.util.Objects;

import jakarta.activation.DataSource;

import de.freese.base.utils.ByteArrayDataSource;

/**
 * Klasse für lokalen Dateizugriff.<br>
 * Alle Operationen finden unterhalb des User-Verzeichnisses statt.
 *
 * @author Thomas Freese
 */
public final class LocalStorage
{
    private final Path storageDirectory;

    public LocalStorage()
    {
        this(Paths.get(System.getProperty("java.io.tmpdir"), ".java-apps"));
    }

    public LocalStorage(final Path storageDirectory)
    {
        super();

        this.storageDirectory = Objects.requireNonNull(storageDirectory, "storageDirectory required");
        createDirectories(storageDirectory);
    }

    public void copy(final InputStream src, final OutputStream target) throws IOException
    {
        src.transferTo(target);
    }

    public File createTemporaryFile(final String prefix, final String suffix) throws IOException
    {
        Path path = Files.createTempFile(getStorageDirectory(), prefix, suffix);
        File file = path.toFile();

        file.deleteOnExit();

        return file;
    }

    public boolean deleteDirectory(final String fileName) throws IOException
    {
        validateFileName(fileName);

        Path path = getStorageDirectory().resolve(fileName);

        return deleteDirectory(path);
    }

    public boolean deleteFile(final String fileName) throws IOException
    {
        Path path = getPath(fileName);

        Files.delete(path);

        return true;
    }

    public InputStream getInputStream(final String fileName, final OpenOption... options) throws IOException
    {
        validateFileName(fileName);

        Path path = getStorageDirectory().resolve(fileName);

        createDirectories(path.getParent());

        return new BufferedInputStream(Files.newInputStream(path, options));
    }

    public OutputStream getOutputStream(final String fileName, final OpenOption... options) throws IOException
    {
        validateFileName(fileName);

        Path path = getStorageDirectory().resolve(fileName);

        createDirectories(path.getParent());

        return new BufferedOutputStream(Files.newOutputStream(path, options));
    }

    public Path getPath(final String fileName)
    {
        validateFileName(fileName);

        return getStorageDirectory().resolve(fileName);
    }

    public Path getStorageDirectory()
    {
        //        if (this.directory == null)
        //        {
        //            String userHome = System.getProperty("user.home");
        //
        //            if (userHome == null)
        //            {
        //                throw new IllegalStateException("user.home is null");
        //            }
        //
        //            Path path = Paths.get(userHome, ".java-apps");
        //
        //            // OS os = ApplicationContext.getOS();
        //            // if (os.name().toLowerCase().startsWith("windows"))
        //            // if (SystemUtils.IS_OS_WINDOWS)
        //            // {
        //            // String appData = System.getenv("APPDATA");
        //            //
        //            // if (appData != null)
        //            // {
        //            // path.append(appData).append(separator);
        //            // }
        //            // else
        //            // {
        //            // path.append(userHome).append(separator);
        //            // path.append("Application Data").append(separator);
        //            // }
        //            // }
        //            // else if (SystemUtils.IS_OS_MAC_OSX)
        //            // {
        //            // path.append(userHome).append(separator);
        //            // path.append("Library/Application Support").append(separator);
        //            // }
        //            // else if (SystemUtils.IS_OS_LINUX)
        //            // {
        //            // path.append(userHome).append(separator);
        //            // path.append(".");
        //            // }
        //            // else
        //            // {
        //            // throw new IllegalStateException("unsupported operating system");
        //            // }
        //
        //            this.directory = path;
        //
        //            createDirectories(this.directory);
        //        }

        return this.storageDirectory;
    }

    public void openPath(final Path file) throws IOException
    {
        Desktop.getDesktop().open(getStorageDirectory().resolve(file).toFile());
    }

    public String removeIllegalFileCharacters(final String fileName)
    {
        validateFileName(fileName);

        String internalName = fileName;

        if (internalName.indexOf('/') != -1)
        {
            internalName = internalName.replace("//", "/").replace("/", "-");
        }

        if (internalName.indexOf('\\') != -1)
        {
            internalName = internalName.replace("\\\\", "\\").replace("\\", "-");
        }

        return internalName;
    }

    public String save(final DataSource dataSource) throws IOException
    {
        String fileName = dataSource.getName();
        validateFileName(fileName);
        fileName = removeIllegalFileCharacters(fileName);

        Path path = getStorageDirectory().resolve(fileName);

        createDirectories(path.getParent());

        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
             InputStream inputStream = dataSource.getInputStream())
        {
            copy(inputStream, outputStream);
        }

        return fileName;
    }

    public String save(final String fileName, final byte[] data) throws Exception
    {
        ByteArrayDataSource dataSource = new ByteArrayDataSource(data, ByteArrayDataSource.MIMETYPE_APPLICATION_OCTET_STREAM);
        dataSource.setName(fileName);

        return save(dataSource);
    }

    public String saveTemp(final String fileName, final InputStream inputStream) throws Exception
    {
        ByteArrayDataSource dataSource = new ByteArrayDataSource(inputStream, ByteArrayDataSource.MIMETYPE_APPLICATION_OCTET_STREAM);
        dataSource.setName(fileName);

        return save(dataSource);
    }

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

    private boolean deleteDirectory(final Path path) throws IOException
    {
        if (Files.isDirectory(path))
        {
            Files.walkFileTree(path, new SimpleFileVisitor<>()
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

    private void validateFileName(final String fileName)
    {
        if (fileName == null)
        {
            throw new NullPointerException("Filename");
        }

        if (fileName.strip().length() == 0)
        {
            throw new IllegalArgumentException("Filename is empty");
        }

        if (fileName.indexOf('.') == -1)
        {
            throw new IllegalArgumentException("Filename don't have extension");
        }
    }
}
