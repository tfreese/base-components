// Created: 22.04.2020
package de.freese.base.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * See org.springframework.util.ClassUtils.
 *
 * @author Thomas Freese
 */
public final class ClassUtils {

    /**
     * Returns all Resources find by the ClassLoader in this Path, non-recursive.
     */
    public static List<Path> findResources(final String path) throws IOException, URISyntaxException {
        final List<Path> resources = new ArrayList<>();

        final Enumeration<URL> enumeration = Thread.currentThread().getContextClassLoader().getResources(path);

        while (enumeration.hasMoreElements()) {
            final URL url = enumeration.nextElement();

            try (FileSystem fileSystem = FileSystems.newFileSystem(url.toURI(), Map.of())) {
                final Path pathImages = fileSystem.getPath("images");

                try (Stream<Path> stream = Files.walk(pathImages, 1)) {
                    stream.forEach(resources::add);
                }
            }
        }

        return resources;
    }

    /**
     * Returns all Classes in the Package and Sub-Packages.<br>
     * Doesn't work for Runtime-Packages.
     */
    public static Set<Class<?>> getClasses(final String packageName) throws ClassNotFoundException, IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        final String packagePath = packageName.replace('.', '/');
        final Enumeration<URL> resources = classLoader.getResources(packagePath);

        final List<String> fileNames = new ArrayList<>();

        while (resources.hasMoreElements()) {
            final URL resource = resources.nextElement();
            fileNames.add(resource.getFile());
        }

        final Set<Class<?>> classes = new HashSet<>();

        for (String fileName : fileNames) {
            if (fileName.contains(".jar!")) {
                final URI jarFileURI = URI.create(fileName.substring(0, fileName.indexOf(".jar!") + 4));

                classes.addAll(findClassesInJar(Paths.get(jarFileURI), packagePath, classLoader));
            }
            else {
                classes.addAll(findClassesInFolder(new File(fileName), packageName));
            }
        }

        return classes.stream().filter(clazz -> !clazz.isInterface()).sorted(Comparator.comparing(Class::getName)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Search in Directories if runs in an IDE.<br>
     * Doesn't work for Runtime-Packages.
     */
    private static Set<Class<?>> findClassesInFolder(final File directory, final String packageName) throws ClassNotFoundException {
        final Set<Class<?>> classes = new HashSet<>();

        if (!directory.exists()) {
            return classes;
        }

        final File[] files = directory.listFiles();

        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClassesInFolder(file, packageName + "." + file.getName()));
            }
            else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }

        return classes;
    }

    /**
     * Search in Jars if runs in Deploy-Environment.<br>
     * Doesn't work for Runtime-Packages.
     */
    private static Set<Class<?>> findClassesInJar(final Path path, final String packagePath, final ClassLoader classLoader) throws ClassNotFoundException, IOException {
        final Set<String> files = new HashSet<>();

        try (FileSystem fileSystem = FileSystems.newFileSystem(path, classLoader)) {
            try (Stream<Path> paths = Files.walk(fileSystem.getPath(packagePath))) {
                paths.map(Path::toString).filter(p -> p.endsWith(".class")).map(p -> p.replace("/", ".")).forEach(files::add);
            }
        }

        final Set<Class<?>> classes = new HashSet<>();

        for (String file : files) {
            classes.add(Class.forName(file.substring(0, file.length() - 6)));
        }

        return classes;
    }

    private ClassUtils() {
        super();
    }
}
