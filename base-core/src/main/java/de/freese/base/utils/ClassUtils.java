// Created: 22.04.2020
package de.freese.base.utils;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Geklaut von org.springframework.util.ClassUtils.
 *
 * @author Thomas Freese
 */
public final class ClassUtils {
    /**
     * Suffix for array class names: {@code "[]"}.
     */
    public static final String ARRAY_SUFFIX = "[]";
    /**
     * The CGLIB class separator: {@code "$$"}.
     */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    /**
     * Map with common Java language class name as key and corresponding Class as value. Primarily for efficient deserialization of remote invocations.
     */
    private static final Map<String, Class<?>> COMMON_CLASS_CACHE = new HashMap<>(64);
    /**
     * The inner class separator character: {@code '$'}.
     */
    private static final char INNER_CLASS_SEPARATOR = '$';
    /**
     * Prefix for internal array class names: {@code "["}.
     */
    private static final String INTERNAL_ARRAY_PREFIX = "[";
    /**
     * Common Java language interfaces which are supposed to be ignored when searching for 'primary' user-level interfaces.
     */
    private static final Set<Class<?>> JAVA_LANGUAGE_INTERFACES;
    /**
     * Prefix for internal non-primitive array class names: {@code "[L"}.
     */
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
    /**
     * The package separator character: {@code '.'}.
     */
    private static final char PACKAGE_SEPARATOR = '.';
    /**
     * Map with primitive type name as key and corresponding primitive type as value, for example: "int" -> "int.class".
     */
    private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new HashMap<>(32);
    /**
     * Map with primitive type as key and corresponding wrapper type as value, for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_TO_WRAPPER_MAP = new IdentityHashMap<>(8);
    /**
     * Map with primitive wrapper type as key and corresponding primitive type as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_TYPE_MAP = new IdentityHashMap<>(8);

    static {
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Character.class, char.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Double.class, double.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Float.class, float.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Integer.class, int.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Long.class, long.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Short.class, short.class);
        PRIMITIVE_WRAPPER_TYPE_MAP.put(Void.class, void.class);

        // Map entry iteration is less expensive to initialize than forEach with lambdas
        for (Map.Entry<Class<?>, Class<?>> entry : PRIMITIVE_WRAPPER_TYPE_MAP.entrySet()) {
            PRIMITIVE_TYPE_TO_WRAPPER_MAP.put(entry.getValue(), entry.getKey());
            registerCommonClasses(entry.getKey());
        }

        final Set<Class<?>> primitiveTypes = new HashSet<>(32);
        primitiveTypes.addAll(PRIMITIVE_WRAPPER_TYPE_MAP.values());
        Collections.addAll(primitiveTypes, boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class);
        primitiveTypes.add(void.class);

        for (Class<?> primitiveType : primitiveTypes) {
            PRIMITIVE_TYPE_NAME_MAP.put(primitiveType.getName(), primitiveType);
        }

        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Class.class, Class[].class, Object.class, Object[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class, StackTraceElement.class, StackTraceElement[].class);
        registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class, Collection.class, List.class, Set.class, Map.class, Map.Entry.class, Optional.class);

        final Class<?>[] javaLanguageInterfaceArray = {Serializable.class, Externalizable.class, Closeable.class, AutoCloseable.class, Cloneable.class, Comparable.class};

        registerCommonClasses(javaLanguageInterfaceArray);
        JAVA_LANGUAGE_INTERFACES = new HashSet<>(Arrays.asList(javaLanguageInterfaceArray));
    }

    /**
     * Replacement for {@code Class.forName()} that also returns Class instances for primitives (e.g. "int") and array class names (e.g. "String[]").
     * Furthermore, it is also capable of resolving inner class names in Java source style (e.g. "java.lang.Thread.State" instead of "java.lang.Thread$State").
     *
     * @param name the name of the Class
     * @param classLoader the class loader to use (maybe {@code null}, which indicates the default class loader)
     *
     * @return a class instance for the supplied name
     *
     * @throws ClassNotFoundException if the class was not found
     * @throws LinkageError if the class file could not be loaded
     */
    public static Class<?> forName(final String name, final ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Objects.requireNonNull(name, "name required");

        Class<?> clazz = resolvePrimitiveClassName(name);

        if (clazz == null) {
            clazz = COMMON_CLASS_CACHE.get(name);
        }

        if (clazz != null) {
            return clazz;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            final String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            final Class<?> elementClass = forName(elementClassName, classLoader);

            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            final String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            final Class<?> elementClass = forName(elementName, classLoader);

            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[[I" or "[[Ljava.lang.String;" style arrays
        if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            final String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            final Class<?> elementClass = forName(elementName, classLoader);

            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader clToUse = classLoader;

        if (clToUse == null) {
            clToUse = getDefaultClassLoader();
        }

        try {
            return Class.forName(name, false, clToUse);
        }
        catch (ClassNotFoundException ex) {
            final int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);

            if (lastDotIndex != -1) {
                final String innerClassName = name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR + name.substring(lastDotIndex + 1);

                try {
                    return Class.forName(innerClassName, false, clToUse);
                }
                catch (ClassNotFoundException ex2) {
                    // Swallow - let original exception get through
                }
            }

            throw ex;
        }
    }

    /**
     * Return the default ClassLoader to use: typically the thread context ClassLoader, if available; the ClassLoader that loaded the ClassUtils class will be
     * used as fallback.<br>
     * <br>
     * Call this method if you intend to use the thread context ClassLoader in a scenario where you clearly prefer a non-null ClassLoader reference: for
     * example, for class path resource loading (but not necessarily for {@code Class.forName}, which accepts a {@code null} ClassLoader reference as well).
     *
     * @return the default ClassLoader (only {@code null} if even the system ClassLoader isn't accessible)
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader classLoader = null;

        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (Exception ex) {
            // Cannot access thread context ClassLoader - falling back...
        }

        if (classLoader == null) {
            // No thread context class loader -> use class loader of this class.
            try {
                classLoader = ClassUtils.class.getClassLoader();
            }
            catch (Exception ex) {
                // Cannot access class loader of this class.
            }
        }

        if (classLoader == null) {
            // getClassLoader() returning null indicates the bootstrap ClassLoader
            try {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            catch (Exception ex) {
                // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
            }
        }

        return classLoader;
    }

    /**
     * Return the qualified name of the given class: usually simply the class name, but component type class name + "[]" for arrays.
     */
    public static String getQualifiedName(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz required");

        return clazz.getTypeName();
    }

    /**
     * Get the class name without the qualified package name.
     */
    public static String getShortName(final Class<?> clazz) {
        return getShortName(getQualifiedName(clazz));
    }

    /**
     * Get the class name without the qualified package name.
     *
     * @throws IllegalArgumentException if the className is empty
     */
    public static String getShortName(final String className) {
        Objects.requireNonNull(className, "className required");

        final int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);

        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }

        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);

        return shortName;
    }

    /**
     * Check if the right-hand side type may be assigned to the left-hand side type, assuming setting by reflection. Considers primitive wrapper classes as
     * assignable to the corresponding primitive types.
     *
     * @param lhsType the target type
     * @param rhsType the value type that should be assigned to the target type
     *
     * @return if the target type is assignable from the value type
     */
    public static boolean isAssignable(final Class<?> lhsType, final Class<?> rhsType) {
        Objects.requireNonNull(lhsType, "lhsType required");
        Objects.requireNonNull(rhsType, "rhsType required");

        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }

        if (lhsType.isPrimitive()) {
            final Class<?> resolvedPrimitive = PRIMITIVE_WRAPPER_TYPE_MAP.get(rhsType);

            return (lhsType == resolvedPrimitive);
        }

        final Class<?> resolvedWrapper = PRIMITIVE_TYPE_TO_WRAPPER_MAP.get(rhsType);

        return ((resolvedWrapper != null) && lhsType.isAssignableFrom(resolvedWrapper));
    }

    /**
     * Determine if the supplied class is an <em>inner class</em>, i.e. a non-static member of an enclosing class.
     *
     * @return {@code true} if the supplied class is an inner class
     */
    public static boolean isInnerClass(final Class<?> clazz) {
        return (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers()));
    }

    /**
     * Determine whether the given interface is a common Java language interface: {@link Serializable}, {@link Externalizable}, {@link Closeable},
     * {@link AutoCloseable}, {@link Cloneable}, {@link Comparable} - all of which can be ignored when looking for 'primary' user-level interfaces. Common
     * characteristics: no service-level operations, no bean property methods, no default methods.
     *
     * @param ifc the interface to check
     */
    public static boolean isJavaLanguageInterface(final Class<?> ifc) {
        return JAVA_LANGUAGE_INTERFACES.contains(ifc);
    }

    /**
     * Resolve the given class name as primitive class, if appropriate, according to the JVM's naming rules for primitive classes.<br>
     * <br>
     * Also supports the JVM's internal class names for primitive arrays. Does <i>not</i> support the "[]" suffix notation for primitive arrays; this is only
     * supported by {@link #forName(String, ClassLoader)}.
     *
     * @param name the name of the potentially primitive class
     *
     * @return the primitive class, or {@code null} if the name does not denote a primitive class or primitive array class
     */
    public static Class<?> resolvePrimitiveClassName(final String name) {
        Class<?> result = null;

        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if ((name != null) && (name.length() <= 7)) {
            // Could be a primitive - likely.
            result = PRIMITIVE_TYPE_NAME_MAP.get(name);
        }

        return result;
    }

    /**
     * Register the given common classes with the ClassUtils cache.
     */
    private static void registerCommonClasses(final Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            COMMON_CLASS_CACHE.put(clazz.getName(), clazz);
        }
    }

    private ClassUtils() {
        super();
    }
}
